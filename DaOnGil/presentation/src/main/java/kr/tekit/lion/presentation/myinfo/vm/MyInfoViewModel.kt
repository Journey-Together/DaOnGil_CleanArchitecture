package kr.tekit.lion.presentation.myinfo.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.model.IceInfo
import kr.tekit.lion.domain.model.PersonalInfo
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.repository.MemberRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import kr.tekit.lion.presentation.delegate.NetworkState
import kr.tekit.lion.presentation.myinfo.model.ImgModifyState
import kr.tekit.lion.presentation.myinfo.model.UserProfileImg
import javax.inject.Inject

@HiltViewModel
class MyInfoViewModel @Inject constructor(
    private val memberRepository: MemberRepository
): ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    val networkState: StateFlow<NetworkState> get() = networkErrorDelegate.networkState

    private val _modifyState = MutableStateFlow(ImgModifyState.ImgUnSelected)
    val modifyState = _modifyState.asStateFlow()

    private val _myPersonalInfo = MutableStateFlow(PersonalInfo())
    val myPersonalInfo = _myPersonalInfo.asStateFlow()

    private val _profileImg = MutableStateFlow(UserProfileImg(""))
    val profileImg = _profileImg.asStateFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _iceInfo = MutableStateFlow(IceInfo())
    val myIceInfo = _iceInfo.asStateFlow()

    private val _isPersonalInfoModified = MutableStateFlow(false)
    val isPersonalInfoModified = _isPersonalInfoModified.asStateFlow()

    init {
        initUiData()
    }

    private fun initUiData() = viewModelScope.launch {
        memberRepository.getMyIfo().onSuccess { myInfo ->
            _name.update { myInfo.name ?: "" }

            _myPersonalInfo.update {
                it.copy(nickname = myInfo.nickname ?: "", phone = myInfo.phone ?: "")
            }

            _profileImg.update { it.copy(imagePath = myInfo.profileImage ?: "") }

            _iceInfo.value = IceInfo(
                bloodType = myInfo.bloodType ?: "",
                birth = myInfo.birth ?: "",
                disease = myInfo.disease ?: "",
                allergy = myInfo.allergy ?: "",
                medication = myInfo.medication ?: "",
                part1Rel = myInfo.part1Rel ?: "",
                part1Phone = myInfo.part1Phone ?: "",
                part2Rel = myInfo.part2Rel ?: "",
                part2Phone = myInfo.part2Phone ?: ""
            )
            networkErrorDelegate.handleNetworkSuccess()
        }.onError {
            networkErrorDelegate.handleNetworkError(it)
        }
    }

    fun onCompleteModifyPersonal(nickname: String, phone: String) {
        _myPersonalInfo.update { it.copy(nickname = nickname, phone = phone) }

        viewModelScope.launch {
            memberRepository.modifyMyPersonalInfo(PersonalInfo(nickname, phone))
                .onSuccess {
                    _isPersonalInfoModified.update { true }
                }.onError {
                    networkErrorDelegate.handleNetworkError(it)
                }
        }
    }

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

    fun onCompleteModifyIce(newIceInfo: IceInfo) {
        _iceInfo.update {
            it.copy(
                birth = newIceInfo.birth,
                bloodType = newIceInfo.bloodType,
                disease = newIceInfo.disease,
                allergy = newIceInfo.allergy,
                medication = newIceInfo.medication,
                part1Rel = newIceInfo.part1Rel,
                part1Phone = newIceInfo.part1Phone,
                part2Rel = newIceInfo.part2Rel,
                part2Phone = newIceInfo.part2Phone
            )
        }
        viewModelScope.launch {
            memberRepository.modifyMyIceInfo(myIceInfo.value).onError {
                networkErrorDelegate.handleNetworkError(it)
            }
        }
    }

    fun onSelectProfileImage(imgUrl: String?) {
        imgUrl?.let { _profileImg.update { it.copy(imagePath = imgUrl) } }
    }

    fun modifyStateChange() {
        _modifyState.value = ImgModifyState.ImgSelected
    }
}