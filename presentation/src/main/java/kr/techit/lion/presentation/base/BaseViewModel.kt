package kr.techit.lion.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.presentation.delegate.NetworkEvent
import kr.techit.lion.domain.exception.Result
import kr.techit.lion.presentation.delegate.NetworkEventDelegate

open class BaseViewModel : ViewModel() {

    protected val recordExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseCrashlytics.getInstance().recordException(throwable)
        }
    }

    protected fun <T> execute(
        action: suspend () -> Result<T>,
        eventHandler: NetworkEventDelegate,
        onSuccess: (T) -> Unit,
    ) {
        viewModelScope.launch(recordExceptionHandler){
            eventHandler.emitEvent(viewModelScope, NetworkEvent.Loading)
            action().onSuccess {
                onSuccess(it)
                eventHandler.emitEvent(viewModelScope, NetworkEvent.Success)
            }.onError { throwable ->
                eventHandler.emitEvent(
                    viewModelScope,
                    NetworkEvent.Error(eventHandler.asUiText(throwable))
                )
            }
        }
    }
}
