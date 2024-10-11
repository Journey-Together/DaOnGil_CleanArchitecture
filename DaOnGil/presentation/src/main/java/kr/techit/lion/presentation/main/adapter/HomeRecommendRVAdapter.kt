package kr.techit.lion.presentation.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.techit.lion.domain.model.mainplace.RecommendPlace
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ItemTouristRecommendBinding

class HomeRecommendRVAdapter(
    private val recommendPlaceList: List<RecommendPlace>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<HomeRecommendRVAdapter.RecommendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendViewHolder {
        val binding: ItemTouristRecommendBinding = ItemTouristRecommendBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return RecommendViewHolder(binding, onClick)
    }

    override fun getItemCount(): Int = 3

    override fun onBindViewHolder(holder: RecommendViewHolder, position: Int) {
        holder.bind(recommendPlaceList[position])
    }

    class RecommendViewHolder(
        val binding: ItemTouristRecommendBinding,
        private val onClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onClick.invoke(absoluteAdapterPosition)
            }
        }
        fun bind(recommendPlace: RecommendPlace) {
            binding.touristRecommendAddressTv.text = recommendPlace.address
            binding.touristRecommendNameTv.text = recommendPlace.name

            val disabilityList = recommendPlace.disability

            Glide.with(binding.touristRecommendIv.context)
                .load(recommendPlace.image)
                .error(R.drawable.empty_view)
                .into(binding.touristRecommendIv)

            val disabilityRVAdapter = DisabilityRVAdapter(disabilityList)
            binding.touristRecommendRv.adapter = disabilityRVAdapter
            binding.touristRecommendRv.layoutManager =
                LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
        }
    }
}