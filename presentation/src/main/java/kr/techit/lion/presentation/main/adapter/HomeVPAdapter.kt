package kr.techit.lion.presentation.main.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ItemHomeVpBinding

class HomeVPAdapter(
    private val images: List<Drawable>,
    private val onItemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<HomeVPAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding: ItemHomeVpBinding = ItemHomeVpBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ImageViewHolder(binding, onItemClickListener)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position], position + 1)
    }

    class ImageViewHolder(
        private val binding: ItemHomeVpBinding,
        private val onItemClickListener: (Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Drawable, currentPage: Int) {
            binding.itemHomeIv.setImageDrawable(image)
            binding.itemHomeCountTv.text = currentPage.toString()

            // 부모 view에만 contentDescription 설정
            with(binding.root) {
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
                contentDescription = when(bindingAdapterPosition) {
                    0 -> binding.root.context.getString(R.string.banner_place_search_desc)
                    1 -> binding.root.context.getString(R.string.banner_emergency_desc)
                    2 -> binding.root.context.getString(R.string.banner_schedule_desc)
                    else -> ""
                }
            }

            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    onItemClickListener(bindingAdapterPosition)
                }
            }
        }
    }
}