package kr.techit.lion.presentation.login.vm

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.domain.model.ConcernType
import kr.techit.lion.domain.model.hasAnyTrue
import kr.techit.lion.domain.repository.AuthRepository
import kr.techit.lion.domain.repository.MemberRepository
import kr.techit.lion.presentation.base.BaseViewModel
import kr.techit.lion.presentation.delegate.NetworkErrorDelegate
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.login.model.UserType
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val memberRepository: MemberRepository,
) : BaseViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate
    val networkState get() = networkErrorDelegate.networkState

    private val _state = MutableStateFlow(UserType.Checking)
    val state get() = _state.asStateFlow()

    fun onCompleteLogIn(type: String, accessToken: String, refreshToken: String) {
        viewModelScope.launch(recordExceptionHandler) {
            authRepository.signIn(type, accessToken, refreshToken)
            checkUserState()
        }
    }

    private fun checkUserState() {
        viewModelScope.launch {
            networkErrorDelegate.handleNetworkLoading()
            memberRepository.getConcernType().onSuccess { type ->
                modifyUserState(type)
                networkErrorDelegate.handleNetworkSuccess()
            }.onError {
                networkErrorDelegate.handleNetworkError(it)
            }
        }
    }

    private fun modifyUserState(type: ConcernType) {
        if (type.hasAnyTrue()) {
            _state.value = UserType.ExistingUser
        } else {
            _state.value = UserType.NewUser
        }
    }
}
