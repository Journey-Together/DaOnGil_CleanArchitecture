package kr.techit.lion.presentation.splash.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.techit.lion.domain.model.Activation
import kr.techit.lion.domain.repository.ActivationRepository
import kr.techit.lion.domain.usecase.areacode.InitAreaCodeInfoUseCase
import kr.techit.lion.domain.usecase.base.onError
import kr.techit.lion.domain.usecase.base.onSuccess
import kr.techit.lion.presentation.delegate.NetworkEvent
import kr.techit.lion.presentation.delegate.NetworkEventDelegate
import kr.techit.lion.presentation.ext.stateInUi
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    activationRepository: ActivationRepository,
    private val initAreaCodeInfoUseCase: InitAreaCodeInfoUseCase,
    private val networkEventDelegate: NetworkEventDelegate
) : ViewModel() {

    val networkEvent = networkEventDelegate.event

    val userActivationState = activationRepository
        .activation
        .stateInUi(scope = viewModelScope, initialValue = Activation.Loading)

    suspend fun loadAreaCode() {
        initAreaCodeInfoUseCase()
            .onSuccess {
                networkEventDelegate.emitEvent(viewModelScope, NetworkEvent.Success)
            }
            .onError { exception ->
                networkEventDelegate.handleErrorEvent(viewModelScope, exception)
            }
    }
}
