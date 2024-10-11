package kr.techit.lion.presentation.login.vm

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.domain.model.hasAnyTrue
import kr.techit.lion.domain.repository.AuthRepository
import kr.techit.lion.domain.repository.MemberRepository
import kr.techit.lion.presentation.base.BaseViewModel
import kr.techit.lion.presentation.delegate.NetworkErrorDelegate
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val memberRepository: MemberRepository,
) : BaseViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    val networkState get() = networkErrorDelegate.networkState

    private val _sigInInUiState = MutableStateFlow(false)
    val sigInInUiState = _sigInInUiState.asStateFlow()

    private val _isFirstUser = MutableStateFlow(false)
    val isFirstUser = _isFirstUser.asStateFlow()

    fun onCompleteLogIn(type: String, accessToken: String, refreshToken: String){
        viewModelScope.launch(recordExceptionHandler) {
            authRepository.signIn(type, accessToken, refreshToken)
            checkIsFirstUser()
            _sigInInUiState.value = true
        }
    }

    private suspend fun checkIsFirstUser(){
        memberRepository.getConcernType().onSuccess { ConcernType ->
            _isFirstUser.value = ConcernType.hasAnyTrue()
            networkErrorDelegate.handleNetworkSuccess()
        }.onError {
            networkErrorDelegate.handleNetworkError(it)
        }
    }
}
