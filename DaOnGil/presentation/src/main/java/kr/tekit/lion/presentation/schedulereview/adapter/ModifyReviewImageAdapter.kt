package kr.tekit.lion.presentation.schedulereview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ItemReviewWriteImageBinding
import kr.tekit.lion.presentation.model.ReviewImage

class ModifyReviewImageAdapter (
    private val images : List<ReviewImage>,
    private val imageRemoveListener: (imagePosition: Int) -> Unit
) : RecyclerView.Adapter<ModifyReviewImageAdapter.ModifyScheduleReviewImageViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ModifyScheduleReviewImageViewHolder {
        val binding: ItemReviewWriteImageBinding = ItemReviewWriteImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ModifyScheduleReviewImageViewHolder(binding, imageRemoveListener)
    }

    override fun onBindViewHolder(holder: ModifyScheduleReviewImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int  = images.size

    class ModifyScheduleReviewImageViewHolder(
        private val binding: ItemReviewWriteImageBinding,
        private val imageRemoveListener: (imagePosition: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.itemWriteReviewCancelIb.setOnClickListener {
                imageRemoveListener(absoluteAdapterPosition)
            }
        }

        fun bind(reviewImage: ReviewImage) {
            Glide.with(binding.itemWriteReviewImage.context)
                .load(reviewImage.imageUri)
                .error(R.drawable.empty_view_small)
                .into(binding.itemWriteReviewImage)
        }
    }
}
