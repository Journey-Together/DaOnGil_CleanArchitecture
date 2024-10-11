package kr.techit.lion.presentation.ext

import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView

/**
 * TextView의 접근성 텍스트를 설정합니다.
 *
 * @param newText 접근성 텍스트로 설정할 새로운 텍스트
 */
fun TextView.setAccessibilityText(newText: CharSequence) {
    // 접근성 위임 객체 설정
    accessibilityDelegate = object : View.AccessibilityDelegate() {
        /**
         * 접근성 노드 정보를 초기화합니다.
         * 접근성 서비스가 뷰에 대한 정보를 요청할 때 호출됩니다.
         *
         * @param host 접근성 정보를 요청하는 뷰
         * @param info 접근성 노드 정보 객체
         */
        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            // 호스트가 TextView인 경우 접근성 텍스트 설정
            if (host is TextView) {
                info.hintText = null
                info.text = newText
            }
        }
    }
}