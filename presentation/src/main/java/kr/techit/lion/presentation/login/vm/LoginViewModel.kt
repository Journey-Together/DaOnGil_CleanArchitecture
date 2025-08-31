package kr.techit.lion.presentation.login.vm

import dagger.hilt.android.lifecycle.HiltViewModel
import kr.techit.lion.domain.repository.AuthRepository
import kr.techit.lion.domain.repository.MemberRepository
import kr.techit.lion.presentation.base.BaseViewModel2
import kr.techit.lion.presentation.connectivity.ConnectivityObserver
import kr.techit.lion.presentation.delegate.NetworkEventDelegate
import kr.techit.lion.presentation.login.LoginUiEvent
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val memberRepository: MemberRepository,
    networkEventHandler: NetworkEventDelegate,
    connectivityObserver: ConnectivityObserver,
) : BaseViewModel2<LoginUiEvent>(networkEventHandler, connectivityObserver) {

    fun signIn(type: String, accessToken: String, refreshToken: String) = runAsync(
        action = { authRepository.signIn(type, accessToken, refreshToken) },
        onSuccess = { checkUserState() }
    )

    private fun checkUserState() = runAsync(
        action = { memberRepository.getConcernType() },
        onSuccess = { type ->
            if (type.anyTrue()) {
                emitEvent(LoginUiEvent.NavigateToMain)
            } else {
                emitEvent(LoginUiEvent.NavigateToSelectConcern)
            }
        }
    )
}
