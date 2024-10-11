package kr.techit.lion.presentation.myreview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ItemPhotoVpBinding

class PhotoVPAdapter(
    private val imageList: List<String>
) : RecyclerView.Adapter<PhotoVPAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding : ItemPhotoVpBinding = ItemPhotoVpBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return PhotoViewHolder(binding)
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    class PhotoViewHolder(private val binding: ItemPhotoVpBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String) {
            Glide.with(binding.photoView.context)
                .load(image)
                .error(R.drawable.empty_view_small)
                .into(binding.photoView)
        }
    }
}