package kr.tekit.lion.presentation.keyword.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.exception.NetworkError
import kr.tekit.lion.domain.model.search.RecentlySearchKeyword
import kr.tekit.lion.domain.model.search.RecentlySearchKeywordList
import kr.tekit.lion.domain.model.search.findKeyword
import kr.tekit.lion.domain.model.search.toRecentlySearchKeyword
import kr.tekit.lion.domain.repository.PlaceRepository
import kr.tekit.lion.domain.repository.RecentlySearchKeywordRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import kr.tekit.lion.presentation.keyword.model.KeywordInputState
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

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate
    val networkState get() = networkErrorDelegate.networkState

    private val _recentlySearchKeyword = MutableStateFlow(RecentlySearchKeywordList(emptyList()))
    val recentlySearchKeyword = _recentlySearchKeyword.asStateFlow()

    private val _keyword = MutableStateFlow("")
    val keyword = _keyword.asStateFlow()

    private val _searchState = MutableStateFlow(KeywordInputState.Initial)
    val searchState = _searchState.asStateFlow()

    @OptIn(FlowPreview:: class,  ExperimentalCoroutinesApi:: class)
    val autocompleteKeyword = keyword
        .debounce(DEBOUNCE_INTERVAL)
        .distinctUntilChanged()
        .filter { it.isNotEmpty() }
        .flatMapLatest { keyword ->
            if (searchState.value != KeywordInputState.Erasing) {
                networkErrorDelegate.handleNetworkLoading()
                placeRepository.getAutoCompleteKeyword(keyword)
            } else {
                flow { }
            }
        }
        .flowOn(Dispatchers.IO)
        .catch { e: Throwable ->
            when (e) {
                is NetworkError -> networkErrorDelegate.handleNetworkError(e)
            }
        }


    fun inputTextChanged(keyword: String) {
        if (searchState.value != KeywordInputState.Erasing) {
            _keyword.update { keyword }
        }
    }

    fun keywordInputStateChanged(state: KeywordInputState) {
        _searchState.value = state
    }

    private fun loadSavedKeyword() = viewModelScope.launch(Dispatchers.IO){
        recentlySearchKeywordRepository.readAllKeyword().collect{
            _recentlySearchKeyword.value =  it
        }
    }

    fun insertKeyword(keyword: String, onSuccess: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val existingKeyword = _recentlySearchKeyword.value.findKeyword(keyword)
        if (existingKeyword != null) {
            existingKeyword.id?.let { recentlySearchKeywordRepository.deleteKeyword(it) }
        }
        recentlySearchKeywordRepository.insertKeyword(keyword.toRecentlySearchKeyword())
        onSuccess()
    }

    fun deleteKeyword(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        recentlySearchKeywordRepository.deleteKeyword(id)
    }

    fun onClickDeleteButton() = viewModelScope.launch(Dispatchers.IO) {
        recentlySearchKeywordRepository.deleteAllKeyword()
    }

    companion object {
        private const val DEBOUNCE_INTERVAL = 300L
    }
}