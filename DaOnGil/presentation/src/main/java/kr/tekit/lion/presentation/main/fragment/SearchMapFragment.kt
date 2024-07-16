package kr.tekit.lion.presentation.main.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.chip.Chip
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentSearchMapBinding
import kr.tekit.lion.presentation.ext.Permissions.LOCATION_PERMISSION_REQUEST_CODE
import kr.tekit.lion.presentation.ext.Permissions.REQUEST_LOCATION_PERMISSIONS
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
import kr.tekit.lion.presentation.ext.setClickEvent
import kr.tekit.lion.presentation.ext.showPermissionSnackBar
import kr.tekit.lion.presentation.main.CategoryBottomSheet
import kr.tekit.lion.presentation.main.model.DisabilityType
import kr.tekit.lion.presentation.main.model.ElderlyPeople
import kr.tekit.lion.presentation.main.model.HearingImpairment
import kr.tekit.lion.presentation.main.model.InfantFamily
import kr.tekit.lion.presentation.main.model.PhysicalDisability
import kr.tekit.lion.presentation.main.model.VisualImpairment
import kr.tekit.lion.presentation.main.vm.SearchMainViewModel

@AndroidEntryPoint
class SearchMapFragment : Fragment(R.layout.fragment_search_map), OnMapReadyCallback {
    private val viewModel: SearchMainViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private lateinit var launcherForPermission: ActivityResultLauncher<Array<String>>

    // 내장 위치 추적 기능 사용
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSearchMapBinding.bind(view)

        initMap()
        subscribeOptionStates(binding)

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
                                requireContext().showPermissionSnackBar(binding.root)
                            }
                        }

                        else -> {
                            permissionDeniedMapUiSetting()
                            requireContext().showPermissionSnackBar(binding.root)
                        }
                    }
                }
            }
        }
    }

    private fun subscribeOptionStates(binding: FragmentSearchMapBinding) {
        with(binding) {
            collectOptions(viewModel.physicalDisabilityOptions, chipPhysicalDisability, R.string.text_physical_disability, PhysicalDisability)
            collectOptions(viewModel.hearingImpairmentOptions, chipHearingImpairment, R.string.text_hearing_impairment, HearingImpairment)
            collectOptions(viewModel.visualImpairmentOptions, chipVisualImpairment, R.string.text_visual_impairment, VisualImpairment)
            collectOptions(viewModel.infantFamilyOptions, chipInfantFamilly, R.string.text_infant_family, InfantFamily)
            collectOptions(viewModel.elderlyPersonOptions, chipElderlyPeople, R.string.text_elderly_person, ElderlyPeople)
        }
    }

    private fun collectOptions(optionState: Flow<List<Int>>, chip: Chip, textResId: Int, disabilityType: DisabilityType){
        repeatOnViewStarted {
            optionState.collect{ options ->
                chip.setClickEvent(this) {
                    showBottomSheet(options, disabilityType)
                }

                val text = if (options.isNotEmpty()) {
                    "${getString(textResId)}(${options.size})"
                } else {
                    getString(textResId)
                }
                chip.text = text
            }
        }
    }

    private fun initMap() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        mLocationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        // 네이버맵 동적으로 불러오기
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    private fun permissionGrantedMapUiSetting() {
        with(naverMap) {
            //setMapType(this)

            // 내장 위치 추적 기능 사용
            locationSource = mLocationSource

            naverMap.addOnOptionChangeListener {
                val mode = naverMap.locationTrackingMode.name
                val currentLocation = mLocationSource.lastLocation

                when (mode) {
                    "None" -> locationTrackingMode = LocationTrackingMode.Follow
                    "Follow", "NoFollow" -> {
                        // 현재 위치 버튼을 눌렀을 때 카메라가 줌이 너무 작아지는걸 방지
                        if (currentLocation != null) {
                            val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                            val cameraPosition = CameraPosition(latLng, 14.0)

                            naverMap.moveCamera(
                                CameraUpdate.toCameraPosition(cameraPosition)
                                    .animate(
                                        com.naver.maps.map.CameraAnimation.Easing
                                    )
                            )
                        }
                    }
                }
            }

            with(naverMap.locationOverlay) {
                val color = getColor(requireContext(), R.color.maker_overlay)
                circleRadius = 200
                // setAlphaComponent : 투명도 지정
                // 0(완전 투명) ~ 255(완전 불투명)
                circleColor = androidx.core.graphics.ColorUtils.setAlphaComponent(color, 90)
            }

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

    private fun permissionDeniedMapUiSetting() {
        with(naverMap) {
            //setMapType(this)

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

    private fun addMaker() {
        this.repeatOnViewStarted {

            val marker = Marker()
            with(marker) {
                icon = OverlayImage.fromResource(R.drawable.maker_unselected_restaurant_icon)
                position = LatLng(
                    37.2792385,
                    127.0346949
                )
                map = naverMap
                width = 86
                height = 90

                setOnClickListener {
                    true
                }
            }
        }
    }

    @UiThread
    override fun onMapReady(p0: NaverMap) {

        this.naverMap = p0

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            launcherForPermission.launch(REQUEST_LOCATION_PERMISSIONS)
        } else {
            permissionGrantedMapUiSetting()


            naverMap.addOnCameraChangeListener { reason: Int, animated: Boolean ->
                val bounds = naverMap.contentBounds
                val northEast = bounds.northEast
                val southWest = bounds.southWest

                val northWest = LatLng(northEast.latitude, southWest.longitude)
                val southEast = LatLng(southWest.latitude, northEast.longitude)

                val maxX = northEast.latitude
                val maxY = northEast.longitude
                val minX = southWest.latitude
                val southWestLng = southWest.longitude
                val northWestLat = northWest.latitude
                val northWestLng = northWest.longitude
                val southEastLat = southEast.latitude
                val minY = southEast.longitude


            }

            // 사용자 현재 위치 받아오기
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                val latitude = location?.latitude ?: 35.1798159
                val longitude = location?.longitude ?: 129.0750222

                // 위치 오버레이의 가시성은 기본적으로 false로 지정되어 있습니다. 가시성을 true로 변경하면 지도에 위치 오버레이가 나타납니다.
                // 파랑색 점, 현재 위치 표시
                with(naverMap.locationOverlay) {
                    isVisible = true
                    position = LatLng(latitude, longitude)
                }

                // 카메라 현재 위치로 이동
                val cameraUpdate = CameraUpdate.scrollTo(
                    LatLng(
                        latitude,
                        longitude
                    )
                )
                naverMap.moveCamera(cameraUpdate)
            }
        }
        addMaker()
    }

    private fun showBottomSheet(selectedOptions: List<Int>, disabilityType: DisabilityType) {
        CategoryBottomSheet(selectedOptions, disabilityType) { optionIds, optionNames ->
            viewModel.onSelectOption(optionIds, optionNames, disabilityType)
        }.show(parentFragmentManager, "bottomSheet")
    }
}