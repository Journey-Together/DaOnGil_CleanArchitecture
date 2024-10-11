package kr.techit.lion.presentation.main.adapter.viewholder

import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.presentation.databinding.ItemListSearchSortBinding
import kr.techit.lion.presentation.ext.announceForAccessibility
import kr.techit.lion.presentation.ext.isTallBackEnabled
import kr.techit.lion.presentation.main.model.SortModel

class ItemCountViewHolder(
    private val binding: ItemListSearchSortBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: SortModel){
        val itemCount = item.totalItemCount
        with(binding){
            if (itemCount == 0){
                sortContainer.visibility = GONE
            }else{
                sortContainer.visibility = VISIBLE
                binding.totalCnt.text = itemCount.toString()
            }
        }
        val context = binding.root.context
        if (context.isTallBackEnabled()) {
            context.announceForAccessibility("총 ${itemCount}개의 장소를 찾았습니다.")
        }
    }
}