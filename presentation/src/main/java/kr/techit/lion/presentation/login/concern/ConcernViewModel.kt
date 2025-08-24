package kr.techit.lion.presentation.login.concern

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kr.techit.lion.domain.model.concern.ConcernType
import kr.techit.lion.domain.model.concern.Concerns
import kr.techit.lion.domain.repository.MemberRepository
import kr.techit.lion.presentation.base.BaseViewModel
import kr.techit.lion.presentation.delegate.NetworkEventDelegate
import javax.inject.Inject

@HiltViewModel
class ConcernViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
    private val networkEventDelegate: NetworkEventDelegate
): BaseViewModel() {

    val networkEvent get() = networkEventDelegate.event

    private val _uiState = MutableStateFlow(Concerns())
    val uiState get() = _uiState.asStateFlow()

    fun onClickSubmitButton() {
        execute(
            action = {
                memberRepository.updateConcernType(_uiState.value)
            },
            eventHandler = networkEventDelegate,
            onSuccess = {}
        )
    }

    fun onSelectInterest(type: ConcernType) {
        _uiState.update { it.update(type) }
    }
}
