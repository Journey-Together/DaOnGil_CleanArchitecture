package kr.tekit.lion.presentation.keyword.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import kr.tekit.lion.presentation.databinding.ItemPlaceHighBinding
import kr.tekit.lion.presentation.main.adapter.viewholder.PlaceHighViewHolder
import kr.tekit.lion.presentation.main.model.PlaceModel

class SearchResultAdapter(
    private val onSelectPlace: (Long) -> Unit
): ListAdapter<PlaceModel, PlaceHighViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceHighViewHolder {
        val binding = ItemPlaceHighBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceHighViewHolder(binding, onSelectPlace)
    }

    override fun onBindViewHolder(holder: PlaceHighViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PlaceModel>() {
            override fun areContentsTheSame(oldItem: PlaceModel, newItem: PlaceModel): Boolean {
                return oldItem.placeId == newItem.placeId
            }

            override fun areItemsTheSame(oldItem: PlaceModel, newItem: PlaceModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}