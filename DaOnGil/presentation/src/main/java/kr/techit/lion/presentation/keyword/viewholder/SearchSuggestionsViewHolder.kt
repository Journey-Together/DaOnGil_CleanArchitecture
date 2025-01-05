package kr.techit.lion.presentation.keyword.viewholder

import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.domain.model.search.AutoCompleteKeyword
import kr.techit.lion.presentation.databinding.ItemSearchSuggestionBinding

class SearchSuggestionsViewHolder(
    private val binding: ItemSearchSuggestionBinding,
    private val onClick: (AutoCompleteKeyword) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: AutoCompleteKeyword){
        binding.root.setOnClickListener {
            onClick(item)
        }
        binding.tvKeyword.text = item.keyword
    }
}