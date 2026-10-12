package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.entity.CelebrityEntity
import com.example.data.local.entity.CompactScanEntity
import com.example.data.local.entity.UserVoteEntity
import com.example.data.model.Celebrity
import com.example.data.model.CelebrityCatalog
import com.example.data.model.CelebrityCategory
import com.example.data.model.PublicMetricsCalculator
import com.example.data.remote.WikipediaApiClient
import com.example.data.remote.WikipediaPageSummary
import com.example.data.remote.WikipediaSearchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

data class StorageStats(
    val celebrityCount: Int,
    val maxCelebrityLimit: Int = 1000,
    val totalScanSnapshots: Int,
    val estimatedStorageBytes: Long
)

class PulseRepository(
    context: Context,
    private val database: AppDatabase = AppDatabase.getDatabase(context),
    private val wikiClient: WikipediaApiClient = WikipediaApiClient(),
    private val appScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    private val celebrityDao = database.celebrityDao()
    private val compactScanDao = database.compactScanDao()
    private val userVoteDao = database.userVoteDao()

    val celebrities: Flow<List<Celebrity>> = celebrityDao.getAllCelebrities().map { entities ->
        entities.map { it.toDomain() }
    }

    val userVotes: Flow<List<UserVoteEntity>> = userVoteDao.getAllVotes()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanProgress = MutableStateFlow<Pair<Int, Int>?>(null)
    val scanProgress: StateFlow<Pair<Int, Int>?> = _scanProgress.asStateFlow()

    init {
        appScope.launch {
            seedDatabaseIfEmpty()
        }
    }

    private suspend fun seedDatabaseIfEmpty() {
        val count = celebrityDao.getCelebrityCount()
        if (count < 200) {
            expandCatalogToTarget(1000)
        }
    }

    /**
     * Kataloğu ve tarama havuzunu hedef kişi sayısına (azami 1000 kişi) kadar genişletir.
     */
    suspend fun expandCatalogToTarget(targetCount: Int = 1000): Int = withContext(Dispatchers.IO) {
        val fullList = CelebrityCatalog.generateFullCatalog(targetCount.coerceIn(100, 1000))
        val entities = fullList.map { CelebrityEntity.fromDomain(it) }
        celebrityDao.insertCelebrities(entities)
        celebrityDao.trimToMaxCount(1000)

        // Sıkıştırılmış tarama denetim izi snapshot'ları oluştur
        val currentWeek = getCurrentEpochWeek()
        val initialScans = fullList.map { celeb ->
            val details = celeb.calculationDetails
            val starCompact = celeb.ratings.entries.sortedBy { it.key }.joinToString(",") { it.value.toString() }
            CompactScanEntity(
                celebrityId = celeb.id,
                epochWeek = currentWeek,
                viewsK = (celeb.wikiMonthlyViews / 1000L).toInt(),
                starDistributionCompact = starCompact,
                likeabilityL = details.likeabilityL.toFloat(),
                popularityP = details.popularityP.toFloat(),
                polarizationD = details.polarizationD.toFloat(),
                momentumM = details.finalMomentum.toFloat(),
                circuitBreakerTriggered = celeb.isCircuitBreakerActive
            )
        }
        compactScanDao.insertScans(initialScans)
        celebrityDao.getCelebrityCount()
    }

    suspend fun getCelebrity(id: String): Celebrity? {
        return celebrityDao.getCelebrityById(id)?.toDomain()
    }

    suspend fun hasUserVoted(celebrityId: String): Boolean {
        return userVoteDao.getVoteForCelebrity(celebrityId) != null
    }

    suspend fun submitVote(
        celebrityId: String,
        knows: String,
        rating: Int,
        selectedTags: List<String>
    ): Boolean = withContext(Dispatchers.IO) {
        val entity = celebrityDao.getCelebrityById(celebrityId) ?: return@withContext false
        val domain = entity.toDomain()

        // Kaydet: kullanıcı oyu
        val vote = UserVoteEntity(
            celebrityId = celebrityId,
            knows = knows,
            rating = rating,
            tagsCompact = selectedTags.joinToString("|")
        )
        userVoteDao.insertVote(vote)

        // Güncelle: oy dağılımı
        val currentRatings = domain.ratings.toMutableMap()
        currentRatings[rating] = (currentRatings[rating] ?: 0) + 1

        val updatedTags = (domain.tags + selectedTags).distinct()

        // Devre kesici denetimi
        val newBayes = PublicMetricsCalculator.calculateBayesianMean(
            currentRatings,
            domain.category.priorMean,
            domain.category.smoothingM
        )
        val newLikeability = PublicMetricsCalculator.calculateLikeability(newBayes)
        val rawDelta = newLikeability - domain.previousPeriodLikeability
        val circuitBreakerTriggered = rawDelta > 8.0 || rawDelta < -8.0

        val updatedDomain = domain.copy(
            ratings = currentRatings,
            tags = updatedTags,
            forcedCircuitBreaker = domain.forcedCircuitBreaker || circuitBreakerTriggered,
            circuitBreakerReason = if (circuitBreakerTriggered) {
                "Hızlı oy hareketi nedeniyle devre kesici devrede (±8.0 puan sınırı korundu)."
            } else domain.circuitBreakerReason
        )

        celebrityDao.insertCelebrity(CelebrityEntity.fromDomain(updatedDomain))

        // Güncel sıkıştırılmış snapshot'ı kaydet
        saveCompactScanSnapshot(updatedDomain)
        true
    }

    suspend fun submitObjection(
        celebrityId: String,
        representative: String,
        subject: String,
        statement: String
    ): Boolean = withContext(Dispatchers.IO) {
        val entity = celebrityDao.getCelebrityById(celebrityId) ?: return@withContext false
        val updated = entity.copy(
            objectionRepresentative = representative.ifBlank { "Temsilci / Doğrulama Talebi" },
            objectionSubject = subject.ifBlank { "Veri ve Skorlama İtirazı" },
            objectionStatement = statement,
            objectionDate = "Bugün (Kayıtlı)"
        )
        celebrityDao.insertCelebrity(updated)
        true
    }

    /**
     * Wikimedia Resmî Analytics API'sinden gerçek sayfa görüntülenme verilerini çeker
     * ve sıkıştırılmış snapshot kütüğüne işler.
     */
    suspend fun scanAndSyncRealDataForCelebrity(celebrityId: String): Boolean = withContext(Dispatchers.IO) {
        val entity = celebrityDao.getCelebrityById(celebrityId) ?: return@withContext false
        val domain = entity.toDomain()

        val realViews = wikiClient.getMonthlyPageviews(domain.wikiTitle)
        val finalViews = if (realViews > 0) realViews else domain.wikiMonthlyViews

        val updatedDomain = domain.copy(wikiMonthlyViews = finalViews)
        celebrityDao.insertCelebrity(CelebrityEntity.fromDomain(updatedDomain))

        saveCompactScanSnapshot(updatedDomain)
        true
    }

    /**
     * Tüm veya taranmamış profilleri Wikimedia'dan gerçek verilerle günceller.
     */
    suspend fun scanAllFromWikipedia() = withContext(Dispatchers.IO) {
        if (_isScanning.value) return@withContext
        _isScanning.value = true

        try {
            val list = celebrityDao.getAllCelebrities().first().map { it.toDomain() }
            val total = list.size
            for ((index, celeb) in list.withIndex()) {
                _scanProgress.value = (index + 1) to total
                val realViews = wikiClient.getMonthlyPageviews(celeb.wikiTitle)
                val finalViews = if (realViews > 0) realViews else celeb.wikiMonthlyViews
                val updated = celeb.copy(wikiMonthlyViews = finalViews)
                celebrityDao.insertCelebrity(CelebrityEntity.fromDomain(updated))
                saveCompactScanSnapshot(updated)
            }
        } finally {
            _isScanning.value = false
            _scanProgress.value = null
        }
    }

    /**
     * Türkçe Wikipedia'da canlı arama yapar.
     */
    suspend fun searchWikipedia(query: String): List<WikipediaSearchResult> {
        return wikiClient.searchWikipedia(query)
    }

    /**
     * Wikipedia'dan seçilen bir kişiyi gerçek biyografi ve görüntülenme verileriyle
     * 1000 kişi sınırını koruyarak listeye otomatik ekler.
     */
    suspend fun addCelebrityFromWikipedia(
        wikiTitle: String,
        category: CelebrityCategory,
        customProfession: String? = null
    ): Result<Celebrity> = withContext(Dispatchers.IO) {
        try {
            // Mevcut kişi sayısı kontrolü (üst sınır 1000)
            val currentCount = celebrityDao.getCelebrityCount()
            if (currentCount >= 1000) {
                // 1000 sınırı aşıldığında en eski taranan profilleri buda
                celebrityDao.trimToMaxCount(999)
            }

            val summary = wikiClient.getPageSummary(wikiTitle)
            val realViews = wikiClient.getMonthlyPageviews(wikiTitle)

            val cleanId = wikiTitle.lowercase()
                .replace(" ", "-")
                .replace("ı", "i")
                .replace("ğ", "g")
                .replace("ü", "u")
                .replace("ş", "s")
                .replace("ö", "o")
                .replace("ç", "c")
                .filter { it.isLetterOrDigit() || it == '-' }

            val profession = customProfession ?: summary?.description ?: category.title
            val bio = summary?.extract?.take(300) ?: "${summary?.title ?: wikiTitle} hakkında kamuoyu değerlendirmesi."

            val symbol = when (category) {
                CelebrityCategory.OYUNCU -> "🎭"
                CelebrityCategory.MUZISYEN -> "🎵"
                CelebrityCategory.SPORCU -> "🏆"
                CelebrityCategory.FENOMEN -> "✨"
            }

            val newCeleb = Celebrity(
                id = cleanId.ifBlank { "celeb-${System.currentTimeMillis()}" },
                name = summary?.title ?: wikiTitle,
                category = category,
                profession = profession,
                bio = bio,
                wikiTitle = wikiTitle,
                wikiMonthlyViews = if (realViews > 0) realViews else 85000L,
                ratings = mapOf(1 to 2, 2 to 4, 3 to 15, 4 to 60, 5 to 220),
                tags = listOf(category.title, "Yeni Eklenen", "Doğrulanmış Profil"),
                previousPeriodLikeability = (category.priorMean - 1.0) * 25.0,
                recentActivity = "Wikipedia ve kamuoyu veri kütüğüne otomatik eklendi.",
                avatarEmojiOrSymbol = symbol
            )

            celebrityDao.insertCelebrity(CelebrityEntity.fromDomain(newCeleb))
            saveCompactScanSnapshot(newCeleb)
            celebrityDao.trimToMaxCount(1000)

            Result.success(newCeleb)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sıkıştırılmış tarama denetim izini kaydeder (~50 bayt / kayıt).
     */
    private suspend fun saveCompactScanSnapshot(celebrity: Celebrity) {
        val details = celebrity.calculationDetails
        val currentWeek = getCurrentEpochWeek()
        val starCompact = celebrity.ratings.entries.sortedBy { it.key }.joinToString(",") { it.value.toString() }

        val scan = CompactScanEntity(
            celebrityId = celebrity.id,
            epochWeek = currentWeek,
            viewsK = (celebrity.wikiMonthlyViews / 1000L).toInt(),
            starDistributionCompact = starCompact,
            likeabilityL = details.likeabilityL.toFloat(),
            popularityP = details.popularityP.toFloat(),
            polarizationD = details.polarizationD.toFloat(),
            momentumM = details.finalMomentum.toFloat(),
            circuitBreakerTriggered = celebrity.isCircuitBreakerActive
        )
        compactScanDao.insertScan(scan)
    }

    fun getScanHistory(celebrityId: String): Flow<List<CompactScanEntity>> {
        return compactScanDao.getScansForCelebrity(celebrityId)
    }

    suspend fun getStorageStats(): StorageStats = withContext(Dispatchers.IO) {
        val count = celebrityDao.getCelebrityCount()
        val scanCount = compactScanDao.getTotalRecordCount()
        // Her kişi entity ~300 byte, her sıkıştırılmış tarama ~50 byte
        val estimatedBytes = (count * 300L) + (scanCount * 50L)
        StorageStats(
            celebrityCount = count,
            maxCelebrityLimit = 1000,
            totalScanSnapshots = scanCount,
            estimatedStorageBytes = estimatedBytes
        )
    }

    private fun getCurrentEpochWeek(): Int {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val week = cal.get(Calendar.WEEK_OF_YEAR)
        return year * 100 + week
    }
}
