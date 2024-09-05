package kr.tekit.lion.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ItemReviewBigImageBinding

class ReviewImageRVAdapter(private val imageList: List<String>) :
    RecyclerView.Adapter<ReviewImageRVAdapter.ReviewImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewImageViewHolder {
        val binding: ItemReviewBigImageBinding = ItemReviewBigImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ReviewImageViewHolder(binding)
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: ReviewImageViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    class ReviewImageViewHolder(
        val binding: ItemReviewBigImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String) {
            Glide.with(binding.reviewImage.context)
                .load(image)
                .error(R.drawable.empty_view_small)
                .into(binding.reviewImage)
        }
    }
}