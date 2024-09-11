package kr.tekit.lion.presentation.home

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.model.placereviewlist.PlaceReview
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ActivityReviewListBinding
import kr.tekit.lion.presentation.delegate.NetworkState
import kr.tekit.lion.presentation.ext.addOnScrollEndListener
import kr.tekit.lion.presentation.ext.repeatOnStarted
import kr.tekit.lion.presentation.ext.showSnackbar
import kr.tekit.lion.presentation.home.adapter.ReviewListRVAdapter
import kr.tekit.lion.presentation.home.vm.ReviewListViewModel
import kr.tekit.lion.presentation.splash.model.LogInState

@AndroidEntryPoint
class ReviewListActivity : AppCompatActivity() {
    private val viewModel: ReviewListViewModel by viewModels()
    private val binding: ActivityReviewListBinding by lazy {
        ActivityReviewListBinding.inflate(layoutInflater)
    }

    private val reportLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    binding.root.showSnackbar("신고가 완료되었습니다")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val placeId = intent.getLongExtra("reviewPlaceId", -1)

        settingToolbar()

        repeatOnStarted {
            launch {
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

            launch {
                viewModel.networkState.collectLatest { state ->
                    when (state) {
                        is NetworkState.Loading -> {
                            binding.reviewListErrorLayout.visibility = View.GONE
                        }

                        is NetworkState.Success -> {
                            binding.reviewListErrorLayout.visibility = View.GONE
                        }

                        is NetworkState.Error -> {
                            binding.reviewListToolbarTitleTv.setTextColor(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.text_primary
                                )
                            )
                            binding.reviewListToolbar.navigationIcon?.setTint(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.text_primary
                                )
                            )
                            binding.reviewListThumbnailDark.visibility = View.GONE
                            binding.reviewListThumbnailIv.visibility = View.GONE
                            binding.reviewListTitleTv.visibility = View.GONE
                            binding.reviewListAddressTv.visibility = View.GONE
                            binding.reviewListContentLayout.visibility = View.GONE
                            binding.reviewListErrorLayout.visibility = View.VISIBLE
                            binding.reviewListErrorTv.text = state.msg
                        }
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

    private fun settingReviewListRVAdapter(reviewList: List<PlaceReview>, loginState: Boolean) {
        val reviewListRVAdapter = ReviewListRVAdapter(reviewList, loginState, reportLauncher)

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

            settingReviewListRVAdapter(placeReviewInfo.placeReviewList, true)
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

            settingReviewListRVAdapter(placeReviewInfo.placeReviewList, false)
        }
    }
}