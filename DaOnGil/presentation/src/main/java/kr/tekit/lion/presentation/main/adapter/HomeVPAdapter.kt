package kr.tekit.lion.presentation.main.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.tekit.lion.presentation.databinding.ItemHomeVpBinding

class HomeVPAdapter(private val images: List<Drawable>)
    : RecyclerView.Adapter<HomeVPAdapter.ImageViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding : ItemHomeVpBinding = ItemHomeVpBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return ImageViewHolder(binding)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position], position + 1)
    }

    class ImageViewHolder(private val binding: ItemHomeVpBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Drawable, currentPage: Int) {
            binding.itemHomeIv.setImageDrawable(image)
            binding.itemHomeCountTv.text = currentPage.toString()
        }
    }
}