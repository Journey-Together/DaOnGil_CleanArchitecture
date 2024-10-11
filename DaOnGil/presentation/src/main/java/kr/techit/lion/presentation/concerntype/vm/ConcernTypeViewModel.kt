package kr.techit.lion.presentation.concerntype.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.techit.lion.domain.model.ConcernType
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.domain.repository.MemberRepository
import kr.techit.lion.presentation.delegate.NetworkErrorDelegate
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

    private val _snackbarEvent = MutableLiveData<String?>()
    val snackbarEvent: LiveData<String?> = _snackbarEvent

    val networkState get() = networkErrorDelegate.networkState

    init {
        val userNickName = savedStateHandle.get<String>("nickName") ?: ""
        _nickName.value = userNickName

        getConcernType()
    }

    fun getConcernType() = viewModelScope.launch {
        memberRepository.getConcernType().onSuccess {
            _concernType.value = it
            networkErrorDelegate.handleNetworkSuccess()
        }.onError {
            networkErrorDelegate.handleNetworkError(it)
        }
    }

    fun updateConcernType(requestBody: ConcernType) {
        viewModelScope.launch {
            memberRepository.updateConcernType(requestBody).onSuccess {
                _snackbarEvent.postValue("관심 유형이 수정되었습니다.")
            }.onError {
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

    fun resetSnackbarEvent() {
        _snackbarEvent.value = null
    }
}