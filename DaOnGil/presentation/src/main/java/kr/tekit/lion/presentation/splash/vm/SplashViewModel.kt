package kr.tekit.lion.presentation.splash.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.repository.ActivationRepository
import kr.tekit.lion.domain.usecase.areacode.InitAreaCodeInfoUseCase
import kr.tekit.lion.domain.usecase.base.onError
import kr.tekit.lion.domain.usecase.base.onSuccess
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    activationRepository: ActivationRepository,
    private val initAreaCodeInfoUseCase: InitAreaCodeInfoUseCase,
): ViewModel() {

    private val _err = MutableSharedFlow<Boolean>()
    val err = _err.asSharedFlow()

    val userActivationState = activationRepository.userActivation.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000)
    )

    suspend fun whenUserActivationIsFirst(onComplete: () -> Unit){
        initAreaCodeInfoUseCase().onSuccess {
            onComplete()
        }.onError {
            viewModelScope.launch {
                _err.emit(true)
            }
        }
    }
}