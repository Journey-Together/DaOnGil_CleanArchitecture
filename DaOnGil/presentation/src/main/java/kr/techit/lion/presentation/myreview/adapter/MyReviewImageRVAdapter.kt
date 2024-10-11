package kr.techit.lion.presentation.myreview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.internal.managers.ViewComponentManager
import kr.techit.lion.presentation.databinding.ItemReviewBigImageBinding
import kr.techit.lion.presentation.ext.setImageSmall
import kr.techit.lion.presentation.ext.showPhotoDialog

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

            binding.root.context.setImageSmall(binding.reviewImage, image)

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