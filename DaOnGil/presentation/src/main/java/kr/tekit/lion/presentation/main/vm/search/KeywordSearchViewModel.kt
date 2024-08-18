package kr.tekit.lion.presentation.main.vm.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.model.search.RecentlySearchKeyword
import kr.tekit.lion.domain.model.search.toRecentlySearchKeyword
import kr.tekit.lion.domain.repository.PlaceRepository
import kr.tekit.lion.domain.repository.RecentlySearchKeywordRepository
import javax.inject.Inject

@HiltViewModel
class KeywordSearchViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val recentlySearchKeywordRepository: RecentlySearchKeywordRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            loadSavedKeyword()
        }
    }

    private val _recentlySearchKeyword = MutableStateFlow<List<RecentlySearchKeyword>>(emptyList())
    val recentlySearchKeyword = _recentlySearchKeyword.asStateFlow()

    private val _keyword = MutableStateFlow("")
    val keyword = _keyword.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val autocompleteKeyword = keyword
        .debounce(DEBOUNCE_INTERVAL)
        .flatMapLatest { keyword ->
            placeRepository.getAutoCompleteKeyword(keyword)
        }
        .flowOn(Dispatchers.IO)
        .catch { e: Throwable ->
            e.printStackTrace()
        }

    fun updateKeyword(keyword: String) {
        _keyword.value = keyword
    }

    fun onClickSearchButton(keyword: String){
    }

    private fun loadSavedKeyword() = viewModelScope.launch(Dispatchers.IO){
        recentlySearchKeywordRepository.readAllKeyword().collect{
            _recentlySearchKeyword.value =  it
        }
    }

    fun insertKeyword(keyword: String) = viewModelScope.launch(Dispatchers.IO) {
        val existingKeyword = _recentlySearchKeyword.value.firstOrNull { it.keyword == keyword }
        if (existingKeyword != null) {
            existingKeyword.id?.let { recentlySearchKeywordRepository.deleteKeyword(it) }
        }
        recentlySearchKeywordRepository.insertKeyword(keyword.toRecentlySearchKeyword())
    }

    fun deleteKeyword(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        recentlySearchKeywordRepository.deleteKeyword(id)
    }

    fun deleteAllKeyword() = viewModelScope.launch(Dispatchers.IO) {
        recentlySearchKeywordRepository.deleteAllKeyword()
    }

    companion object {
        private const val DEBOUNCE_INTERVAL = 800L
    }
}