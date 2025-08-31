package kr.techit.lion.presentation.concerntype.vm

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kr.techit.lion.domain.model.concern.ConcernType
import kr.techit.lion.domain.repository.MemberRepository
import kr.techit.lion.presentation.base.BaseViewModel2
import kr.techit.lion.presentation.concerntype.ConcernTypeUiEvent
import kr.techit.lion.presentation.concerntype.ConcernTypUiState
import kr.techit.lion.presentation.connectivity.ConnectivityObserver
import kr.techit.lion.presentation.delegate.NetworkEventDelegate
import javax.inject.Inject

@HiltViewModel
class ConcernTypeViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
    connectivityObserver: ConnectivityObserver,
    networkEventDelegate: NetworkEventDelegate,
) : BaseViewModel2<ConcernTypeUiEvent>(networkEventDelegate, connectivityObserver) {

    private val _uiState = MutableStateFlow(ConcernTypUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadConcernType()
    }

    private fun loadConcernType() = runAsync(
        action = { memberRepository.getConcernType() },
        onSuccess = { _uiState.value = _uiState.value.copy(concernType = it) }
    )

    fun updateConcernType(type: ConcernType) {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(concernType = currentState.changeSelectedType(type))
    }

    fun saveModifiedConcernType() = runAsync(
        action = { memberRepository.updateConcernType(_uiState.value.concernType) },
        onSuccess = { emitEvent(ConcernTypeUiEvent.NavigateToBack) }
    )

    fun setNickName(nickName: String) {
        _uiState.value = _uiState.value.copy(
            nickName = nickName
        )
    }
}
