package kr.techit.lion.presentation.ext

import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.addOnScrollEndListener(
    threshold: Int = 3,
    callback: () -> Unit,
) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == SCROLL_STATE_IDLE && recyclerView.hasLessItemThan(threshold)) {
                callback.invoke()
            }
        }

        private fun RecyclerView.hasLessItemThan(threshold: Int): Boolean {
            if (isLayoutRequested) {
                return false
            }
            (layoutManager as? LinearLayoutManager)?.let {
                val lastVisibleItem = it.findLastVisibleItemPosition()
                return lastVisibleItem >= it.itemCount - threshold
            }
            return false
        }
    })
}

fun RecyclerView.gridAddOnScrollEndListener(
    threshold: Int = 4,
    callback: () -> Unit,
) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == SCROLL_STATE_IDLE && recyclerView.hasLessItemThan(threshold)) {
                callback.invoke()
            }
        }

        private fun RecyclerView.hasLessItemThan(threshold: Int): Boolean {
            if (isLayoutRequested) {
                return false
            }
            (layoutManager as? GridLayoutManager)?.let {
                val lastVisibleItem = it.findLastVisibleItemPosition()
                return lastVisibleItem >= it.itemCount - threshold
            }
            return false
        }
    })
}