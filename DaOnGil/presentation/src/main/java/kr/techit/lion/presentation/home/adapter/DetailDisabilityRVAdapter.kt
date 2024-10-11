package kr.techit.lion.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ItemDetailDisabilityImageBinding

class DetailDisabilityRVAdapter(private val disabilityList: List<Int>) :
    RecyclerView.Adapter<DetailDisabilityRVAdapter.DetailDisabilityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailDisabilityViewHolder {
        val binding: ItemDetailDisabilityImageBinding = ItemDetailDisabilityImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return DetailDisabilityViewHolder(binding)
    }

    override fun getItemCount(): Int = disabilityList.size

    override fun onBindViewHolder(holder: DetailDisabilityViewHolder, position: Int) {
        holder.bind(disabilityList[position])
    }

    class DetailDisabilityViewHolder(private val binding: ItemDetailDisabilityImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Int) {
            val imageResource = when (data) {
                1 -> R.drawable.detail_physical_disability_icon
                2 -> R.drawable.detail_visual_impairment_icon
                3 -> R.drawable.detail_hearing_impairment_icon
                4 -> R.drawable.detail_infant_family_icon
                else -> R.drawable.detail_elderly_people_icon
            }
            binding.itemDetailDisabilityIv.setImageResource(imageResource)
        }
    }
}