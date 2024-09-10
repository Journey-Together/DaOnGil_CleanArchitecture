package kr.tekit.lion.presentation.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.domain.model.placereviewlist.PlaceReview
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ActivityReviewListBinding
import kr.tekit.lion.presentation.ext.addOnScrollEndListener
import kr.tekit.lion.presentation.ext.repeatOnStarted
import kr.tekit.lion.presentation.home.adapter.ReviewListRVAdapter
import kr.tekit.lion.presentation.home.vm.ReviewListViewModel
import kr.tekit.lion.presentation.main.dialog.ConfirmDialog
import kr.tekit.lion.presentation.splash.model.LogInState

@AndroidEntryPoint
class ReviewListActivity : AppCompatActivity() {
    private val viewModel: ReviewListViewModel by viewModels()
    private val binding: ActivityReviewListBinding by lazy {
        ActivityReviewListBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val placeId = intent.getLongExtra("reviewPlaceId", -1)

        settingToolbar()

        repeatOnStarted {
            viewModel.loginState.collect { uiState ->
                when (uiState) {
                    is LogInState.Checking -> {
                        return@collect
                    }

                    is LogInState.LoggedIn -> {
                        getReviewListInfo(placeId)
                    }

                    is LogInState.LoginRequired -> {
                        getReviewListInfoGuest(placeId)
                    }
                }
            }
        }

    }

    private fun settingToolbar() {
        binding.reviewListToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun settingReviewListRVAdapter(reviewList: List<PlaceReview>) {
        val reviewListRVAdapter = ReviewListRVAdapter(reviewList) {
            val dialog = ConfirmDialog(
                "신고하기",
                "해당 댓글을 신고하시겠습니까?",
                "신고하기"
            ) {
                // 신고하기 api 연결
            }
            dialog.isCancelable = false
            dialog.show(supportFragmentManager, "PlaceReviewListDialog")
        }

        binding.reviewListRv.adapter = reviewListRVAdapter
        binding.reviewListRv.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun getReviewListInfo(placeId: Long) {
        viewModel.getPlaceReview(placeId)

        binding.reviewListRv.addOnScrollEndListener {
            if (viewModel.isLastPage.value == false) {
                viewModel.getNewPlaceReview(placeId)
            }
        }

        viewModel.placeReviewInfo.observe(this@ReviewListActivity) { placeReviewInfo ->
            binding.reviewListTitleTv.text = placeReviewInfo.placeName
            binding.reviewListAddressTv.text = placeReviewInfo.placeAddress
            binding.reviewListCount2Tv.text = placeReviewInfo.reviewNum.toString()

            Glide.with(binding.reviewListThumbnailIv)
                .load(placeReviewInfo.placeImg)
                .error(R.drawable.empty_view)
                .into(binding.reviewListThumbnailIv)

            settingReviewListRVAdapter(placeReviewInfo.placeReviewList)
        }
    }

    private fun getReviewListInfoGuest(placeId: Long) {
        viewModel.getPlaceReviewGuest(placeId)

        binding.reviewListRv.addOnScrollEndListener {
            if (viewModel.isLastPage.value == false) {
                viewModel.getNewPlaceReviewGuest(placeId)
            }
        }

        viewModel.placeReviewInfo.observe(this@ReviewListActivity) { placeReviewInfo ->
            binding.reviewListTitleTv.text = placeReviewInfo.placeName
            binding.reviewListAddressTv.text = placeReviewInfo.placeAddress
            binding.reviewListCount2Tv.text = placeReviewInfo.reviewNum.toString()

            Glide.with(binding.reviewListThumbnailIv)
                .load(placeReviewInfo.placeImg)
                .error(R.drawable.empty_view)
                .into(binding.reviewListThumbnailIv)

            settingReviewListRVAdapter(placeReviewInfo.placeReviewList)
        }
    }
}