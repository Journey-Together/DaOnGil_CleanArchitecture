package kr.techit.lion.presentation.keyword.vm

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.techit.lion.presentation.connectivity.connectivity.ConnectivityObserver
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.domain.model.search.ListSearchOption
import kr.techit.lion.domain.model.search.ListSearchResultList
import kr.techit.lion.domain.repository.PlaceRepository
import kr.techit.lion.presentation.base.BaseViewModel
import kr.techit.lion.presentation.delegate.NetworkEvent
import kr.techit.lion.presentation.delegate.NetworkEventDelegate
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.stateInUi
import kr.techit.lion.presentation.keyword.vm.model.SearchResultState
import kr.techit.lion.presentation.main.search.vm.model.toUiModel
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val networkEventDelegate: NetworkEventDelegate,
    connectivityObserver: ConnectivityObserver
) : BaseViewModel() {

    val networkEvent get() = networkEventDelegate.event

    private val _uiState = MutableStateFlow(SearchResultState())
    val uiState get() = _uiState.asStateFlow()

    val connectivityStatus = connectivityObserver.observe()
        .stateInUi(
            scope = viewModelScope,
            initialValue = NetworkState.Loading
        )

    fun loadPlace(searchQuery: String) {
        viewModelScope.launch(recordExceptionHandler) {
            placeRepository.getSearchPlaceResultByList(
                ListSearchOption(
                    category = null,
                    page = _uiState.value.page,
                    size = 10,
                    query = searchQuery
                )
            ).onSuccess { response ->
                updatePlace(response)
            }.onError { e ->
                val msg = networkEventDelegate.asUiText(e)
                networkEventDelegate.emitEvent(viewModelScope, NetworkEvent.Error(msg))
            }
        }
    }

    private fun updatePlace(response: ListSearchResultList) {
        val uiState = _uiState.value
        _uiState.value = uiState.copy(
            place = uiState.place + response.toUiModel(),
            page = uiState.page + 1
        )
        if (response.isLastPage) _uiState.value = uiState.copy(isLastPage = true)
        networkEventDelegate.emitEvent(
            scope = viewModelScope,
            event = NetworkEvent.Success
        )
    }
}
