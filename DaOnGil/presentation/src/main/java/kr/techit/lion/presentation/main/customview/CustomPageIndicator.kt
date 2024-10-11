package kr.techit.lion.presentation.main.customview

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.ext.dpToPx

class CustomPageIndicator(
    private val context: Context,
    indicatorLayout: LinearLayout,
    itemCount: Int
) {
    private val indicators = ArrayList<ImageView>()

    init {
        indicatorLayout.removeAllViews()

        for (i in 0 until itemCount) {
            val dot = ImageView(context)
            val params = LinearLayout.LayoutParams(
                6.dpToPx(context),
                6.dpToPx(context)
            )
            params.setMargins(6.dpToPx(context), 0, 6.dpToPx(context), 0)
            dot.layoutParams = params
            dot.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.indicator_unselected))
            indicators.add(dot)
            indicatorLayout.addView(dot)
        }
    }

    fun onPageSelected(position: Int) {
        for (i in indicators.indices) {
            indicators[i].setImageDrawable(
                ContextCompat.getDrawable(context, if (i == position) R.drawable.indicator_selected else R.drawable.indicator_unselected)
            )
        }
    }
}