package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Celebrity
import com.example.data.model.CelebrityCategory
import com.example.data.repository.PulseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

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
    val userNotification: String? = null
)

class PulseViewModel(
    private val repository: PulseRepository = PulseRepository()
) : ViewModel() {

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
        _userNotification
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
            userNotification = notif
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
        // If navigating to main tab, clear detail view
        if (_selectedDetailId.value != null && tab != AppNavTab.FEED) {
            _selectedDetailId.value = null
        }
    }

    fun selectCelebrityDetail(celebrity: Celebrity?) {
        _selectedDetailId.value = celebrity?.id
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
        val success = repository.submitVote(celebrityId, knows, rating, tags)
        if (success) {
            _votingCelebrityId.value = null
            _userNotification.value = "Oyunuz Bayesçi formüle dahil edildi (1 oy / 90 gün koruması aktif)."
        }
    }

    fun submitObjection(
        celebrityId: String,
        representative: String,
        subject: String,
        statement: String
    ) {
        val success = repository.submitObjection(celebrityId, representative, subject, statement)
        if (success) {
            _objectionCelebrityId.value = null
            _userNotification.value = "İtiraz ve düzeltme açıklaması şeffaflık kütüğüne işlendi."
        }
    }

    fun clearNotification() {
        _userNotification.value = null
    }

    fun hasUserVoted(celebrityId: String): Boolean {
        return repository.hasUserVoted(celebrityId)
    }
}
