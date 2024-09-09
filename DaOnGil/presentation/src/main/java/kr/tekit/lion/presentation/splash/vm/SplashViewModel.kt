package kr.tekit.lion.presentation.splash.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.repository.ActivationRepository
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val activationRepository: ActivationRepository
): ViewModel() {

    private val _userActivationState = MutableSharedFlow<Boolean>()
    val userActivationState = _userActivationState.asSharedFlow()

    init {
        viewModelScope.launch {
            checkFirstLogIn()
        }
    }

    private fun checkFirstLogIn() = viewModelScope.launch {
        activationRepository.userActivation.collect{
            _userActivationState.emit(it)
        }
    }
}