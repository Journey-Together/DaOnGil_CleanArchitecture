package kr.tekit.lion.presentation.splash.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.repository.AuthRepository
import kr.tekit.lion.domain.repository.ActivationRepository
import kr.tekit.lion.presentation.splash.model.LogInState
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val activationRepository: ActivationRepository
): ViewModel() {

    private val _logInState = MutableStateFlow<LogInState>(LogInState.Checking)
    val logInState = _logInState.asStateFlow()

    private val _userActivationState = MutableSharedFlow<Boolean>()
    val userActivationState = _userActivationState.asSharedFlow()

    init {
        viewModelScope.launch {
            checkFirstLogIn()
        }
    }

    suspend fun checkLoginStatus(){
        authRepository.loggedIn.collect{ isLoggedIn ->
            if (isLoggedIn) _logInState.value = LogInState.LoggedIn
            else _logInState.value = LogInState.LoginRequired
        }
    }

    private fun checkFirstLogIn() = viewModelScope.launch {
        activationRepository.userActivation.collect{
            _userActivationState.emit(it)
        }
    }
}