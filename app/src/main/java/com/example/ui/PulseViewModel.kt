package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.CompactScanEntity
import com.example.data.model.Celebrity
import com.example.data.model.CelebrityCategory
import com.example.data.remote.WikipediaSearchResult
import com.example.data.repository.PulseRepository
import com.example.data.repository.StorageStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab(val title: String) {
    FEED("Gündem"),
    RANKINGS("Haftalık Listeler"),
    COMPARE("Karşılaştır"),
    METHODOLOGY("Metodoloji & Etik")
}

enum class ScoreSortOption(val title: String) {
    SEVILIRLIK("Sevilirlik (L)"),
    POPULERLIK("Popülerlik (P)"),
    KUTUPLASMA("Kutuplaşma (D)"),
    MOMENTUM("Momentum (M)")
}

enum class RankingTab(val title: String, val subtitle: String) {
    TOP_LIKED("En Sevilenler", "Bayesçi düzeltmeli kullanıcı beğenisi"),
    TOP_POPULAR("En Popülerler", "Wikipedia görüntülenme & arama ilgisi"),
    TOP_RISING("En Çok Yükselenler", "Dönemler arası pozitif momentum"),
    TOP_POLARIZED("En Kutuplaştırıcı", "Fikir ayrılığı en belirgin profiller")
}

data class PulseUiState(
    val celebrities: List<Celebrity> = emptyList(),
    val filteredCelebrities: List<Celebrity> = emptyList(),
    val selectedCategory: CelebrityCategory? = null,
    val searchQuery: String = "",
    val sortOption: ScoreSortOption = ScoreSortOption.SEVILIRLIK,
    val currentTab: AppNavTab = AppNavTab.FEED,
    val selectedDetailCelebrity: Celebrity? = null,
    val transparencyCelebrity: Celebrity? = null,
    val votingCelebrity: Celebrity? = null,
    val objectionCelebrity: Celebrity? = null,
    val compareCelebrity1: Celebrity? = null,
    val compareCelebrity2: Celebrity? = null,
    val selectedRankingTab: RankingTab = RankingTab.TOP_LIKED,
    val userNotification: String? = null,
    val isScanning: Boolean = false,
    val scanProgress: Pair<Int, Int>? = null,
    val storageStats: StorageStats = StorageStats(0, 1000, 0, 0L),
    val showAddCelebrityDialog: Boolean = false,
    val wikiSearchResults: List<WikipediaSearchResult> = emptyList(),
    val isSearchingWiki: Boolean = false,
    val selectedCelebrityScanHistory: List<CompactScanEntity> = emptyList()
)

class PulseViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: PulseRepository = PulseRepository(application.applicationContext)

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<CelebrityCategory?>(null)
    private val _sortOption = MutableStateFlow(ScoreSortOption.SEVILIRLIK)
    private val _currentTab = MutableStateFlow(AppNavTab.FEED)
    private val _selectedDetailId = MutableStateFlow<String?>(null)
    private val _transparencyCelebrityId = MutableStateFlow<String?>(null)
    private val _votingCelebrityId = MutableStateFlow<String?>(null)
    private val _objectionCelebrityId = MutableStateFlow<String?>(null)
    private val _compareCeleb1Id = MutableStateFlow<String?>("haluk-bilginer")
    private val _compareCeleb2Id = MutableStateFlow<String?>("yusuf-dikec")
    private val _selectedRankingTab = MutableStateFlow(RankingTab.TOP_LIKED)
    private val _userNotification = MutableStateFlow<String?>(null)

    private val _showAddCelebrityDialog = MutableStateFlow(false)
    private val _wikiSearchResults = MutableStateFlow<List<WikipediaSearchResult>>(emptyList())
    private val _isSearchingWiki = MutableStateFlow(false)
    private val _storageStats = MutableStateFlow(StorageStats(0, 1000, 0, 0L))
    private val _selectedCelebrityScanHistory = MutableStateFlow<List<CompactScanEntity>>(emptyList())
    private val _votedCelebrityIds = MutableStateFlow<Set<String>>(emptySet())

    init {
        viewModelScope.launch {
            repository.userVotes.collect { votes ->
                _votedCelebrityIds.value = votes.map { it.celebrityId }.toSet()
            }
        }
        refreshStats()
    }

    val uiState: StateFlow<PulseUiState> = combine(
        repository.celebrities,
        _searchQuery,
        _selectedCategory,
        _sortOption,
        _currentTab,
        _selectedDetailId,
        _transparencyCelebrityId,
        _votingCelebrityId,
        _objectionCelebrityId,
        _compareCeleb1Id,
        _compareCeleb2Id,
        _selectedRankingTab,
        _userNotification,
        repository.isScanning,
        repository.scanProgress,
        _storageStats,
        _showAddCelebrityDialog,
        _wikiSearchResults,
        _isSearchingWiki,
        _selectedCelebrityScanHistory
    ) { params ->
        @Suppress("UNCHECKED_CAST")
        val celebs = params[0] as List<Celebrity>
        val query = params[1] as String
        val cat = params[2] as CelebrityCategory?
        val sort = params[3] as ScoreSortOption
        val tab = params[4] as AppNavTab
        val detailId = params[5] as String?
        val transId = params[6] as String?
        val voteId = params[7] as String?
        val objId = params[8] as String?
        val comp1Id = params[9] as String?
        val comp2Id = params[10] as String?
        val rankTab = params[11] as RankingTab
        val notif = params[12] as String?
        val isScan = params[13] as Boolean
        val scanProg = params[14] as Pair<Int, Int>?
        val stats = params[15] as StorageStats
        val showAdd = params[16] as Boolean
        val wikiResults = params[17] as List<WikipediaSearchResult>
        val isSearching = params[18] as Boolean
        val scanHistory = params[19] as List<CompactScanEntity>

        val filtered = celebs.filter { celeb ->
            val matchesCategory = cat == null || celeb.category == cat
            val matchesQuery = query.isBlank() ||
                celeb.name.contains(query, ignoreCase = true) ||
                celeb.profession.contains(query, ignoreCase = true) ||
                celeb.tags.any { it.contains(query, ignoreCase = true) }
            matchesCategory && matchesQuery
        }.let { list ->
            when (sort) {
                ScoreSortOption.SEVILIRLIK -> list.sortedByDescending { it.likeability }
                ScoreSortOption.POPULERLIK -> list.sortedByDescending { it.popularity }
                ScoreSortOption.KUTUPLASMA -> list.sortedByDescending { it.polarization }
                ScoreSortOption.MOMENTUM -> list.sortedByDescending { it.momentum }
            }
        }

        PulseUiState(
            celebrities = celebs,
            filteredCelebrities = filtered,
            selectedCategory = cat,
            searchQuery = query,
            sortOption = sort,
            currentTab = tab,
            selectedDetailCelebrity = celebs.find { it.id == detailId },
            transparencyCelebrity = celebs.find { it.id == transId },
            votingCelebrity = celebs.find { it.id == voteId },
            objectionCelebrity = celebs.find { it.id == objId },
            compareCelebrity1 = celebs.find { it.id == comp1Id },
            compareCelebrity2 = celebs.find { it.id == comp2Id },
            selectedRankingTab = rankTab,
            userNotification = notif,
            isScanning = isScan,
            scanProgress = scanProg,
            storageStats = stats,
            showAddCelebrityDialog = showAdd,
            wikiSearchResults = wikiResults,
            isSearchingWiki = isSearching,
            selectedCelebrityScanHistory = scanHistory
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PulseUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: CelebrityCategory?) {
        _selectedCategory.value = category
    }

    fun onSortOptionChanged(option: ScoreSortOption) {
        _sortOption.value = option
    }

    fun onTabSelected(tab: AppNavTab) {
        _currentTab.value = tab
        if (_selectedDetailId.value != null && tab != AppNavTab.FEED) {
            _selectedDetailId.value = null
        }
    }

    fun selectCelebrityDetail(celebrity: Celebrity?) {
        _selectedDetailId.value = celebrity?.id
        if (celebrity != null) {
            loadScanHistory(celebrity.id)
        } else {
            _selectedCelebrityScanHistory.value = emptyList()
        }
    }

    private fun loadScanHistory(celebrityId: String) {
        viewModelScope.launch {
            repository.getScanHistory(celebrityId).collect { history ->
                _selectedCelebrityScanHistory.value = history
            }
        }
    }

    fun showTransparencyDialog(celebrity: Celebrity?) {
        _transparencyCelebrityId.value = celebrity?.id
    }

    fun showVotingSheet(celebrity: Celebrity?) {
        _votingCelebrityId.value = celebrity?.id
    }

    fun showObjectionDialog(celebrity: Celebrity?) {
        _objectionCelebrityId.value = celebrity?.id
    }

    fun setShowAddCelebrityDialog(show: Boolean) {
        _showAddCelebrityDialog.value = show
        if (!show) {
            _wikiSearchResults.value = emptyList()
        }
    }

    fun selectCompareCelebrities(celeb1: Celebrity?, celeb2: Celebrity?) {
        _compareCeleb1Id.value = celeb1?.id
        _compareCeleb2Id.value = celeb2?.id
    }

    fun onRankingTabSelected(tab: RankingTab) {
        _selectedRankingTab.value = tab
    }

    fun submitVote(
        celebrityId: String,
        knows: String,
        rating: Int,
        tags: List<String>
    ) {
        viewModelScope.launch {
            val success = repository.submitVote(celebrityId, knows, rating, tags)
            if (success) {
                _votingCelebrityId.value = null
                _userNotification.value = "Oyunuz Bayesçi formüle dahil edildi ve sıkıştırılmış kütüğe işlendi."
                refreshStats()
            }
        }
    }

    fun submitObjection(
        celebrityId: String,
        representative: String,
        subject: String,
        statement: String
    ) {
        viewModelScope.launch {
            val success = repository.submitObjection(celebrityId, representative, subject, statement)
            if (success) {
                _objectionCelebrityId.value = null
                _userNotification.value = "İtiraz ve düzeltme açıklaması şeffaflık kütüğüne işlendi."
            }
        }
    }

    fun triggerWikipediaScanAll() {
        viewModelScope.launch {
            _userNotification.value = "Wikimedia REST API'sinden gerçek sayfa görüntülenme taraması başlatıldı..."
            repository.scanAllFromWikipedia()
            _userNotification.value = "Tarama tamamlandı: Tüm popülerlik ve kutuplaşma göstergeleri gerçek verilerle güncellendi."
            refreshStats()
        }
    }

    fun expandCatalogToTarget(targetCount: Int = 1000) {
        viewModelScope.launch {
            _userNotification.value = "Katalog $targetCount kişiye genişletiliyor ve taranıyor..."
            val newTotal = repository.expandCatalogToTarget(targetCount)
            _userNotification.value = "Katalog başarıyla $newTotal kişiye genişletildi! (Sıkıştırılmış denetim izi hazır)."
            refreshStats()
        }
    }

    fun triggerSingleCelebrityScan(celebrityId: String) {
        viewModelScope.launch {
            val success = repository.scanAndSyncRealDataForCelebrity(celebrityId)
            if (success) {
                _userNotification.value = "Gerçek Wikimedia verisi çekildi ve sıkıştırılmış denetim izine kaydedildi."
                loadScanHistory(celebrityId)
                refreshStats()
            }
        }
    }

    fun searchWikipedia(query: String) {
        if (query.isBlank()) {
            _wikiSearchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isSearchingWiki.value = true
            try {
                val results = repository.searchWikipedia(query)
                _wikiSearchResults.value = results
            } finally {
                _isSearchingWiki.value = false
            }
        }
    }

    fun addCelebrityFromWikipedia(wikiTitle: String, category: CelebrityCategory) {
        viewModelScope.launch {
            _userNotification.value = "'$wikiTitle' için Wikipedia verileri çekiliyor..."
            val result = repository.addCelebrityFromWikipedia(wikiTitle, category)
            result.onSuccess { celeb ->
                _showAddCelebrityDialog.value = false
                _userNotification.value = "'${celeb.name}' başarıyla eklendi! (Kapasite: ${_storageStats.value.celebrityCount + 1}/1000)"
                refreshStats()
            }.onFailure {
                _userNotification.value = "Hata: Kişi eklenemedi (${it.localizedMessage})."
            }
        }
    }

    private fun refreshStats() {
        viewModelScope.launch {
            val stats = repository.getStorageStats()
            _storageStats.value = stats
        }
    }

    fun clearNotification() {
        _userNotification.value = null
    }

    fun hasUserVoted(celebrityId: String): Boolean {
        return _votedCelebrityIds.value.contains(celebrityId)
    }
}
