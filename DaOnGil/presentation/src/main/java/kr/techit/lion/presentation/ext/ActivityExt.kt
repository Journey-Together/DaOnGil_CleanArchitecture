package kr.techit.lion.presentation.ext

import android.app.Activity
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar

fun Activity.updateToolbarColors(toolbarTitle: TextView, toolbar: MaterialToolbar, @ColorRes colorRes: Int) {
    toolbarTitle.setTextColor(ContextCompat.getColor(this, colorRes))
    toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, colorRes))
}
