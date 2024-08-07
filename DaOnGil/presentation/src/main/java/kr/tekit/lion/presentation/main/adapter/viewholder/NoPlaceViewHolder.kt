package kr.tekit.lion.presentation.main.adapter.multi_viewholder

import androidx.recyclerview.widget.RecyclerView
import kr.tekit.lion.presentation.databinding.ItemNoPlaceBinding

class NoPlaceViewHolder(private val binding: ItemNoPlaceBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(msg: String){
        binding.textMsg.text = msg
    }
}