package kr.tekit.lion.presentation.home.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.tekit.lion.domain.model.detailplace.Review
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ItemDetailReviewBigBinding
import kr.tekit.lion.presentation.login.LoginActivity
import kr.tekit.lion.presentation.main.dialog.ConfirmDialog
import kr.tekit.lion.presentation.report.ReportActivity

class DetailReviewRVAdapter(
    private val reviewList: List<Review>,
    private val loginState: Boolean
) : RecyclerView.Adapter<DetailReviewRVAdapter.DetailReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailReviewViewHolder {
        val binding: ItemDetailReviewBigBinding = ItemDetailReviewBigBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return DetailReviewViewHolder(binding, loginState)
    }

    override fun getItemCount(): Int = reviewList.size

    override fun onBindViewHolder(holder: DetailReviewViewHolder, position: Int) {
        holder.bind(reviewList[position])
    }

    class DetailReviewViewHolder(
        private val binding: ItemDetailReviewBigBinding,
        private val loginState: Boolean
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

                    val reviewImageRVAdapter = ReviewImageRVAdapter(reviewData.reviewImgs!!)
                    itemDetailReviewBigRv.adapter = reviewImageRVAdapter
                    itemDetailReviewBigRv.layoutManager =
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

                itemDetailReviewBigReportBtn.setOnClickListener {
                    if (loginState) {
                        val context = binding.root.context

                        val intent = Intent(context, ReportActivity::class.java).apply {
                            putExtra("reviewType", "PlaceReview")
                            putExtra("reviewId", reviewData.reviewId)
                        }
                        context.startActivity(intent)
                    } else {
                        displayLoginDialog("후기를 신고하고 싶다면\n 로그인을 진행해주세요", binding)
                    }
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