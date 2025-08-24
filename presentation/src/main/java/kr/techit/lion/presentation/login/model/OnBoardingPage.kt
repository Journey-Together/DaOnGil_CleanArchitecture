package kr.techit.lion.presentation.login.model

import android.graphics.drawable.Drawable

data class OnBoardingPage(
    val image: Drawable,
    val title: String,
    val description: String,
    val extra: String = EMPTY_EXTRA,
) {
    companion object {
        private const val EMPTY_EXTRA = ""
    }
}

