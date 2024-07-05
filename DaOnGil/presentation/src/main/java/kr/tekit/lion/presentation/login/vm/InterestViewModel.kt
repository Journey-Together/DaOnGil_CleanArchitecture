package kr.tekit.lion.presentation.login.vm

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kr.tekit.lion.domain.model.ConcernType
import kr.tekit.lion.domain.repository.MemberRepository
import javax.inject.Inject

@HiltViewModel
class InterestViewModel @Inject constructor(
    private val memberRepository: MemberRepository
): ViewModel() {

    private val _concernType = MutableStateFlow(ConcernType(
        isPhysical = false,
        isHear = false,
        isVisual = false,
        isElderly = false,
        isChild = false
    ))

    val concernType get() = _concernType.asStateFlow()

    fun onSelectInterest(typeNo: Int) {
        val currentInterests = _concernType.value

        val updatedInterests = when(typeNo) {
            1 -> currentInterests.copy(isPhysical = !currentInterests.isPhysical)
            2 -> currentInterests.copy(isHear = !currentInterests.isHear)
            3 -> currentInterests.copy(isVisual = !currentInterests.isVisual)
            4 -> currentInterests.copy(isElderly = !currentInterests.isElderly)
            5 -> currentInterests.copy(isChild = !currentInterests.isChild)
            else -> currentInterests
        }

        _concernType.value = updatedInterests
    }


    suspend fun onClickSubmitButton(){
        memberRepository.updateConcernType(_concernType.value)
    }
}