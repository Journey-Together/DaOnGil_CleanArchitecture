package kr.techit.lion.presentation.ext

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kr.techit.lion.presentation.R

fun View.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT){
    Snackbar.make(this, message, duration)
        .setBackgroundTint(ContextCompat.getColor(this.context, R.color.text_secondary))
        .show()
}