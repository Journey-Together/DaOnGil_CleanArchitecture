package kr.tekit.lion.presentation.login.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.model.hasAnyTrue
import kr.tekit.lion.domain.repository.AuthRepository
import kr.tekit.lion.domain.repository.MemberRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val memberRepository: MemberRepository,
) : ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    val networkState get() = networkErrorDelegate.networkState

    private val _sigInInUiState = MutableStateFlow(false)
    val sigInInUiState = _sigInInUiState.asStateFlow()

    private val _isFirstUser = MutableStateFlow(false)
    val isFirstUser = _isFirstUser.asStateFlow()

    fun onCompleteLogIn(type: String, token: String) = viewModelScope.launch {
        authRepository.signIn(type, token)
        checkIsFirstUser()
        _sigInInUiState.value = true
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
