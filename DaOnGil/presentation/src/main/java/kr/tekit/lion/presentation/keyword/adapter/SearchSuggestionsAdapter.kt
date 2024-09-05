package kr.tekit.lion.presentation.keyword.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import kr.tekit.lion.domain.model.search.AutoCompleteKeyword
import kr.tekit.lion.presentation.databinding.ItemSearchSuggestionBinding
import kr.tekit.lion.presentation.keyword.viewholder.SearchSuggestionsViewHolder

class SearchSuggestionsAdapter(
    private val onClick: (String) -> Unit
) : ListAdapter<AutoCompleteKeyword, SearchSuggestionsViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchSuggestionsViewHolder {
        return SearchSuggestionsViewHolder(
            ItemSearchSuggestionBinding.inflate(
                LayoutInflater.from(parent.context)
            ),
            onClick
        )
    }

    override fun onBindViewHolder(holder: SearchSuggestionsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AutoCompleteKeyword>() {
            override fun areContentsTheSame(oldItem: AutoCompleteKeyword, newItem: AutoCompleteKeyword): Boolean {
                return oldItem.placeId == newItem.placeId
            }

            override fun areItemsTheSame(oldItem: AutoCompleteKeyword, newItem: AutoCompleteKeyword): Boolean {
                return oldItem == newItem
            }
        }
    }
}