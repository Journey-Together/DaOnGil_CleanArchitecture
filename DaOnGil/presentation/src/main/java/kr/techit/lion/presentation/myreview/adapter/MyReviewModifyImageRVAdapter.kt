package kr.techit.lion.presentation.myreview.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ItemReviewWriteImageBinding

class MyReviewModifyImageRVAdapter(
    private val imageList : MutableList<Uri>,
    private val onDeleteImage: (Int) -> Unit
) : RecyclerView.Adapter<MyReviewModifyImageRVAdapter.MyReviewModifyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyReviewModifyViewHolder {
        val binding : ItemReviewWriteImageBinding = ItemReviewWriteImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return MyReviewModifyViewHolder(binding, this, onDeleteImage)
    }

    override fun getItemCount(): Int  = imageList.size

    override fun onBindViewHolder(holder: MyReviewModifyViewHolder, position: Int) {
        holder.bind(imageList[position], position)
    }

    class MyReviewModifyViewHolder(
        private val binding: ItemReviewWriteImageBinding,
        private val adapter : MyReviewModifyImageRVAdapter,
        private val onDeleteImage: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(image : Uri, position: Int) {
            Glide.with(binding.itemWriteReviewImage.context)
                .load(image)
                .error(R.drawable.empty_view_small)
                .into(binding.itemWriteReviewImage)

            binding.itemWriteReviewCancelIb.setOnClickListener {
                if (position != RecyclerView.NO_POSITION) {
                    adapter.imageList.removeAt(position)
                    adapter.notifyDataSetChanged()
                    onDeleteImage(position)
                }
            }
        }
    }
}