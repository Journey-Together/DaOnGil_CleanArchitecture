package kr.techit.lion.presentation.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import kr.techit.lion.domain.model.search.RecentlySearchKeyword
import kr.techit.lion.presentation.databinding.ItemRecentlySearchKeywordBinding
import kr.techit.lion.presentation.keyword.viewholder.RecentlyKeywordViewHolder

class RecentlyKeywordAdapter(
    private val onClick: (String) -> Unit,
    private val onClickDeleteBtn: (Long?) -> Unit
): ListAdapter<RecentlySearchKeyword, RecentlyKeywordViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentlyKeywordViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RecentlyKeywordViewHolder(
            ItemRecentlySearchKeywordBinding.inflate(inflater, parent, false),
            onClick,
            onClickDeleteBtn
        )
    }

    override fun onBindViewHolder(holder: RecentlyKeywordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RecentlySearchKeyword>() {
            override fun areItemsTheSame(oldItem: RecentlySearchKeyword, newItem: RecentlySearchKeyword): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RecentlySearchKeyword, newItem: RecentlySearchKeyword
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}