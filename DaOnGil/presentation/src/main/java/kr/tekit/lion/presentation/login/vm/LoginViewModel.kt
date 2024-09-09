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
import kr.tekit.lion.domain.repository.ActivationRepository
import kr.tekit.lion.domain.repository.MemberRepository
import kr.tekit.lion.domain.usecase.areacode.InitAreaCodeInfoUseCase
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val memberRepository: MemberRepository,
    private val initAreaCodeInfoUseCase: InitAreaCodeInfoUseCase,
    private val activationRepository: ActivationRepository
) : ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    init {
        viewModelScope.launch {
            initAreaCodeInfoUseCase()
            checkIsFirstUser()
        }
    }

    val networkState get() = networkErrorDelegate.networkState

    private val _sigInInUiState = MutableStateFlow(false)
    val sigInInUiState = _sigInInUiState.asStateFlow()

    private val _isFirstUser = MutableStateFlow(false)
    val isFirstUser = _isFirstUser.asStateFlow()

    fun onCompleteLogIn(type: String, token: String) = viewModelScope.launch {
        activationRepository.saveUserActivation(true)
        authRepository.signIn(type, "Bearer $token")
        _sigInInUiState.value = true
    }

    private fun checkIsFirstUser() = viewModelScope.launch {
        memberRepository.getConcernType().onSuccess { ConcernType ->
            _isFirstUser.value = ConcernType.hasAnyTrue()
        }.onError {
            networkErrorDelegate.handleNetworkError(it)
        }
    }
}
