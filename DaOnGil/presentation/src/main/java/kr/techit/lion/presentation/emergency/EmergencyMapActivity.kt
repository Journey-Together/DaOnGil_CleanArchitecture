package kr.techit.lion.presentation.emergency

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.UiThread
import androidx.core.app.ActivityCompat
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.techit.lion.domain.model.AedMapInfo
import kr.techit.lion.domain.model.EmergencyMapInfo
import kr.techit.lion.domain.model.HospitalMapInfo
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ActivityEmergencyMapBinding
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.emergency.fragment.EmergencyAreaDialog
import kr.techit.lion.presentation.emergency.fragment.EmergencyBottomSheet
import kr.techit.lion.presentation.emergency.vm.EmergencyMapViewModel
import kr.techit.lion.presentation.ext.repeatOnStarted
import kr.techit.lion.presentation.ext.showPermissionSnackBar
import kr.techit.lion.presentation.observer.ConnectivityObserver
import kr.techit.lion.presentation.observer.NetworkConnectivityObserver

@AndroidEntryPoint
class EmergencyMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private val binding: ActivityEmergencyMapBinding by lazy {
        ActivityEmergencyMapBinding.inflate(layoutInflater)
    }

    private val viewModel: EmergencyMapViewModel by viewModels()

    private val connectivityObserver: ConnectivityObserver by lazy {
        NetworkConnectivityObserver.getInstance(this)
    }

    private lateinit var launcherForPermission: ActivityResultLauncher<Array<String>>

    // 내장 위치 추적 기능 사용
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    private val bottomSheetBehavior by lazy {
        BottomSheetBehavior.from(binding.emergencyBottomSheet.emergencyBottomSheetLayout)
    }

    private var emergencySelectedMarker: Marker? = null
    private var aedSelectedMarker: Marker? = null
    private val markers = mutableListOf<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getEmergencyMap()

        val contracts = ActivityResultContracts.RequestMultiplePermissions()
        launcherForPermission = registerForActivityResult(contracts) { permissions ->
            if (permissions.any { it.value }) {
                permissionGrantedMapUiSetting()
            } else {
                // 하나 이상의 권한이 거부된 경우 처리할 작업
                permissions.forEach { (permission, isGranted) ->
                    when {
                        !isGranted -> {
                            if (!shouldShowRequestPermissionRationale(permission)) {
                                permissionDeniedMapUiSetting()
                                showPermissionSnackBar(binding.root)

                                permissionDeniedMapSetting()
                            }
                        }

                        else -> {
                            permissionDeniedMapUiSetting()
                            showPermissionSnackBar(binding.root)
                            permissionDeniedMapSetting()
                        }
                    }
                }

            }
        }

        initMap()
        initBottomSheet()
        settingDialog()
        setAreaUi()
        setToolbar()

        repeatOnStarted {
            supervisorScope {
                launch { observeConnectivity() }
                launch { collectEmergencyMapState() }
            }
        }
    }

    private suspend fun observeConnectivity() {
        with(binding){
            connectivityObserver.getFlow().collect { connectivity ->
                when(connectivity){
                    ConnectivityObserver.Status.Available -> {
                        emergencyMapErrorLayout.visibility = View.GONE
                        emergencyMapErrorProgressBar.visibility = View.GONE
                        emergencyMapLayout.visibility = View.VISIBLE
                        emergencyMapProgressBar.visibility = View.VISIBLE
                    }
                    ConnectivityObserver.Status.Unavailable,
                    ConnectivityObserver.Status.Losing,
                    ConnectivityObserver.Status.Lost -> {
                        emergencyMapErrorLayout.visibility = View.VISIBLE
                        emergencyMapErrorProgressBar.visibility = View.GONE
                        emergencyMapLayout.visibility = View.GONE
                        emergencyMapProgressBar.visibility = View.GONE
                        val msg = "${getString(R.string.text_network_is_unavailable)}\n" +
                                "${getString(R.string.text_plz_check_network)} "
                        emergencyMapErrorMsg.text = msg

                        val emergencyAreaDialog = this@EmergencyMapActivity.supportFragmentManager.findFragmentByTag("EmergencyAreaDialog") as? EmergencyAreaDialog
                        emergencyAreaDialog.let { dialog ->
                            if (dialog?.isVisible == true) {
                                dialog.dismiss()
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun collectEmergencyMapState() {
        with(binding){
            viewModel.networkState.collect { networksState ->
                when(networksState){
                    is NetworkState.Loading -> {
                        emergencyMapProgressBar.visibility = View.GONE
                        emergencyMapErrorProgressBar.visibility = View.VISIBLE
                        emergencyMapErrorLayout.visibility = View.GONE
                    }
                    is NetworkState.Success -> {
                        emergencyMapProgressBar.visibility = View.VISIBLE
                        emergencyMapLayout.visibility = View.VISIBLE
                        emergencyMapErrorProgressBar.visibility = View.GONE
                        emergencyMapErrorLayout.visibility = View.GONE
                    }
                    is NetworkState.Error -> {
                        emergencyMapProgressBar.visibility = View.GONE
                        emergencyMapLayout.visibility = View.GONE
                        emergencyMapErrorProgressBar.visibility = View.GONE
                        emergencyMapErrorLayout.visibility = View.VISIBLE
                        emergencyMapErrorMsg.text = networksState.msg
                    }
                }
            }
        }
    }
    private fun setToolbar(){
        binding.toolbarEmergencyMap.setNavigationOnClickListener {
            finish()
        }
    }
    private fun setAreaUi(){
        viewModel.area.observe(this@EmergencyMapActivity) { area ->
            binding.emergencyMapArea.text = area
            binding.emergencyMapProgressBar.setProgressCompat(20, true)

            markers.map {
                it.map = null
            }
            markers.clear()
        }
    }

    private fun settingDialog() {
        val dialog = EmergencyAreaDialog()
        dialog.isCancelable = false

        binding.emergencyMapAreaButton.setOnClickListener {
            dialog.show(this.supportFragmentManager, "EmergencyAreaDialog")
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

    }

    private fun setBottomRecylcerView(emergencyMapInfoList: List<EmergencyMapInfo>){
        val emergencyBottomSheet = EmergencyBottomSheet(binding.emergencyBottomSheet, emergencyMapInfoList)
        emergencyBottomSheet.setRecyclerView()
        emergencyBottomSheet.recyclerViewTopButton()
    }

    private fun initBottomSheet() {

        val observer = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val metrics = resources.displayMetrics
                val dp = 40f
                val px = (dp * (metrics.densityDpi / 160f)).toInt()

                val buttonHeight = binding.emergencyMapAreaButton.height
                val toolbarHeight = binding.toolbarEmergencyMap.height
                val deviceHeight = metrics.heightPixels - buttonHeight - toolbarHeight - px

                if (deviceHeight > 0) {
                    bottomSheetBehavior.maxHeight = deviceHeight
                    Log.d("device", "Calculated deviceHeight: $deviceHeight")
                } else {
                    Log.e("device", "Failed to calculate deviceHeight")
                }

                // 레이아웃 변경을 추적하는 리스너는 더 이상 필요 없으므로 제거
                binding.emergencyMapAreaButton.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }

        binding.emergencyMapAreaButton.viewTreeObserver.addOnGlobalLayoutListener(observer)
    }

    private fun initMap() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // LocationSource 구현체를 지정
        // 네이버 지도 SDK에 위치를 제공하는 인터페이스
        mLocationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        // 네이버맵 동적으로 불러오기
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    // 네이버맵 불러오기가 완료되면 콜백
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {

        this.naverMap = naverMap

        naverMap.minZoom = 10.0
        naverMap.maxZoom = 15.0

        naverMap.setOnMapClickListener { _, _ ->
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            viewModel.emergencyMapInfo.value?.toMutableList()?.let { setBottomRecylcerView(it) }

            /*viewModel.emergencyMapInfo.value?.let { list ->
                val emergencyList = list.filter { it.emergencyType == "hospital" }.toMutableList()
                if (emergencyList.isEmpty()) {
                    emergencyList.addAll(list)
                }
                setBottomRecylcerView(emergencyList)
            }*/

            // 선택된 마커가 있는 경우 unselected 상태로 변경
            this.emergencySelectedMarker?.let { marker ->
                marker.icon = OverlayImage.fromResource(R.drawable.marker_unselected_emergency_icon)
                marker.isHideCollidedMarkers = true
                marker.isForceShowIcon = false
                marker.width = 56
                marker.height = 60
                marker.zIndex = 15
                this.emergencySelectedMarker = null
            }

            this.aedSelectedMarker?.let { marker ->
                marker.icon = OverlayImage.fromResource(R.drawable.marker_unselected_aed_icon)
                marker.isHideCollidedMarkers = true
                marker.isForceShowIcon = false
                marker.width = 56
                marker.height = 60
                marker.zIndex = 0
                this.emergencySelectedMarker = null
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            launcherForPermission.launch(REQUEST_LOCATION_PERMISSIONS)
        } else {
            permissionGrantedMapUiSetting()
        }
    }
    private fun permissionGrantedMapUiSetting() {
        with(naverMap) {
            setMapType(this)

            // 내장 위치 추적 기능 사용
            locationSource = mLocationSource

            naverMap.addOnOptionChangeListener {
                val mode = naverMap.locationTrackingMode.name

                when (mode) {
                    "None" -> locationTrackingMode = LocationTrackingMode.Follow
                    "Follow", "NoFollow" -> {
                        permissionGrantedMapSetting()
                    }
                }
            }

            with(naverMap.locationOverlay) {
                val color = getColor(R.color.maker_overlay)
                circleRadius = 200
                // setAlphaComponent : 투명도 지정
                // 0(완전 투명) ~ 255(완전 불투명)
                circleColor = ColorUtils.setAlphaComponent(color, 90)
            }

            /**
             * 사용자의 위치를 지도에서 추적하는 모드
             *
             * Follow : 위치를 추적하면서 카메라도 따라 움직이는 모드.
             * LocationOverlay와 카메라의 좌표가 사용자의 위치를 따라 움직입니다.
             * API나 제스처를 사용해 지도를 임의로 움직일 경우 모드가 NoFollow로 바뀝니다.
             *
             * NoFollow : 위치는 추적하지만 지도는 움직이지 않는 모드.
             * LocationOverlay가 사용자의 위치를 따라 움직이나 지도는 움직이지 않습니다.
             *
             * Face : 위치를 추적하면서 카메라의 좌표와 베어링도 따라 움직이는 모드.
             * LocationOverlay와 카메라의 좌표,
             * 베어링이 사용자의 위치, 사용자가 바라보고 있는 방향을 따라 움직입니다.
             * API나 제스처를 사용해 지도를 임의로 움직일 경우 모드가 NoFollow로 바뀝니다.
             *
             * */
            locationTrackingMode = LocationTrackingMode.Follow

            // 건물 내부 표시
            isIndoorEnabled = true

            with(uiSettings) {
                // 줌버튼
                isZoomControlEnabled = false

                // 실내지도 층 피커
                isIndoorLevelPickerEnabled = true

                // 축적바
                isScaleBarEnabled = true

                // 현위치 버튼
                isLocationButtonEnabled = true
            }
        }
    }

    private fun permissionGrantedMapSetting() {
        if (ActivityCompat.checkSelfPermission(
                this@EmergencyMapActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@EmergencyMapActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            launcherForPermission.launch(REQUEST_LOCATION_PERMISSIONS)
        } else {

            // 사용자 현재 위치 받아오기
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                val latitude = location?.latitude ?: 37.5670135
                val longitude = location?.longitude ?: 126.9783740

                val coords = "$longitude,$latitude"

                viewModel.getUserLocationRegion(coords)

                viewModel.emergencyMapInfo.observe(this@EmergencyMapActivity) { item ->

                    val boundsBuilder = LatLngBounds.Builder()
                    val emergencyMapList = mutableListOf<EmergencyMapInfo>()

                    item.filter {
                        it.emergencyType == "hospital" &&
                                it.hospitalList?.hospitalLat != null &&
                                it.hospitalList?.hospitalLon != null
                    }.map {
                        val hospitalList = it.hospitalList
                        val hospitalLat = hospitalList?.hospitalLat
                        val hospitalLon = hospitalList?.hospitalLon

                        if (hospitalList != null && hospitalLat != null && hospitalLon != null) {
                            addEmergencyMarker(hospitalList, hospitalLat, hospitalLon)
                            emergencyMapList.add(it)
                            boundsBuilder.include(LatLng(hospitalLat, hospitalLon))
                        }
                    }

                    item.filter {
                        it.emergencyType == "aed" &&
                                it.aedList?.aedLat != null &&
                                it.aedList?.aedLon != null
                    }.map {
                        val aedList = it.aedList
                        val aedLat = aedList?.aedLat
                        val aedLon = aedList?.aedLon

                        if (aedList != null && aedLat != null && aedLon != null) {
                            addAedMarker(aedList, aedLat, aedLon)
                            if(emergencyMapList.isNullOrEmpty()){
                                boundsBuilder.include(LatLng(aedLat, aedLon))
                            }
                        }
                    }

                    setBottomRecylcerView(item)

                    val bounds = boundsBuilder.build()
                    val cameraUpdate = CameraUpdate.scrollAndZoomTo(bounds.center, 11.0)
                        .finishCallback {
                            lifecycleScope.launch {
                                delay(500)
                                binding.emergencyMapProgressBar.setProgressCompat(100, true)
                            }
                        }
                    naverMap.moveCamera(cameraUpdate)


                }

                // 위치 오버레이 설정
                with(naverMap.locationOverlay) {
                    isVisible = true
                    position = LatLng(latitude, longitude)
                }
            }
        }
    }

    private fun permissionDeniedMapUiSetting() {
        with(naverMap) {
            setMapType(this)

            // 건물 내부 표시
            isIndoorEnabled = true

            with(uiSettings) {
                // 줌버튼
                isZoomControlEnabled = false

                // 실내지도 층 피커
                isIndoorLevelPickerEnabled = true

                // 축적바
                isScaleBarEnabled = true
            }
        }
    }

    private fun permissionDeniedMapSetting() {
        val latitude = 37.5670135
        val longitude = 126.9783740

        val coords = "$longitude,$latitude"

        viewModel.getUserLocationRegion(coords)

        viewModel.emergencyMapInfo.observe(this@EmergencyMapActivity) { item ->
            val boundsBuilder = LatLngBounds.Builder()
            val emergencyMapList = mutableListOf<EmergencyMapInfo>()

            item.filter {
                it.emergencyType == "hospital" &&
                        it.hospitalList?.hospitalLat != null &&
                        it.hospitalList?.hospitalLon != null
            }.map {
                val hospitalList = it.hospitalList
                val hospitalLat = hospitalList?.hospitalLat
                val hospitalLon = hospitalList?.hospitalLon

                if (hospitalList != null && hospitalLat != null && hospitalLon != null) {
                    addEmergencyMarker(hospitalList, hospitalLat, hospitalLon)
                    emergencyMapList.add(it)
                    boundsBuilder.include(LatLng(hospitalLat, hospitalLon))
                }
            }

            item.filter {
                it.emergencyType == "aed" &&
                        it.aedList?.aedLat != null &&
                        it.aedList?.aedLon != null
            }.map {
                val aedList = it.aedList
                val aedLat = aedList?.aedLat
                val aedLon = aedList?.aedLon

                if (aedList != null && aedLat != null && aedLon != null) {
                    addAedMarker(aedList, aedLat, aedLon)
                    if(emergencyMapList.isNullOrEmpty()){
                        boundsBuilder.include(LatLng(aedLat, aedLon))
                    }
                }
            }

            setBottomRecylcerView(item)

            val bounds = boundsBuilder.build()
            val cameraUpdate = CameraUpdate.scrollAndZoomTo(bounds.center, 11.0)
                .finishCallback {
                    lifecycleScope.launch {
                        delay(500)
                        binding.emergencyMapProgressBar.setProgressCompat(100, true)
                    }
                }
            naverMap.moveCamera(cameraUpdate)
        }
        // 위치 오버레이 설정
        with(naverMap.locationOverlay) {
            isVisible = true
            position = LatLng(latitude, longitude)
        }
    }

    private fun getEmergencyMap(){
        viewModel.area.observe(this@EmergencyMapActivity) { area ->
            area?.split(" ")?.let { parts ->
                if (parts.isNotEmpty()) {
                    when {
                        parts.size > 2 -> {
                            val (STAGE1, STAGE2, STAGE3) = parts
                            viewModel.getEmergencyMapInfo(STAGE1, "$STAGE2 $STAGE3")
                        }
                        parts.size == 2 -> {
                            val (STAGE1, STAGE2) = parts
                            viewModel.getEmergencyMapInfo(STAGE1, STAGE2)
                        }
                        else -> {
                            viewModel.getEmergencyMapInfo(area, null)
                        }
                    }
                }
            }
        }
    }

    private fun addAedMarker(
        aedMapInfo: AedMapInfo,
        latitude: Double,
        longitude: Double
    ) {
        val marker = Marker().apply {
            icon = OverlayImage.fromResource(R.drawable.marker_unselected_aed_icon)
            position = LatLng(
                aedMapInfo.aedLat ?: latitude,
                aedMapInfo.aedLon ?: longitude
            )
            zIndex = 0
            map = naverMap
            width = 56
            height = 60
            this.isHideCollidedMarkers = true
            this.isForceShowIcon = false

            setOnClickListener {
                // 이전에 선택된 마커가 있다면 초기 상태로 되돌림
                this@EmergencyMapActivity.emergencySelectedMarker?.let { previousMarker ->
                    previousMarker.icon = OverlayImage.fromResource(R.drawable.marker_unselected_emergency_icon)
                    previousMarker.isHideCollidedMarkers = true
                    previousMarker.isForceShowIcon = false
                    previousMarker.width = 56
                    previousMarker.height = 60
                    previousMarker.zIndex = 15
                }

                this@EmergencyMapActivity.aedSelectedMarker?.let { previousMarker ->
                    previousMarker.icon = OverlayImage.fromResource(R.drawable.marker_unselected_aed_icon)
                    previousMarker.isHideCollidedMarkers = true
                    previousMarker.isForceShowIcon = false
                    previousMarker.width = 56
                    previousMarker.height = 60
                    previousMarker.zIndex = 0
                }

                // 현재 선택된 마커 업데이트
                this@EmergencyMapActivity.aedSelectedMarker = this

                // 클릭된 마커를 선택 상태로 변경
                this.icon = OverlayImage.fromResource(R.drawable.marker_selected_aed_icon)
                this.isHideCollidedMarkers = true
                this.isForceShowIcon = false
                this.width = 100
                this.height = 130
                this.zIndex = 10

                setBottomRecylcerView(
                    listOf(
                        EmergencyMapInfo(
                            null,
                            "aed",
                            null,
                            aedMapInfo
                        )
                    )
                )
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                true
            }
        }

        marker.map = naverMap
        markers.add(marker)
    }

    private fun addEmergencyMarker(
        hospitalMapInfo: HospitalMapInfo?,
        latitude: Double,
        longitude: Double
    ) {
        val marker = Marker().apply {
            icon = OverlayImage.fromResource(R.drawable.marker_unselected_emergency_icon)
            position = LatLng(
                hospitalMapInfo?.hospitalLat ?: latitude,
                hospitalMapInfo?.hospitalLon ?: longitude
            )
            map = naverMap
            width = 56
            height = 60
            zIndex = 15
            isHideCollidedMarkers = true
            isForceShowIcon = false

            setOnClickListener {
                // 이전에 선택된 마커가 있다면 초기 상태로 되돌림
                this@EmergencyMapActivity.emergencySelectedMarker?.let { previousMarker ->
                    previousMarker.icon = OverlayImage.fromResource(R.drawable.marker_unselected_emergency_icon)
                    previousMarker.isHideCollidedMarkers = true
                    previousMarker.isForceShowIcon = false
                    previousMarker.width = 56
                    previousMarker.height = 60
                    previousMarker.zIndex = 15
                }

                this@EmergencyMapActivity.aedSelectedMarker?.let { previousMarker ->
                    previousMarker.icon = OverlayImage.fromResource(R.drawable.marker_unselected_aed_icon)
                    previousMarker.isHideCollidedMarkers = true
                    previousMarker.isForceShowIcon = false
                    previousMarker.width = 56
                    previousMarker.height = 60
                    previousMarker.zIndex = 0
                }

                // 현재 선택된 마커 업데이트
                this@EmergencyMapActivity.emergencySelectedMarker = this

                // 클릭된 마커를 선택 상태로 변경
                this.icon = OverlayImage.fromResource(R.drawable.marker_selected_emergency_icon)
                this.isHideCollidedMarkers = true
                this.isForceShowIcon = true
                this.width = 100
                this.height = 130
                this.zIndex = 20

                setBottomRecylcerView(
                    listOf(
                        EmergencyMapInfo(
                            hospitalMapInfo,
                            "hospital",
                            hospitalMapInfo?.hospitalId.toString(),
                            null
                        )
                    )
                )
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                true
            }
        }

        marker.map = naverMap
        markers.add(marker)
    }

    private fun isNightMode(): Boolean {
        val currentNightMode =
            this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    private fun setMapType(naverMap: NaverMap) {
        if (isNightMode()) {
            // 야간 모드 허용
            naverMap.mapType = NaverMap.MapType.Navi
            naverMap.isNightModeEnabled = true
        } else {
            naverMap.mapType = NaverMap.MapType.Basic
            naverMap.isNightModeEnabled = false
        }
    }


    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 100
        val REQUEST_LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }

}