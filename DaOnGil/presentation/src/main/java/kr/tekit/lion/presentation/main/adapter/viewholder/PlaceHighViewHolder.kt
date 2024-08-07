package kr.tekit.lion.presentation.main.adapter.multi_viewholder

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ItemPlaceHighBinding
import kr.tekit.lion.presentation.main.adapter.DisabilityRVAdapter
import kr.tekit.lion.presentation.main.model.PlaceModel

class PlaceHighViewHolder(private val binding: ItemPlaceHighBinding)
    : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: PlaceModel) {
        with(binding) {
            tvName.text = item.placeName
            tvAddr.text = item.placeAddr
            val disabilityList = item.disability

            Glide.with(binding.root)
                .load(item.placeImg)
                .placeholder(R.drawable.empty_view_small)
                .error(R.drawable.empty_view_small)
                .into(thumbnailImg)

            val disabilityRVAdapter = DisabilityRVAdapter(disabilityList)
            binding.touristHighDisabilityRv.adapter = disabilityRVAdapter
            binding.touristHighDisabilityRv.layoutManager = LinearLayoutManager(
                root.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }
}