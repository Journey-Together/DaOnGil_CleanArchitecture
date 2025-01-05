package kr.techit.lion.presentation.splash.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.shareIn
import kr.techit.lion.domain.repository.ActivationRepository
import kr.techit.lion.domain.usecase.areacode.InitAreaCodeInfoUseCase
import kr.techit.lion.domain.usecase.base.onError
import kr.techit.lion.domain.usecase.base.onSuccess
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    activationRepository: ActivationRepository,
    private val initAreaCodeInfoUseCase: InitAreaCodeInfoUseCase,
): ViewModel() {

    private val _errorState = MutableStateFlow(false)
    val errorState get() = _errorState.asStateFlow()

    val userActivationState = activationRepository.userActivation.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000)
    )

    suspend fun whenUserActivationIsFirst(onComplete: () -> Unit){
        initAreaCodeInfoUseCase().onSuccess {
            onComplete()
        }.onError {
            _errorState.value = true
        }
    }
}