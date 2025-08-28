package kr.techit.lion.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kr.techit.lion.domain.exception.NetworkError
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.presentation.delegate.NetworkEvent
import kr.techit.lion.domain.exception.Result
import kr.techit.lion.presentation.connectivity.ConnectivityObserver
import kr.techit.lion.presentation.delegate.NetworkEventDelegate

open class BaseViewModel2<UiEvent>(
    private val networkEventHandler: NetworkEventDelegate,
    private val connectivityObserver: ConnectivityObserver,
) : ViewModel() {

    private val recordExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseCrashlytics.getInstance().recordException(throwable)
        }
    }

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent get() = _uiEvent.receiveAsFlow()

    protected fun <T> runAsync(
        action: suspend () -> Result<T>,
        onSuccess: (T) -> Unit,
    ) {
        when (connectivityObserver.value()) {
            ConnectivityObserver.Status.Available -> launchNetworkOperation(action, onSuccess)
            else -> submitNetworkException()
        }
    }

    private fun <T> launchNetworkOperation(
        action: suspend () -> Result<T>,
        onSuccess: (T) -> Unit,
    ) {
        viewModelScope.launch(recordExceptionHandler) {
            networkEventHandler.event(viewModelScope, NetworkEvent.Loading)
            action().onSuccess {
                onSuccess(it)
                networkEventHandler.event(viewModelScope, NetworkEvent.Success)
            }.onError { throwable ->
                networkEventHandler.event(
                    viewModelScope,
                    NetworkEvent.Error(networkEventHandler.asUiText(throwable))
                )
            }
        }
    }

    private fun submitNetworkException() {
        networkEventHandler.submitThrowableEvent(viewModelScope, NetworkError.UnknownHostError)
    }

    protected fun emitEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}
