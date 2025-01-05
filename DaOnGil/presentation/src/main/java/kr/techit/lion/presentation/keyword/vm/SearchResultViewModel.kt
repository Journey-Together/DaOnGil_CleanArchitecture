package kr.techit.lion.presentation.keyword.vm

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.domain.repository.PlaceRepository
import kr.techit.lion.presentation.base.BaseViewModel
import kr.techit.lion.presentation.delegate.NetworkErrorDelegate
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.keyword.model.KeywordSearch
import kr.techit.lion.presentation.main.model.PlaceModel
import kr.techit.lion.presentation.main.model.toUiModel
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
): BaseViewModel(){

    init {
        loadPlace()
    }

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    val networkState: StateFlow<NetworkState> get() = networkErrorDelegate.networkState

    private val _query = MutableStateFlow(KeywordSearch(keyword = "", page = 0))
    val query: StateFlow<KeywordSearch> = _query.asStateFlow()

    private val _place: MutableStateFlow<List<PlaceModel>> = MutableStateFlow(emptyList())
    val place: StateFlow<List<PlaceModel>> = _place.asStateFlow()

    private val _isLastPage = MutableStateFlow(false)
    val isLastPage get() = _isLastPage.asStateFlow()

    private fun loadPlace() = viewModelScope.launch(recordExceptionHandler){
        query.collect {
            placeRepository.getSearchPlaceResultByList(
                it.toDomainModel()
            ).onSuccess { response ->
                _place.value += response.toUiModel()
                if (response.isLastPage) _isLastPage.value = true
                networkErrorDelegate.handleNetworkSuccess()
            }.onError { e ->
                networkErrorDelegate.handleNetworkError(e)
            }
        }
    }

    fun onChangeQuery(keyword: String) {
        if (_place.value.isNotEmpty()) _place.value = emptyList()
        _query.value = KeywordSearch(keyword = keyword, page = 0)
        _isLastPage.value = false
    }

    fun whenLastPageReached() {
        _query.update { it.copy(page = it.page + 1) }
    }
}