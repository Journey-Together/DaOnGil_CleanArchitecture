package kr.tekit.lion.presentation.myreview.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dagger.hilt.android.internal.managers.ViewComponentManager
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ItemReviewBigImageBinding
import kr.tekit.lion.presentation.ext.setImage
import kr.tekit.lion.presentation.ext.showPhotoDialog

class MyReviewImageRVAdapter(private val imageList: List<String>) :
    RecyclerView.Adapter<MyReviewImageRVAdapter.MyReviewImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyReviewImageViewHolder {
        val binding = ItemReviewBigImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return MyReviewImageViewHolder(binding)
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: MyReviewImageViewHolder, position: Int) {
        holder.bind(imageList[position], imageList, position)
    }

    class MyReviewImageViewHolder(
        private val binding: ItemReviewBigImageBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String, imageList: List<String>, position: Int) {

            binding.root.context.setImage(binding.reviewImage, image)

            binding.reviewImage.setOnClickListener {
                val context = binding.root.context

                // FragmentContextWrapper에서 FragmentActivity 추출
                val activityContext = (context as? ViewComponentManager.FragmentContextWrapper)?.baseContext as? FragmentActivity

                if (activityContext != null) {
                    val fragmentManager = activityContext.supportFragmentManager
                    activityContext.showPhotoDialog(fragmentManager, imageList, position)
                }
            }
        }
    }
}