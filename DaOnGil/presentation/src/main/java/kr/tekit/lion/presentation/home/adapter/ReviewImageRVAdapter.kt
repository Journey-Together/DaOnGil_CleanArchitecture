package kr.tekit.lion.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ItemReviewBigImageBinding
import kr.tekit.lion.presentation.ext.setImageSmall
import kr.tekit.lion.presentation.ext.showPhotoDialog

class ReviewImageRVAdapter(
    private val imageList: List<String>,
    private val activity: AppCompatActivity
) :
    RecyclerView.Adapter<ReviewImageRVAdapter.ReviewImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewImageViewHolder {
        val binding: ItemReviewBigImageBinding = ItemReviewBigImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ReviewImageViewHolder(binding, activity)
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: ReviewImageViewHolder, position: Int) {
        holder.bind(imageList[position], imageList, position)
    }

    class ReviewImageViewHolder(
        val binding: ItemReviewBigImageBinding,
        val activity: AppCompatActivity
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String, imageList: List<String>, position: Int) {

            binding.root.context.setImageSmall(binding.reviewImage, image)

            binding.reviewImage.setOnClickListener {
                activity.baseContext.showPhotoDialog(
                    activity.supportFragmentManager, imageList, position
                )
            }
        }
    }
}