package kr.techit.lion.presentation.home

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.techit.lion.domain.model.placereviewlist.PlaceReview
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ActivityReviewListBinding
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.addOnScrollEndListener
import kr.techit.lion.presentation.ext.repeatOnStarted
import kr.techit.lion.presentation.ext.showSnackbar
import kr.techit.lion.presentation.ext.updateToolbarColors
import kr.techit.lion.presentation.ext.updateVisibility
import kr.techit.lion.presentation.home.adapter.ReviewListRVAdapter
import kr.techit.lion.presentation.home.vm.ReviewListViewModel
import kr.techit.lion.presentation.observer.ConnectivityObserver
import kr.techit.lion.presentation.observer.NetworkConnectivityObserver
import kr.techit.lion.presentation.splash.model.LogInState

@AndroidEntryPoint
class ReviewListActivity : AppCompatActivity() {
    private val viewModel: ReviewListViewModel by viewModels()
    private val binding: ActivityReviewListBinding by lazy {
        ActivityReviewListBinding.inflate(layoutInflater)
    }
    private val connectivityObserver: ConnectivityObserver by lazy {
        NetworkConnectivityObserver.getInstance(this)
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
            supervisorScope {
                launch { collectLoginState(placeId) }
                launch { collectReviewNetworkState() }
                launch { observeConnectivity(placeId) }
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

    private suspend fun collectLoginState(placeId: Long) {
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

    private suspend fun collectReviewNetworkState() {
        with(binding) {
            viewModel.networkState.collectLatest { state ->
                when (state) {
                    is NetworkState.Loading -> {
                        reviewListErrorLayout.visibility = View.GONE
                    }

                    is NetworkState.Success -> {
                        reviewListErrorLayout.visibility = View.GONE
                    }

                    is NetworkState.Error -> {
                        this@ReviewListActivity.updateToolbarColors(
                            reviewListToolbarTitleTv,
                            reviewListToolbar,
                            R.color.text_primary
                        )

                        root.updateVisibility(
                            isVisible = false,
                            reviewListThumbnailDark, reviewListThumbnailIv, reviewListTitleTv,
                            reviewListAddressTv, reviewListContentLayout
                        )
                        binding.reviewListErrorLayout.visibility = View.VISIBLE
                        binding.reviewListErrorTv.text = state.msg
                    }
                }
            }
        }
    }

    private suspend fun observeConnectivity(placeId: Long) {
        with(binding) {
            connectivityObserver.getFlow().collect { connectivity ->
                when (connectivity) {
                    ConnectivityObserver.Status.Available -> {
                        reviewListErrorLayout.visibility = View.GONE

                        this@ReviewListActivity.updateToolbarColors(
                            reviewListToolbarTitleTv,
                            reviewListToolbar,
                            R.color.white
                        )
                        root.updateVisibility(
                            isVisible = true,
                            reviewListThumbnailDark, reviewListThumbnailIv, reviewListTitleTv,
                            reviewListAddressTv, reviewListContentLayout
                        )

                        if (viewModel.networkState.value is NetworkState.Error) {
                            lifecycleScope.launch {
                                collectLoginState(placeId)
                            }
                        }
                    }
                    // Unavailable, Losing, Lost
                    else -> {
                        val msg = "${getString(R.string.text_network_is_unavailable)}\n" +
                                "${getString(R.string.text_plz_check_network)} "

                        this@ReviewListActivity.updateToolbarColors(
                            reviewListToolbarTitleTv,
                            reviewListToolbar,
                            R.color.text_primary
                        )
                        root.updateVisibility(
                            isVisible = false,
                            reviewListThumbnailDark, reviewListThumbnailIv, reviewListTitleTv,
                            reviewListAddressTv, reviewListContentLayout
                        )
                        reviewListErrorLayout.visibility = View.VISIBLE
                        reviewListErrorTv.text = msg
                    }
                }
            }
        }
    }

}