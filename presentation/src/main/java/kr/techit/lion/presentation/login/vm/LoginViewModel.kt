package kr.techit.lion.presentation.login.vm

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.techit.lion.domain.repository.AuthRepository
import kr.techit.lion.domain.repository.MemberRepository
import kr.techit.lion.presentation.base.BaseViewModel
import kr.techit.lion.presentation.delegate.NetworkEventDelegate
import kr.techit.lion.presentation.login.model.UserType
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val memberRepository: MemberRepository,
    private val networkEventDelegate: NetworkEventDelegate,
) : BaseViewModel() {

    val networkEvent get() = networkEventDelegate.event

    private val _userType = MutableStateFlow(UserType.Checking)
    val userType get() = _userType.asStateFlow()

    fun signIn(type: String, accessToken: String, refreshToken: String) {
        viewModelScope.launch(recordExceptionHandler) {
            authRepository.signIn(type, accessToken, refreshToken)
            checkUserState()
        }
    }

    private fun checkUserState() = execute(
        action = { memberRepository.getConcernType() },
        eventHandler = networkEventDelegate,
        onSuccess = { type ->
            if (type.anyTrue()) {
                _userType.update { UserType.ExistingUser }
            } else {
                _userType.update { UserType.NewUser }
            }
        }
    )
}
