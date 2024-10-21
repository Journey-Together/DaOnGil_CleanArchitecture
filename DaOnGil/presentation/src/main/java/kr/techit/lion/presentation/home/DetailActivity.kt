package kr.techit.lion.presentation.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.ColorRes
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.techit.lion.domain.model.detailplace.Review
import kr.techit.lion.domain.model.detailplace.SubDisability
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ActivityDetailBinding
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.emergency.EmergencyMapActivity
import kr.techit.lion.presentation.ext.repeatOnStarted
import kr.techit.lion.presentation.ext.showInfinitySnackBar
import kr.techit.lion.presentation.ext.showSnackbar
import kr.techit.lion.presentation.home.adapter.DetailDisabilityRVAdapter
import kr.techit.lion.presentation.home.adapter.DetailInfoRVAdapter
import kr.techit.lion.presentation.home.adapter.DetailReviewRVAdapter
import kr.techit.lion.presentation.home.model.toReviewInfo
import kr.techit.lion.presentation.home.vm.DetailViewModel
import kr.techit.lion.presentation.myreview.MyReviewActivity
import kr.techit.lion.presentation.observer.ConnectivityObserver
import kr.techit.lion.presentation.observer.NetworkConnectivityObserver
import kr.techit.lion.presentation.splash.model.LogInState

@AndroidEntryPoint
class DetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private val viewModel: DetailViewModel by viewModels()
    private val binding: ActivityDetailBinding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationSource: FusedLocationSource
    private val connectivityObserver: ConnectivityObserver by lazy {
        NetworkConnectivityObserver(applicationContext)
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

        repeatOnStarted {
            supervisorScope {
                launch { collectDetailNetworkState(binding) }
                launch { observeConnectivity(binding) }
            }
        }

        settingToolbar()
        initMap()
    }

    override fun onResume() {
        super.onResume()
        initMap()
    }

    private fun settingDetailInfoRVAdapter(detailInfo: List<SubDisability>) {
        val detailInfoRVAdapter = DetailInfoRVAdapter(detailInfo)
        binding.detailDisabilityInfoRv.adapter = detailInfoRVAdapter
        binding.detailDisabilityInfoRv.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun settingReviewRVAdapter(reviewList: List<Review>, loginState: Boolean) {
        if (reviewList.isEmpty()) {
            binding.detailReviewRv.visibility = View.GONE
            binding.detailNoReviewTv.visibility = View.VISIBLE
            binding.detailNoReviewTv.text = "현재 작성된 리뷰가 없습니다"
        } else {
            binding.detailReviewRv.visibility = View.VISIBLE
            binding.detailNoReviewTv.visibility = View.GONE

            val detailReviewRVAdapter =
                DetailReviewRVAdapter(reviewList, loginState, reportLauncher)
            binding.detailReviewRv.adapter = detailReviewRVAdapter
            binding.detailReviewRv.layoutManager = LinearLayoutManager(applicationContext)
        }
    }

    private fun settingDisabilityRVAdapter(disabilityList: List<Int>) {
        val disabilityRVAdapter = DetailDisabilityRVAdapter(disabilityList)
        binding.detailDisabilityIvRv.adapter = disabilityRVAdapter
        binding.detailDisabilityIvRv.layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun settingToolbar() {
        binding.detailToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun settingReviewBtn(
        placeId: Long,
        placeName: String,
        placeAddress: String,
        image: String?
    ) {
        binding.detailMoreReviewBtn.setOnClickListener {
            val intent = Intent(this, ReviewListActivity::class.java)
            intent.putExtra("reviewPlaceId", placeId)
            startActivity(intent)
        }

        binding.detailWriteReviewBtn.setOnClickListener {
            val intent = Intent(this, WriteReviewActivity::class.java).apply {
                putExtra("reviewPlaceId", placeId)
                putExtra("reviewPlaceName", placeName)
                putExtra("reviewPlaceAddress", placeAddress)
                putExtra("reviewPlaceImage", image)
            }
            startActivity(intent)
        }
    }

    private fun settingModifyBtn(review: Review, placeName: String) {

        binding.detailModifyReviewBtn.setOnClickListener {
            val intent = Intent(this, MyReviewActivity::class.java).apply {
                val reviewInfo = review.toReviewInfo(placeName)
                putExtra("reviewInfo", reviewInfo)
                putExtra("isModifyFromDetail", true)
            }
            startActivity(intent)
        }

    }

    private fun handleCommonDetailPlaceInfo(
        placeId: Long,
        reviewList: List<Review>?,
        disability: List<Int>,
        name: String,
        address: String,
        overview: String,
        tel: String,
        homepage: String,
        image: String?,
        longitude: Double,
        latitude: Double,
        category: String,
        subDisability: List<SubDisability>?,
        loginState: Boolean
    ) {
        reviewList?.let {
            settingReviewRVAdapter(it, loginState)

            val myReview = it.filter { review -> review.myReview }

            myReview.forEach { review ->
                settingModifyBtn(review, name)
            }
        }

        settingDisabilityRVAdapter(disability)

        if (subDisability != null) {
            settingDetailInfoRVAdapter(subDisability)
        }

        settingReviewBtn(placeId, name, address, image)

        with(binding) {
            detailTitleTv.text = name
            detailAddressTv.text = address
            detailBasicContentTv.text = overview
            detailToolbarTitleTv.text = category
            detailRoutePlaceTv.text = category
            detailBasicAddressContentTv.text = address
            detailCallContentTv.text = tel
            detailHomepageContentTv.text = homepage

            detailCallContentTv.setOnClickListener {
                if (tel != "문의 정보가 제공되지 않습니다") {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:$tel")
                    startActivity(intent)
                }
            }

            detailHomepageContentTv.setOnClickListener {
                val url = detailHomepageContentTv.text.toString()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }

            if (image != null) {
                Glide.with(detailThumbnailIv.context)
                    .load(image)
                    .error(R.drawable.empty_view)
                    .into(detailThumbnailIv)
            }
        }

        val cameraUpdate = CameraUpdate.scrollTo(LatLng(longitude, latitude))
        naverMap.moveCamera(cameraUpdate)

        addMapMarker(longitude, latitude)
    }

    private fun getDetailPlaceInfo(placeId: Long) {
        viewModel.getDetailPlace(placeId)

        viewModel.detailPlaceInfo.observe(this@DetailActivity) { detailPlaceInfo ->
            handleCommonDetailPlaceInfo(
                detailPlaceInfo.placeId,
                detailPlaceInfo.reviewList,
                detailPlaceInfo.disability,
                detailPlaceInfo.name,
                detailPlaceInfo.address,
                detailPlaceInfo.overview,
                detailPlaceInfo.tel,
                detailPlaceInfo.homepage,
                detailPlaceInfo.image,
                detailPlaceInfo.longitude.toDouble(),
                detailPlaceInfo.latitude.toDouble(),
                detailPlaceInfo.category,
                detailPlaceInfo.subDisability,
                true
            )

            updateBookmarkState(detailPlaceInfo.isMark)

            binding.detailBookmarkBtn.setOnClickListener {
                val originalMarkState = detailPlaceInfo.isMark
                val newMarkState = !originalMarkState

                viewModel.updateDetailPlaceBookmark(placeId)

                viewModel.isBookmarkSuccess.observe(this) { isSuccess ->
                    if (isSuccess) {
                        detailPlaceInfo.isMark = newMarkState
                        updateBookmarkState(newMarkState)
                    } else {
                        updateBookmarkState(originalMarkState)
                    }
                }
            }

            if (detailPlaceInfo.isReview) {
                binding.detailWriteReviewBtn.visibility = View.GONE
                binding.detailModifyReviewBtn.visibility = View.VISIBLE
            } else {
                binding.detailWriteReviewBtn.visibility = View.VISIBLE
                binding.detailModifyReviewBtn.visibility = View.GONE
            }
        }
    }

    private fun getDetailPlaceInfoGuest(placeId: Long) {
        viewModel.getDetailPlaceGuest(placeId)

        viewModel.detailPlaceInfoGuest.observe(this@DetailActivity) { detailPlaceInfoGuest ->
            val tel = detailPlaceInfoGuest.tel
            val homepage = detailPlaceInfoGuest.homepage

            handleCommonDetailPlaceInfo(
                detailPlaceInfoGuest.placeId,
                detailPlaceInfoGuest.reviewList,
                detailPlaceInfoGuest.disability,
                detailPlaceInfoGuest.name,
                detailPlaceInfoGuest.address,
                detailPlaceInfoGuest.overview,
                tel,
                homepage,
                detailPlaceInfoGuest.image,
                detailPlaceInfoGuest.longitude.toDouble(),
                detailPlaceInfoGuest.latitude.toDouble(),
                detailPlaceInfoGuest.category,
                detailPlaceInfoGuest.subDisability,
                false
            )
            binding.detailBookmarkBtn.visibility = View.GONE
            binding.detailWriteReviewBtn.visibility = View.GONE
            binding.detailModifyReviewBtn.visibility = View.GONE
        }
    }

    private fun updateBookmarkState(isMark: Boolean) {
        val bookmarkDrawable = if (isMark) {
            R.drawable.bookmark_fill_icon
        } else {
            R.drawable.bookmark_icon
        }
        binding.detailBookmarkBtn.setImageResource(bookmarkDrawable)
    }

    private fun initMap() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 네이버 지도 SDK에 위치를 제공하는 인터페이스
        mLocationSource =
            FusedLocationSource(this, EmergencyMapActivity.LOCATION_PERMISSION_REQUEST_CODE)
        // 네이버맵 동적으로 불러오기
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.detail_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.detail_map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        naverMap.minZoom = 10.0
        naverMap.maxZoom = 15.0

        val recommendPlaceId = intent.getLongExtra("detailPlaceId", -1)

        repeatOnStarted {
            viewModel.loginState.collect { uiState ->
                when (uiState) {
                    is LogInState.Checking -> {
                        return@collect
                    }

                    is LogInState.LoggedIn -> {
                        getDetailPlaceInfo(recommendPlaceId)
                    }

                    is LogInState.LoginRequired -> {
                        getDetailPlaceInfoGuest(recommendPlaceId)
                    }
                }
            }
        }
    }

    private fun addMapMarker(mapX: Double, mapY: Double) {
        val marker = Marker()
        with(marker) {
            icon = OverlayImage.fromResource(R.drawable.maker_selected_lodging_icon)
            position = LatLng(mapX, mapY)
            zIndex = 0
            map = naverMap
            width = 86
            height = 90
        }
    }

    private suspend fun collectDetailNetworkState(binding: ActivityDetailBinding) {
        with(binding) {
            viewModel.networkState.collectLatest { state ->
                when (state) {
                    is NetworkState.Loading -> {
                        detailProgressbar.visibility = View.VISIBLE
                        detailErrorLayout.visibility = View.GONE
                    }

                    is NetworkState.Success -> {
                        detailProgressbar.visibility = View.GONE
                        detailErrorLayout.visibility = View.GONE
                    }

                    is NetworkState.Error -> {
                        detailProgressbar.visibility = View.GONE

                        if (viewModel.isBookmarkError.value == true) {
                            this@DetailActivity.showInfinitySnackBar(binding.root, state.msg)
                        } else {
                            updateToolbarColors(binding, R.color.text_primary)
                            setVisibility(
                                isVisible = false,
                                detailThumbnailIv, detailThumbnailDark, detailTitleTv,
                                detailAddressTv, detailBookmarkBtn, detailContentLayout
                            )
                            detailErrorLayout.visibility = View.VISIBLE
                            detailErrorTv.text = state.msg
                        }
                    }
                }
            }
        }
    }

    private suspend fun observeConnectivity(binding: ActivityDetailBinding) {
        with(binding) {
            connectivityObserver.getFlow().collect { connectivity ->
                when (connectivity) {
                    ConnectivityObserver.Status.Available -> {
                        setVisibility(
                            isVisible = true,
                            detailThumbnailIv, detailThumbnailDark, detailTitleTv, detailAddressTv,
                            detailBookmarkBtn, detailContentLayout
                        )
                        setVisibility(isVisible = false, detailProgressbar, detailErrorLayout)
                        updateToolbarColors(binding, R.color.white)

                        if (viewModel.networkState.value is NetworkState.Error) {
                            initMap()
                        }
                    }
                    // Unavailable, Losing, Lost
                    else -> {
                        val msg = "${getString(R.string.text_network_is_unavailable)}\n" +
                                "${getString(R.string.text_plz_check_network)} "

                        setVisibility(
                            isVisible = false,
                            detailThumbnailIv, detailThumbnailDark, detailTitleTv, detailAddressTv,
                            detailBookmarkBtn, detailContentLayout, detailProgressbar
                        )
                        setVisibility(isVisible = true, detailErrorLayout)
                        detailErrorTv.text = msg
                        updateToolbarColors(binding, R.color.text_primary)
                    }
                }
            }
        }
    }

    private fun setVisibility(isVisible: Boolean, vararg views: View) {
        views.forEach { it.visibility = if (isVisible) View.VISIBLE else View.GONE }
    }

    private fun updateToolbarColors(binding: ActivityDetailBinding, @ColorRes colorRes: Int) {
        binding.detailToolbarTitleTv.setTextColor(
            ContextCompat.getColor(
                binding.root.context,
                colorRes
            )
        )
        binding.detailToolbar.navigationIcon?.setTint(
            ContextCompat.getColor(
                binding.root.context,
                colorRes
            )
        )
    }
}