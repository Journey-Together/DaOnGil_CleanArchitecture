package kr.tekit.lion.presentation.login.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.model.ConcernType
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.repository.MemberRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import kr.tekit.lion.presentation.delegate.NetworkState
import kr.tekit.lion.presentation.login.model.InterestType
import javax.inject.Inject

@HiltViewModel
class InterestViewModel @Inject constructor(
    private val memberRepository: MemberRepository
): ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    val errorMessage: StateFlow<String?> get() = networkErrorDelegate.errorMessage
    val networkState: StateFlow<NetworkState?> get() = networkErrorDelegate.networkState

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
            InterestType.Child -> currentInterests.copy(isElderly = !currentInterests.isElderly)
            InterestType.Elderly -> currentInterests.copy(isChild = !currentInterests.isChild)
        }

        _concernType.update { updatedInterests }
    }

    fun onClickSubmitButton() = viewModelScope.launch{
        memberRepository.updateConcernType(_concernType.value).onSuccess {
            networkErrorDelegate.handleNetworkSuccess()
        }.onError {
            networkErrorDelegate.handleNetworkError(it)
        }
    }
}