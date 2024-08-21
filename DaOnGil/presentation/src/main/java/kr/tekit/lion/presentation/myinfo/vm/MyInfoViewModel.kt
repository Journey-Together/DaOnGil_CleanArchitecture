package kr.tekit.lion.presentation.myinfo.vm

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyInfoViewModel @Inject constructor(

): ViewModel() {

    fun onCompleteModifyPersonalWithImg(nickname: String, phone: String) {
        viewModelScope.launch {
            onCompleteModifyPersonal(nickname, phone)
            memberRepository.modifyMyProfileImg(profileImg.value.toDomainModel())
                .onSuccess {
                    _isPersonalInfoModified.update { true }
                }.onError {
                    networkErrorDelegate.handleNetworkError(it)
                }
        }
    }
}