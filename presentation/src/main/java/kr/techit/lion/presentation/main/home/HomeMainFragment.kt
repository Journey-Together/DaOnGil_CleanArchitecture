package kr.techit.lion.presentation.main.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.techit.lion.domain.model.Activation
import kr.techit.lion.domain.model.AppTheme
import kr.techit.lion.domain.model.mainplace.AroundPlace
import kr.techit.lion.domain.model.mainplace.RecommendPlace
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.FragmentHomeMainBinding
import kr.techit.lion.presentation.databinding.ItemTouristRecommendBinding
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.repeatOnViewStarted
import kr.techit.lion.presentation.ext.showPermissionSnackBar
import kr.techit.lion.presentation.ext.showSnackbar
import kr.techit.lion.presentation.home.DetailActivity
import kr.techit.lion.presentation.home.model.HomeViewPagerPage
import kr.techit.lion.presentation.main.adapter.HomeLocationRVAdapter
import kr.techit.lion.presentation.main.adapter.HomeRecommendRVAdapter
import kr.techit.lion.presentation.main.adapter.HomeVPAdapter
import kr.techit.lion.presentation.main.customview.CustomPageIndicator
import kr.techit.lion.presentation.main.customview.ItemOffsetDecoration
import kr.techit.lion.presentation.main.dialog.ThemeGuideDialog
import kr.techit.lion.presentation.main.dialog.ThemeSettingDialog
import kr.techit.lion.presentation.main.home.vm.HomeViewModel
import kr.techit.lion.presentation.connectivity.ConnectivityObserver
import kr.techit.lion.presentation.connectivity.NetworkConnectivityObserver
import kr.techit.lion.presentation.ext.announceForAccessibility
import kr.techit.lion.presentation.ext.isTallBackEnabled
import kr.techit.lion.presentation.main.dialog.WalkthroughDialog
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.abs

@AndroidEntryPoint
class HomeMainFragment : Fragment(R.layout.fragment_home_main) {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var snapHelper: SnapHelper? = null
    private var hideAroundSection = false
    private val connectivityObserver: ConnectivityObserver by lazy {
        NetworkConnectivityObserver(requireContext().applicationContext)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            initLocationClient(FragmentHomeMainBinding.bind(requireView()))
        } else {
            // 위치 권한 거부 시, 주변 장소 섹션은 숨기고 추천 장소만 노출
            val binding = FragmentHomeMainBinding.bind(requireView())
            hideAroundSection = true
            requireContext().showPermissionSnackBar(binding.root)
            showPermissionDeniedLabel(binding)
            viewModel.getPlaceMain(DEFAULT_AREA, DEFAULT_SIGUNGU)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeMainBinding.bind(view)

        repeatOnViewStarted {
            launch { collectAppTheme() }
            launch { collectNetworkState(binding) }
            launch { observeConnectivity(binding) }
            launch { observeUserActivation() }
        }

        binding.homeHighcontrastBtn.visibility = View.GONE // 고대비 버튼 숨김 처리

        observePlaceData(binding)
        observeUserLocation(binding)
        settingAppTheme(binding)
        checkLocationPermission(binding)
        settingVPAdapter(binding)
        settingSearchBanner(binding)
        initializeAccessibility(binding)
    }

    private fun settingAppTheme(binding: FragmentHomeMainBinding) {
        childFragmentManager.setFragmentResultListener(
            "negativeButtonClick",
            viewLifecycleOwner
        ) { _, _ ->
            viewModel.onClickThemeChangeButton(AppTheme.SYSTEM)
        }

        childFragmentManager.setFragmentResultListener(
            "positiveButtonClick",
            viewLifecycleOwner
        ) { _, _ ->
            viewModel.onClickThemeChangeButton(AppTheme.HIGH_CONTRAST)
        }

        childFragmentManager.setFragmentResultListener(
            "completeButtonClick",
            viewLifecycleOwner
        ) { _, _ ->
            viewModel.onClickThemeChangeButton(AppTheme.SYSTEM)
        }

        binding.homeHighcontrastBtn.setOnClickListener {
            viewModel.onClickThemeToggleButton(isDarkTheme(resources.configuration))
        }
    }

    private fun settingVPAdapter(binding: FragmentHomeMainBinding) {
        val images = listOfNotNull(
            ContextCompat.getDrawable(requireContext(), R.drawable.item_home_vp1),
            ContextCompat.getDrawable(requireContext(), R.drawable.item_home_vp2),
            ContextCompat.getDrawable(requireContext(), R.drawable.item_home_vp3)
        )

        val homeVPAdapter = HomeVPAdapter(images) { position ->
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homeMainFragment, true)
                .build()

            val page = HomeViewPagerPage.fromPosition(position)

            when (page) {
                HomeViewPagerPage.SearchPlace -> findNavController().navigate(
                    R.id.action_homeMainFragment_to_searchPlaceMainFragment,
                    null,
                    navOptions
                )

                HomeViewPagerPage.Emergency -> findNavController().navigate(
                    R.id.action_homeMainFragment_to_emergencyMainFragment,
                    null,
                    navOptions
                )

                HomeViewPagerPage.Schedule -> findNavController().navigate(
                    R.id.action_homeMainFragment_to_scheduleMainFragment,
                    null,
                    navOptions
                )
            }
        }

        binding.homeVp.adapter = homeVPAdapter
        binding.homeVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.homeVpIndicator.setViewPager(binding.homeVp)
        binding.homeVpIndicator.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO

        startAutoSlide(homeVPAdapter, binding)
    }

    private fun settingSearchBanner(binding: FragmentHomeMainBinding) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.homeMainFragment, true)
            .build()

        binding.homeSearchBannerIv.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeMainFragment_to_searchPlaceMainFragment,
                null,
                navOptions
            )
        }
    }

    private fun initializeAccessibility(binding: FragmentHomeMainBinding) {
        if (requireContext().isTallBackEnabled()) {
            setupAccessibility(binding)
        } else {
            binding.homeToolbar.menu.clear()
        }
    }

    private fun setupAccessibility(binding: FragmentHomeMainBinding) {
        requireActivity().announceForAccessibility(
            getString(R.string.text_script_this_is_home) +
                    getString(R.string.text_script_read_all_text)
        )

        binding.homeToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.read_script -> {
                    requireActivity().announceForAccessibility(getString(R.string.text_script_guide_for_home))
                    true
                }

                else -> false
            }
        }
    }

    private fun startAutoSlide(adapter: HomeVPAdapter, binding: FragmentHomeMainBinding) {
        val timer = Timer()
        val handler = Handler(Looper.getMainLooper())

        // TalkBack 활성화 여부 확인 후 슬라이드 간격 설정(기본 4초, TalkBack: 10초)
        val isTalkBackEnabled = requireContext().isTallBackEnabled()
        val interval = if (isTalkBackEnabled) 10000L else 4000L

        timer.scheduleAtFixedRate(3000L, interval) {
            handler.post {
                val nextItem = binding.homeVp.currentItem + 1
                val targetItem = if (nextItem < adapter.itemCount) nextItem else 0

                // 자동 슬라이드 TalkBack 읽기 방지
                binding.homeVp.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                binding.homeVp.currentItem = targetItem
            }
        }
    }

    private fun settingRecommendRVAdapter(
        binding: FragmentHomeMainBinding,
        recommendPlaceList: List<RecommendPlace>
    ) {
        val homeRecommendRVAdapter = HomeRecommendRVAdapter(
            recommendPlaceList,
            onClick = { position ->
                val context: Context = binding.root.context
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("detailPlaceId", recommendPlaceList[position].placeId)
                startActivity(intent)
            })
        binding.homeRecommendRv.adapter = homeRecommendRVAdapter
        binding.homeRecommendRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val itemCount = recommendPlaceList.size
        val customPageIndicator =
            CustomPageIndicator(requireActivity(), binding.homeRecommendRvIndicator, itemCount)

        if (snapHelper == null) {
            snapHelper = LinearSnapHelper()
            snapHelper?.attachToRecyclerView(binding.homeRecommendRv)
        }

        binding.homeRecommendRv.addItemDecoration(ItemOffsetDecoration(70, 70))

        // 스크롤에 따라 아이템의 크기와 알파를 조정
        binding.homeRecommendRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val centerX = recyclerView.width / 2

                // 스냅된 뷰 찾기
                val snapView = snapHelper!!.findSnapView(recyclerView.layoutManager)
                val position = recyclerView.getChildAdapterPosition(snapView ?: return)

                customPageIndicator.onPageSelected(position)

                for (i in 0 until recyclerView.childCount) {
                    val child = recyclerView.getChildAt(i)
                    val itemBinding = ItemTouristRecommendBinding.bind(child)
                    val childCenterX = (child.left + child.right) / 2
                    val distanceFromCenter = abs(centerX - childCenterX)
                    val scaleFactor = 1 - (distanceFromCenter.toFloat() / centerX)

                    // 아이템의 크기 및 알파값 조정
                    child.scaleX = 1.0f + 0.2f * scaleFactor
                    child.scaleY = 1.0f + 0.2f * scaleFactor

                    // Z축 이동량 조정 - 겹쳐 보이도록 음수 값으로 조정
                    child.translationZ = (-distanceFromCenter / centerX).toFloat()

                    // 좌우 이동 조정 - 중앙에 가까운 아이템이 앞으로 나오는 효과
                    val translationXFactor = 0.25f * distanceFromCenter / centerX
                    child.translationX =
                        (if (childCenterX < centerX) translationXFactor else -translationXFactor) * child.width

                    if (distanceFromCenter < recyclerView.width / 2) {
                        itemBinding.touristRecommendDark.visibility = View.GONE
                    } else {
                        itemBinding.touristRecommendDark.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun settingLocationRVAdapter(
        binding: FragmentHomeMainBinding,
        aroundPlaceList: List<AroundPlace>
    ) {
        val homeLocationRVAdapter = HomeLocationRVAdapter(
            aroundPlaceList,
            onClick = { position ->
                val context: Context = binding.root.context
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("detailPlaceId", aroundPlaceList[position].placeId)
                startActivity(intent)
            })
        binding.homeMyLocationRv.adapter = homeLocationRVAdapter
    }

    private fun showThemeSettingDialog() {
        val dialog = ThemeSettingDialog()
        dialog.isCancelable = false
        dialog.show(childFragmentManager, "ThemeSettingDialog")
    }

    private fun showThemeGuideDialog() {
        val dialog = ThemeGuideDialog()
        dialog.isCancelable = false
        dialog.show(childFragmentManager, "ThemeGuideDialog")
    }

    private fun showWalkthroughDialog() {
        val dialog = WalkthroughDialog()
        dialog.isCancelable = false
        dialog.show(childFragmentManager, "WalkthroughDialog")
    }

    private fun checkLocationPermission(binding: FragmentHomeMainBinding) {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            initLocationClient(binding)
        }
    }

    private fun initLocationClient(binding: FragmentHomeMainBinding) {
        // 위치 요청 설정
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3600000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(3600000)
            .setMaxUpdateDelayMillis(3600000)
            .build()

        // 위치 설정 요청 빌더 구성
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(requireContext())
        val task = client.checkLocationSettings(builder.build())

        // 위치 설정이 성공적으로 확인된 경우 위치 업데이트 시작
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    getTaskResult(task)
                }

                withContext(Dispatchers.Main) {
                    fusedLocationProviderClient =
                        LocationServices.getFusedLocationProviderClient(requireContext())
                    startLocationUpdates(binding)

                    if (result.locationSettingsStates?.isLocationUsable != true) {
                        loadDefaultPlace(binding)
                    }
                }
            } catch (e: Exception) {
                // 위치 설정 확인 실패 시 기본 위치로 우선 표시하되, 업데이트 구독은 유지
                withContext(Dispatchers.Main) {
                    fusedLocationProviderClient =
                        LocationServices.getFusedLocationProviderClient(requireContext())
                    startLocationUpdates(binding)
                    loadDefaultPlace(binding)
                }
            }
        }
    }

    private suspend fun getTaskResult(task: Task<LocationSettingsResponse>): LocationSettingsResponse {
        return suspendCoroutine { continuation ->
            task.addOnSuccessListener { result ->
                continuation.resume(result)
            }.addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
        }
    }

    private fun startLocationUpdates(binding: FragmentHomeMainBinding) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3600000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(3600000)
            .setMaxUpdateDelayMillis(3600000)
            .build()

        locationCallback = object : LocationCallback() {
            @SuppressLint("SetTextI18n")
            override fun onLocationResult(locationResult: LocationResult) {
                // 위치를 받지 못하면 기본 위치로 설정
                val location = locationResult.locations.firstOrNull()
                if (location == null) {
                    loadDefaultPlace(binding)
                    return
                }

                val coords = "${location.longitude},${location.latitude}"
                viewModel.getUserLocationRegion(coords)
            }
        }

        // 위치 권한이 부여되었는지 확인 후 위치 업데이트 요청
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        }
    }

    override fun onPause() {
        super.onPause()
        // locationCallback이 초기화되었는지 확인 후 초기화 된 경우에만 removeLocationUpdates()를 호출
        if (::locationCallback.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun showPermissionDeniedLabel(binding: FragmentHomeMainBinding) {
        binding.homeMyLocationTv.text = "위치 권한을 허용해주세요"
    }

    @SuppressLint("SetTextI18n")
    private fun loadDefaultPlace(binding: FragmentHomeMainBinding) {
        hideAroundSection = false
        binding.homeMyLocationTv.text = "$DEFAULT_AREA $DEFAULT_SIGUNGU"
        viewModel.getPlaceMain(DEFAULT_AREA, DEFAULT_SIGUNGU)
    }

    private fun observePlaceData(binding: FragmentHomeMainBinding) {
        viewModel.aroundPlaceInfo.observe(viewLifecycleOwner) { aroundPlaceList ->
            when {
                // 위치 권한 거부 시 주변 관광지 리스트 숨김
                hideAroundSection -> {
                    binding.homeMyLocationRv.visibility = View.GONE
                    binding.homeMyLocationEmptyTv.visibility = View.GONE
                }
                // 주변 장소가 있을 시 리스트 노출
                aroundPlaceList.isNotEmpty() -> {
                    settingLocationRVAdapter(binding, aroundPlaceList)
                    binding.homeMyLocationRv.visibility = View.VISIBLE
                    binding.homeMyLocationEmptyTv.visibility = View.GONE
                }
                // 해당 지역에 주변 장소가 0건일 경우 빈 안내문 노출
                else -> {
                    binding.homeMyLocationRv.visibility = View.GONE
                    binding.homeMyLocationEmptyTv.visibility = View.VISIBLE
                }
            }
        }

        viewModel.recommendPlaceInfo.observe(viewLifecycleOwner) { recommendPlaceList ->
            if (recommendPlaceList.isNotEmpty()) {
                settingRecommendRVAdapter(binding, recommendPlaceList)
            }
        }

        viewModel.locationMessage.observe(viewLifecycleOwner) { message ->
            binding.root.showSnackbar(message)
            binding.homeMyLocationTv.text = "$DEFAULT_AREA $DEFAULT_SIGUNGU"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeUserLocation(binding: FragmentHomeMainBinding) {
        viewModel.area.observe(viewLifecycleOwner) { area ->
            hideAroundSection = false
            val parts = area.split(" ")
            when {
                parts.size >= 2 -> {
                    binding.homeMyLocationTv.text = "${parts[0]} ${parts[1]}"
                    viewModel.getPlaceMain(parts[0], parts[1])
                }
                parts.size == 1 -> {
                    binding.homeMyLocationTv.text = parts[0]
                    viewModel.getPlaceMain(parts[0], parts[0])
                }
                else -> {
                    loadDefaultPlace(binding)
                }
            }
        }
    }

    private fun isDarkTheme(configuration: Configuration): Boolean {
        return (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    private suspend fun collectAppTheme() {
        viewModel.appTheme.collect {
            when (it) {
                AppTheme.LIGHT ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                AppTheme.HIGH_CONTRAST ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                AppTheme.SYSTEM ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

                AppTheme.LOADING -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private suspend fun collectNetworkState(binding: FragmentHomeMainBinding) {
        with(binding) {
            viewModel.networkState.collectLatest { state ->
                when (state) {
                    is NetworkState.Loading -> {
                        homeProgressbar.visibility = View.VISIBLE
                        homeMainLayout.visibility = View.VISIBLE
                        homeErrorLayout.visibility = View.GONE
                    }

                    is NetworkState.Success -> {
                        homeProgressbar.visibility = View.GONE
                        homeMainLayout.visibility = View.VISIBLE
                        homeErrorLayout.visibility = View.GONE
                    }

                    is NetworkState.Error -> {
                        homeProgressbar.visibility = View.GONE
                        homeMainLayout.visibility = View.GONE
                        homeErrorLayout.visibility = View.VISIBLE
                        homeErrorTv.text = state.msg
                    }
                }
            }
        }
    }

    private suspend fun observeUserActivation() {
        viewModel.userActivationState.collect {
            when (it) {
                Activation.Loading,
                Activation.Activate -> return@collect

                Activation.DeActivate -> {
//                    if (isDarkTheme(resources.configuration)) {
//                        showThemeGuideDialog()
//                    } else {
//                        showThemeSettingDialog()
//                    }

                    showWalkthroughDialog()
                }
            }
        }
    }

    private suspend fun observeConnectivity(binding: FragmentHomeMainBinding) {
        with(binding) {
            connectivityObserver.getFlow().collect { connectivity ->
                when (connectivity) {
                    ConnectivityObserver.Status.Available -> {
                        homeProgressbar.visibility = View.GONE
                        homeMainLayout.visibility = View.VISIBLE
                        homeErrorLayout.visibility = View.GONE

                        if (viewModel.networkState.value is NetworkState.Error) {
                            checkLocationPermission(binding)
                        }
                    }
                    // Unavailable, Losing, Lost
                    else -> {
                        val msg = "${getString(R.string.text_network_is_unavailable)}\n" +
                                getString(R.string.text_plz_check_network)

                        homeProgressbar.visibility = View.GONE
                        homeMainLayout.visibility = View.GONE
                        homeErrorLayout.visibility = View.VISIBLE
                        homeErrorTv.text = msg
                    }
                }
            }
        }
    }

    companion object {
        const val DEFAULT_AREA = "서울특별시"
        const val DEFAULT_SIGUNGU = "중구"
    }
}
