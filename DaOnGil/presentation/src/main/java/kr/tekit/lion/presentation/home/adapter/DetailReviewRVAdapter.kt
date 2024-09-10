package kr.tekit.lion.presentation.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.tekit.lion.domain.model.detailplace.Review
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ItemDetailReviewBigBinding
import kr.tekit.lion.presentation.home.DetailActivity

class DetailReviewRVAdapter(
    private val reviewList: List<Review>
) : RecyclerView.Adapter<DetailReviewRVAdapter.DetailReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailReviewViewHolder {
        val binding: ItemDetailReviewBigBinding = ItemDetailReviewBigBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return DetailReviewViewHolder(binding)
    }

    override fun getItemCount(): Int = reviewList.size

    override fun onBindViewHolder(holder: DetailReviewViewHolder, position: Int) {
        holder.bind(reviewList[position])
    }

    class DetailReviewViewHolder(
        private val binding: ItemDetailReviewBigBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reviewData: Review) {
            with(binding) {
                itemDetailReviewBigNickname.text = reviewData.nickname
                itemDetailReviewBigContent.text = reviewData.content
                itemDetailReviewBigDate.text = reviewData.date.toString()
                itemDetailReviewBigRatingbar.rating = reviewData.grade

                Glide.with(itemDetailReviewBigProfileIv.context)
                    .load(reviewData.profileImg)
                    .error(R.drawable.default_profile)
                    .into(itemDetailReviewBigProfileIv)

                if (reviewData.reviewImgs != null) {
                    itemDetailReviewBigRv.visibility = View.VISIBLE

                    val reviewImageRVAdapter =
                        ReviewImageRVAdapter(reviewData.reviewImgs!!)
                    binding.itemDetailReviewBigRv.adapter = reviewImageRVAdapter
                    binding.itemDetailReviewBigRv.layoutManager =
                        LinearLayoutManager(
                            binding.root.context,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                } else {
                    itemDetailReviewBigRv.visibility = View.GONE
                }

                if (reviewData.myReview) {
                    itemDetailReviewBigReportBtn.visibility = View.GONE
                } else {
                    itemDetailReviewBigReportBtn.visibility = View.VISIBLE
                }
            }
        }
    }
}