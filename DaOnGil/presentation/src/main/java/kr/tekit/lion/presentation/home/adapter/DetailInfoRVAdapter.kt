package kr.tekit.lion.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.tekit.lion.domain.model.detailplace.SubDisability
import kr.tekit.lion.presentation.databinding.ItemDetailServiceInfoBinding

class DetailInfoRVAdapter(private val infoList: List<SubDisability>) :
    RecyclerView.Adapter<DetailInfoRVAdapter.DetailInfoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailInfoViewHolder {
        val binding: ItemDetailServiceInfoBinding = ItemDetailServiceInfoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return DetailInfoViewHolder(binding)
    }

    override fun getItemCount(): Int = infoList.size

    override fun onBindViewHolder(holder: DetailInfoViewHolder, position: Int) {
        holder.bind(infoList[position])
    }

    class DetailInfoViewHolder(private val binding: ItemDetailServiceInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(infoData: SubDisability) {
            binding.itemDetailInfoTitleTv.text = infoData.subDisabilityName
            binding.itemDetailInfoContentTv.text = infoData.description
        }
    }
}