package kr.techit.lion.presentation.login.vm

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.techit.lion.domain.model.ConcernType
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.domain.repository.MemberRepository
import kr.techit.lion.presentation.base.BaseViewModel
import kr.techit.lion.presentation.delegate.NetworkErrorDelegate
import kr.techit.lion.presentation.delegate.NetworkEventDelegate
import kr.techit.lion.domain.model.InterestType
import javax.inject.Inject

@HiltViewModel
class InterestViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
    private val networkEventDelegate: NetworkEventDelegate
): BaseViewModel() {

    val networkEvent get() = networkEventDelegate.event

    private val _state = MutableStateFlow(ConcernType.create())
    val state get() = _state.asStateFlow()

    fun onSelectInterest(type: InterestType) {
        val currentInterests = _state.value

        val updatedInterests = when(type) {
            InterestType.Physical -> currentInterests.copy(isPhysical = !currentInterests.isPhysical)
            InterestType.Hear -> currentInterests.copy(isHear = !currentInterests.isHear)
            InterestType.Visual -> currentInterests.copy(isVisual = !currentInterests.isVisual)
            InterestType.Child -> currentInterests.copy(isChild = !currentInterests.isChild)
            InterestType.Elderly -> currentInterests.copy(isElderly = !currentInterests.isElderly)
        }

        _state.update { updatedInterests }
    }

    fun onClickSubmitButton() {
        execute(
            action = {
                memberRepository.updateConcernType(_state.value)
            },
            eventHandler = networkEventDelegate,
            onSuccess = {}
        )
    }
}