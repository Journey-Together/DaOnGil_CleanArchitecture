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
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.login.model.InterestType
import javax.inject.Inject

@HiltViewModel
class InterestViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
): BaseViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    val networkState: StateFlow<NetworkState> get() = networkErrorDelegate.networkState

    private val _concernType = MutableStateFlow(ConcernType(
        isPhysical = false,
        isHear = false,
        isVisual = false,
        isElderly = false,
        isChild = false
    ))

    val concernType get() = _concernType.asStateFlow()

    fun onSelectInterest(type: InterestType) {
        val currentInterests = _concernType.value

        val updatedInterests = when(type) {
            InterestType.Physical -> currentInterests.copy(isPhysical = !currentInterests.isPhysical)
            InterestType.Hear -> currentInterests.copy(isHear = !currentInterests.isHear)
            InterestType.Visual -> currentInterests.copy(isVisual = !currentInterests.isVisual)
            InterestType.Child -> currentInterests.copy(isChild = !currentInterests.isChild)
            InterestType.Elderly -> currentInterests.copy(isElderly = !currentInterests.isElderly)
        }

        _concernType.update { updatedInterests }
    }

    fun onClickSubmitButton() {
        viewModelScope.launch(recordExceptionHandler){
            memberRepository.updateConcernType(_concernType.value).onSuccess {
                networkErrorDelegate.handleNetworkSuccess()
            }.onError {
                networkErrorDelegate.handleNetworkError(it)
            }
        }
    }
}