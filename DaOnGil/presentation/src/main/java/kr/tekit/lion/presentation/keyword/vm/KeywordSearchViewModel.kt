package kr.tekit.lion.presentation.keyword.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.exception.NetworkError
import kr.tekit.lion.domain.exception.TimeoutError
import kr.tekit.lion.domain.exception.UnknownError
import kr.tekit.lion.domain.exception.UnknownHostError
import kr.tekit.lion.domain.model.search.RecentlySearchKeywordList
import kr.tekit.lion.domain.model.search.findKeyword
import kr.tekit.lion.domain.model.search.toRecentlySearchKeyword
import kr.tekit.lion.domain.repository.PlaceRepository
import kr.tekit.lion.domain.repository.RecentlySearchKeywordRepository
import kr.tekit.lion.presentation.base.BaseViewModel
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import kr.tekit.lion.presentation.keyword.model.KeywordInputState
import kr.tekit.lion.presentation.observer.ConnectivityObserver
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class KeywordSearchViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val recentlySearchKeywordRepository: RecentlySearchKeywordRepository,
) : BaseViewModel() {

    init {
        viewModelScope.launch {
            loadSavedKeyword()
        }
    }

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate
    val errorState get() = networkErrorDelegate.networkState

    private val _networkStatus = MutableStateFlow(ConnectivityObserver.Status.Available)
    val networkStatus = _networkStatus.asStateFlow()

    private val _recentlySearchKeyword = MutableStateFlow(RecentlySearchKeywordList(emptyList()))
    val recentlySearchKeyword = _recentlySearchKeyword.asStateFlow()

    private val _keyword = MutableStateFlow("")
    val keyword = _keyword.asStateFlow()

    private val _searchState = MutableStateFlow(KeywordInputState.Initial)
    val searchState = _searchState.asStateFlow()

    @OptIn(FlowPreview:: class, ExperimentalCoroutinesApi:: class)
    val autocompleteKeyword = keyword
        .debounce(DEBOUNCE_INTERVAL)
        .distinctUntilChanged()
        .filter { it.isNotEmpty() }
        .combine(_networkStatus){keyword, status -> keyword to status}
        .flatMapLatest { (keyword, status) ->
            if (status == ConnectivityObserver.Status.Available && searchState.value != KeywordInputState.Erasing) {
                val response = placeRepository.getAutoCompleteKeyword(keyword)
                networkErrorDelegate.handleNetworkSuccess()
                response
            } else {
                flow { }
            }
        }
        .flowOn(recordExceptionHandler)
        .catch { e ->
            submitThrowableState(e)
        }.retryWhen{ cause, attempt ->
            if (cause is TimeoutException && attempt < 3) {
                delay(1000)
                true
            } else {
                false
            }
        }

    fun onChangeNetworkState(state: ConnectivityObserver.Status) {
        _networkStatus.value = state
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

    private fun submitThrowableState(e: Throwable){
        when (e) {
            is TimeoutException ->{
                networkErrorDelegate.handleNetworkError(TimeoutError)
            }
            is UnknownHostException -> {
                networkErrorDelegate.handleNetworkError(UnknownHostError)
            }
            is UnknownError -> {
                networkErrorDelegate.handleNetworkError(UnknownError)
            }
            else -> {
                networkErrorDelegate.handleNetworkError(e as NetworkError)
            }
        }
    }

    companion object {
        private const val DEBOUNCE_INTERVAL = 300L
    }
}