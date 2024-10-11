package kr.techit.lion.presentation.main.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.presentation.databinding.ItemNoPlaceBinding

class NoPlaceViewHolder(private val binding: ItemNoPlaceBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(msg: String){
        binding.textMsg.text = msg
    }
}