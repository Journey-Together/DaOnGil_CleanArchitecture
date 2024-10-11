package kr.techit.lion.presentation.schedulereview.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ItemReviewWriteImageBinding

class WriteReviewImageAdapter(
    private val images: List<Uri>,
    private val imageRemoveListener: (imagePosition: Int) -> Unit
) : RecyclerView.Adapter<WriteReviewImageAdapter.WriteScheduleReviewImageViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WriteScheduleReviewImageViewHolder {
        val binding: ItemReviewWriteImageBinding = ItemReviewWriteImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return WriteScheduleReviewImageViewHolder(binding, imageRemoveListener)
    }

    override fun onBindViewHolder(holder: WriteScheduleReviewImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size
    class WriteScheduleReviewImageViewHolder(
        private val binding: ItemReviewWriteImageBinding,
        private val imageRemoveListener: (imagePosition: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.itemWriteReviewCancelIb.setOnClickListener {
                imageRemoveListener(absoluteAdapterPosition)
            }
        }

        fun bind(image: Uri) {
            Glide.with(binding.itemWriteReviewImage.context)
                .load(image)
                .error(R.drawable.empty_view_small)
                .into(binding.itemWriteReviewImage)
        }
    }
}