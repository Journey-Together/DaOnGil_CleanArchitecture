package kr.tekit.lion.presentation.main.adapter.viewholder

import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.RecyclerView
import kr.tekit.lion.presentation.databinding.ItemListSearchSortBinding
import kr.tekit.lion.presentation.main.model.SortModel

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
    }
}