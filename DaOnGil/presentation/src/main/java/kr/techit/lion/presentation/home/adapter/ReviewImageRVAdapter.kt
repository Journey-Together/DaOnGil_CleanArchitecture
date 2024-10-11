package kr.techit.lion.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.presentation.databinding.ItemReviewBigImageBinding
import kr.techit.lion.presentation.ext.setImageSmall
import kr.techit.lion.presentation.ext.showPhotoDialog

class ReviewImageRVAdapter(
    private val imageList: List<String>
) :
    RecyclerView.Adapter<ReviewImageRVAdapter.ReviewImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewImageViewHolder {
        val binding: ItemReviewBigImageBinding = ItemReviewBigImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ReviewImageViewHolder(binding)
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: ReviewImageViewHolder, position: Int) {
        holder.bind(imageList[position], imageList, position)
    }

    class ReviewImageViewHolder(
        val binding: ItemReviewBigImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String, imageList: List<String>, position: Int) {

            binding.root.context.setImageSmall(binding.reviewImage, image)

            binding.reviewImage.setOnClickListener {
                val context = binding.root.context

                if (context is FragmentActivity) {
                    val fragmentManager = context.supportFragmentManager
                    context.showPhotoDialog(fragmentManager, imageList, position)
                }
            }
        }
    }
}