package kr.tekit.lion.presentation.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import kr.tekit.lion.presentation.databinding.ItemSearchSuggestionBinding
import kr.tekit.lion.presentation.main.adapter.viewholder.SearchSuggestionsViewHolder

class SearchSuggestionsAdapter(
    private val onClick: (String) -> Unit
) : ListAdapter<String, SearchSuggestionsViewHolder>(DIFF_CALLBACK) {

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
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }
}