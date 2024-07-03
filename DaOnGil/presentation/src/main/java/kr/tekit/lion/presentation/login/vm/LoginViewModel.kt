package kr.tekit.lion.presentation.login.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _sigInInUiState = MutableStateFlow(false)
    val sigInInUiState = _sigInInUiState.asStateFlow()

    fun onCompleteLogIn(type: String, token: String) = viewModelScope.launch {
        authRepository.signIn(type, "Bearer $token")
        _sigInInUiState.value = true
    }
}
