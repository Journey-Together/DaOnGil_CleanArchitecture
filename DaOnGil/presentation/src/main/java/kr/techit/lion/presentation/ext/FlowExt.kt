package kr.techit.lion.presentation.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

fun <T> Flow<T>.throttleFirst(periodMillis: Long): Flow<T>{
    require(periodMillis > 0) { "period should be positive" }
    return flow{
        var lastTime = 0L
        collect{ value ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTime >= periodMillis){
                lastTime = currentTime
                emit(value)
            }
        }
    }
}

fun <T> Flow<T>.stateInUi(
    scope: CoroutineScope,
    initialValue: T
) = stateIn(scope, SharingStarted.WhileSubscribed(5000), initialValue)

fun <T> Flow<T>.shareInUi(
    scope: CoroutineScope,
) = shareIn(scope, SharingStarted.WhileSubscribed(5000))