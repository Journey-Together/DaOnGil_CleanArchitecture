package kr.techit.lion.presentation.keyword.viewholder

import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.domain.model.search.RecentlySearchKeyword
import kr.techit.lion.presentation.databinding.ItemRecentlySearchKeywordBinding

class RecentlyKeywordViewHolder(
    private val binding: ItemRecentlySearchKeywordBinding,
    private val onClick: (String) -> Unit,
    private val onClickDeleteBtn: (Long?) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: RecentlySearchKeyword){
        with(binding){
            imageButtonDelete.setOnClickListener {
                onClickDeleteBtn(item.id)
            }
            root.setOnClickListener{
                onClick(item.keyword)
            }
            textviewRecentSearch.text = item.keyword
        }
    }
}