package kr.tekit.lion.presentation.ext

import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView

fun TextView.setAccessibilityText(newText: CharSequence) {
    accessibilityDelegate = object : View.AccessibilityDelegate() {
        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            if (host is TextView) {
                info.text = newText
            }
        }
    }
}