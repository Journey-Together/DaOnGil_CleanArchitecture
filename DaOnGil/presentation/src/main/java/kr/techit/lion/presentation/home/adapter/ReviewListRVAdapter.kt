package kr.techit.lion.presentation.home.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.techit.lion.domain.model.placereviewlist.PlaceReview
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ItemDetailReviewBigBinding
import kr.techit.lion.presentation.login.LoginActivity
import kr.techit.lion.presentation.main.dialog.ConfirmDialog
import kr.techit.lion.presentation.report.ReportActivity

class ReviewListRVAdapter(
    private val reviewList: List<PlaceReview>,
    private val loginState: Boolean,
    private val reportLauncher: ActivityResultLauncher<Intent>
) : RecyclerView.Adapter<ReviewListRVAdapter.ReviewListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewListViewHolder {
        val binding: ItemDetailReviewBigBinding = ItemDetailReviewBigBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ReviewListViewHolder(binding, loginState, reportLauncher)
    }

    override fun getItemCount(): Int = reviewList.size

    override fun onBindViewHolder(holder: ReviewListViewHolder, position: Int) {
        holder.bind(reviewList[position])
    }

    class ReviewListViewHolder(
        private val binding: ItemDetailReviewBigBinding,
        private val loginState: Boolean,
        private val reportLauncher: ActivityResultLauncher<Intent>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(review: PlaceReview) {
            binding.itemDetailReviewBigNickname.text = review.nickname
            binding.itemDetailReviewBigContent.text = review.content
            binding.itemDetailReviewBigDate.text = review.date.toString()
            binding.itemDetailReviewBigRatingbar.rating = review.grade

            Glide.with(binding.itemDetailReviewBigProfileIv.context)
                .load(review.profileImg)
                .error(R.drawable.default_profile)
                .into(binding.itemDetailReviewBigProfileIv)

            if (review.imageList != null) {
                binding.itemDetailReviewBigRv.visibility = View.VISIBLE

                val reviewImageRVAdapter = ReviewImageRVAdapter(review.imageList)
                binding.itemDetailReviewBigRv.adapter = reviewImageRVAdapter
                binding.itemDetailReviewBigRv.layoutManager =
                    LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            } else {
                binding.itemDetailReviewBigRv.visibility = View.GONE
            }

            if (review.myReview) {
                binding.itemDetailReviewBigReportBtn.visibility = View.GONE
            } else {
                binding.itemDetailReviewBigReportBtn.visibility = View.VISIBLE
            }

            binding.itemDetailReviewBigReportBtn.setOnClickListener {
                if (loginState) {
                    val context = binding.root.context

                    val intent = Intent(context, ReportActivity::class.java).apply {
                        putExtra("reviewType", "PlaceReview")
                        putExtra("reviewId", review.reviewId.toLong())
                    }
                    reportLauncher.launch(intent)
                } else {
                    displayLoginDialog("후기를 신고하고 싶다면\n 로그인을 진행해주세요", binding)
                }
            }
        }

        private fun displayLoginDialog(subtitle: String, binding: ItemDetailReviewBigBinding) {
            val context = binding.root.context
            val dialog = ConfirmDialog(
                "로그인이 필요해요!",
                subtitle,
                "로그인하기",
            ) {
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            }

            if (context is FragmentActivity) {
                dialog.isCancelable = true
                dialog.show(context.supportFragmentManager, "ScheduleLoginDialog")
            }
        }
    }
}