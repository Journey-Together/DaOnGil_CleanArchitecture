package kr.techit.lion.presentation.main.customview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemOffsetDecoration(private val startPadding: Int, private val endPadding: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        when (position) {
            0 -> outRect.set(startPadding, 0, 0, 0) // 첫 번째 아이템
            itemCount - 1 -> outRect.set(0, 0, endPadding, 0) // 마지막 아이템
            else -> outRect.set(0, 0, 0, 0) // 나머지 아이템
        }
    }
}