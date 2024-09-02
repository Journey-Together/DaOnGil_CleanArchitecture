package kr.tekit.lion.presentation.main.vm.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.repository.AuthRepository
import kr.tekit.lion.presentation.splash.model.LogInState
import javax.inject.Inject

@HiltViewModel
class ScheduleMainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _loginState = MutableStateFlow<LogInState>(LogInState.Checking)
    val loginState = _loginState.asStateFlow()

    init {
        viewModelScope.launch {
            checkLoginState()
        }
    }

    private fun checkLoginState() =
        viewModelScope.launch {
            authRepository.loggedIn.collect { isLoggedIn ->
                if (isLoggedIn) _loginState.value = LogInState.LoggedIn
                else _loginState.value = LogInState.LoginRequired
            }
        }
}