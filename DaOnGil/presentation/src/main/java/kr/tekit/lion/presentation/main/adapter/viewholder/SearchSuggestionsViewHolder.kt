package kr.tekit.lion.presentation.main.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import kr.tekit.lion.presentation.databinding.ItemSearchSuggestionBinding

class SearchSuggestionsViewHolder(
    private val binding: ItemSearchSuggestionBinding,
    private val onClick: (String) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: String){
        binding.root.setOnClickListener {
            onClick(item)
        }

        binding.tvKeyword.text = item
    }
}