package kr.tekit.lion.presentation.splash.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.repository.AuthRepository
import kr.tekit.lion.domain.usecase.areacode.InitAreaCodeInfoUseCase
import kr.tekit.lion.presentation.splash.model.LogInState
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val initAreaCodeInfoUseCase: InitAreaCodeInfoUseCase
): ViewModel() {

    private val _logInState = MutableStateFlow<LogInState>(LogInState.Checking)
    val logInState = _logInState.asStateFlow()

    init {
        viewModelScope.launch {
            initAreaCodeInfoUseCase()
            checkLoginStatus()
        }
    }

    private suspend fun checkLoginStatus(){
        authRepository.loggedIn.collectLatest{ isLoggedIn ->
            if (isLoggedIn) _logInState.value = LogInState.LoggedIn
            else _logInState.value = LogInState.LoginRequired
        }
    }
}