package kr.techit.lion.presentation.ext

import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.ImageView
import android.widget.TextView
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

fun View.setAccessibilityText(newText: CharSequence) {
    accessibilityDelegate = object : View.AccessibilityDelegate() {
        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            info.hintText = null
            if (host is TextView) {
                info.text = newText
            } else if (host is ImageView) {
                info.contentDescription = newText
            }
        }
    }
}

fun View.updateVisibility(isVisible: Boolean, vararg views: View) {
    views.forEach {
        it.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}