package kr.techit.lion.presentation.delegate

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kr.techit.lion.domain.exception.NetworkError.TimeoutError
import kr.techit.lion.domain.exception.NetworkError.UnknownError
import kr.techit.lion.domain.exception.NetworkError.UnknownHostError
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

class NetworkEventDelegate @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _event = Channel<NetworkEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    fun handleErrorEvent(scope: CoroutineScope, e: Throwable) {
        when (e) {
            is TimeoutException -> emitEvent(scope, NetworkEvent.Error(asUiText(TimeoutError)))
            is UnknownHostException -> emitEvent(scope, NetworkEvent.Error(asUiText(UnknownHostError)))
            is UnknownError -> emitEvent(scope, NetworkEvent.Error(asUiText(UnknownError)))
            else -> emitEvent(scope, NetworkEvent.Error(asUiText(e)))
        }
    }

    fun asUiText(exception: Throwable): String{
        return UiTextProvider(context).asUiText(exception)
    }

    fun emitEvent(scope: CoroutineScope, event: NetworkEvent) {
        scope.launch {
            _event.send(event)
        }
    }
}

sealed class NetworkEvent{
    data object Loading: NetworkEvent()
    data object Success: NetworkEvent()
    data class Error(val msg: String): NetworkEvent()
}
