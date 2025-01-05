package kr.techit.lion.presentation.main.vm.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.domain.model.AppTheme
import kr.techit.lion.domain.model.AppTheme.Companion.getNewTheme
import kr.techit.lion.domain.model.mainplace.AroundPlace
import kr.techit.lion.domain.model.mainplace.RecommendPlace
import kr.techit.lion.domain.repository.ActivationRepository
import kr.techit.lion.domain.repository.AppThemeRepository
import kr.techit.lion.domain.repository.AreaCodeRepository
import kr.techit.lion.domain.repository.NaverMapRepository
import kr.techit.lion.domain.repository.PlaceRepository
import kr.techit.lion.domain.repository.SigunguCodeRepository
import kr.techit.lion.presentation.delegate.NetworkErrorDelegate
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.shareInUi
import kr.techit.lion.presentation.ext.stateInUi
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appThemeRepository: AppThemeRepository,
    private val placeRepository: PlaceRepository,
    private val areaCodeRepository: AreaCodeRepository,
    private val sigunguCodeRepository: SigunguCodeRepository,
    private val activationRepository: ActivationRepository,
    private val naverMapRepository: NaverMapRepository
) : ViewModel() {

    companion object {
        const val DEFAULT_AREA = "서울특별시"
        const val DEFAULT_SIGUNGU = "중구"
    }

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate
    val networkState: StateFlow<NetworkState> get() = networkErrorDelegate.networkState

    private val _aroundPlaceInfo = MutableLiveData<List<AroundPlace>>()
    val aroundPlaceInfo: LiveData<List<AroundPlace>> = _aroundPlaceInfo

    private val _recommendPlaceInfo = MutableLiveData<List<RecommendPlace>>()
    val recommendPlaceInfo: LiveData<List<RecommendPlace>> = _recommendPlaceInfo

    val userActivationState = activationRepository
        .userActivation
        .shareInUi(scope = viewModelScope)

    private val _area = MutableLiveData<String>()
    val area: LiveData<String> = _area

    private val _locationMessage = MutableLiveData<String>()
    val locationMessage: LiveData<String> get() = _locationMessage

    val appTheme = appThemeRepository.getAppTheme().stateInUi(
        viewModelScope, AppTheme.LOADING
    )

    private fun setAppTheme(appTheme: AppTheme) {
        viewModelScope.launch {
            appThemeRepository.saveAppTheme(appTheme)
        }
    }

    // 상단 테마 토글 버튼 클릭시
    fun onClickThemeToggleButton(isDarkTheme: Boolean) {
        val newAppTheme = getNewTheme(appTheme.value, isDarkTheme)
        setAppTheme(newAppTheme)
    }

    // 테마 설정 다이얼로그 클릭시
    fun onClickThemeChangeButton(theme: AppTheme) {
        viewModelScope.launch {
            setAppTheme(theme)
            activationRepository.saveUserActivation(false)
        }
    }

    fun getPlaceMain(area: String, sigungu: String) = viewModelScope.launch(Dispatchers.IO) {

        var areaCode = getAreaCode(area)
        var sigunguCode = areaCode?.let { getSigunguCode(sigungu, it) }

        if (areaCode == null || sigunguCode == null) {
            _locationMessage.postValue("위치를 찾을 수 없어 기본값($DEFAULT_AREA, $DEFAULT_SIGUNGU)으로 설정합니다.")
            areaCode = getAreaCode(DEFAULT_AREA)
            sigunguCode = areaCode?.let { getSigunguCode(DEFAULT_SIGUNGU, it) }
        }

        if (areaCode != null && sigunguCode != null) {
            placeRepository.getPlaceMainInfo(areaCode, sigunguCode).onSuccess {
                _aroundPlaceInfo.postValue(it.aroundPlaceList)
                _recommendPlaceInfo.postValue(it.recommendPlaceList)

                networkErrorDelegate.handleNetworkSuccess()
            }.onError {
                networkErrorDelegate.handleNetworkError(it)
            }
        }
    }

    private suspend fun getAreaCode(area: String) = suspendCoroutine { continutation ->
        continutation.resume(areaCodeRepository.getAreaCodeByName(area))
    }

    private suspend fun getSigunguCode(sigungu: String, areaCode: String) =
        suspendCoroutine { continutation ->
            continutation.resume(
                sigunguCodeRepository.getSigunguCodeByVillageName(
                    sigungu,
                    areaCode
                )
            )
        }

    fun getUserLocationRegion(coords: String) = viewModelScope.launch {
        naverMapRepository.getReverseGeoCode(coords).onSuccess {
            if (it.code == 0) {
                if (it.results[0].areaDetail.isNullOrEmpty()) {
                    _area.value = it.results[0].area.toString()
                } else {
                    _area.value = "${it.results[0].area} ${it.results[0].areaDetail}"
                }
            } else {
                _area.value = "결과없음"
            }
        }.onError {
            networkErrorDelegate.handleNetworkError(it)
        }
    }
}