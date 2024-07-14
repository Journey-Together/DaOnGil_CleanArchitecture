package kr.tekit.lion.presentation.ext

import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

const val THROTTLE_DURATION = 2000L

fun View.clicks(): Flow<Unit> = callbackFlow {
    setOnClickListener {
        this.trySend(Unit)
    }
    awaitClose{ setOnClickListener(null) }
}

fun View.setClickEvent(
    uiScope: CoroutineScope,
    windowDuration: Long = THROTTLE_DURATION,
    onClick: () -> Unit
){
    clicks()
        .throttleFirst(windowDuration)
        .onEach { onClick.invoke() }
        .launchIn(uiScope)
}