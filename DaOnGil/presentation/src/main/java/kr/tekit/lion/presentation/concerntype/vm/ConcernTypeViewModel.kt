package kr.tekit.lion.presentation.concerntype.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.model.ConcernType
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.repository.MemberRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import javax.inject.Inject

@HiltViewModel
class ConcernTypeViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    private val _nickName = MutableLiveData<String>()
    val nickName: LiveData<String> = _nickName

    private val _concernType = MutableLiveData<ConcernType>()
    val concernType: LiveData<ConcernType> = _concernType

    val networkState get() = networkErrorDelegate.networkState

    init {
        val userNickName = savedStateHandle.get<String>("nickName") ?: ""
        _nickName.value = userNickName

        getConcernType()
    }

    private fun getConcernType() = viewModelScope.launch {
        memberRepository.getConcernType().onSuccess {
            _concernType.value = it
            networkErrorDelegate.handleNetworkSuccess()
        }.onError {
            networkErrorDelegate.handleNetworkError(it)
        }
    }

    fun updateConcernType(requestBody: ConcernType) {
        viewModelScope.launch {
            memberRepository.updateConcernType(requestBody)
                .onError {
                    networkErrorDelegate.handleNetworkError(it)
                }
        }

        _concernType.value = _concernType.value?.copy(
            isPhysical = requestBody.isPhysical,
            isVisual = requestBody.isVisual,
            isHear = requestBody.isHear,
            isChild = requestBody.isChild,
            isElderly = requestBody.isElderly
        )
    }
}