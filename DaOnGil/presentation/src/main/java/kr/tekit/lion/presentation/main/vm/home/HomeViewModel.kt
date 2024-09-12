package kr.tekit.lion.presentation.main.vm.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.model.AppTheme
import kr.tekit.lion.domain.model.mainplace.AroundPlace
import kr.tekit.lion.domain.model.mainplace.RecommendPlace
import kr.tekit.lion.domain.repository.AppThemeRepository
import kr.tekit.lion.domain.repository.PlaceRepository
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.repository.ActivationRepository
import kr.tekit.lion.domain.repository.AreaCodeRepository
import kr.tekit.lion.domain.repository.NaverMapRepository
import kr.tekit.lion.domain.repository.SigunguCodeRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import kr.tekit.lion.presentation.delegate.NetworkState
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
): ViewModel() {

    init {
        viewModelScope.launch {
            checkUserActivation()
        }
    }

    companion object {
        const val DEFAULT_AREA = "서울특별시"
        const val DEFAULT_SIGUNGU = "중구"
    }

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate
    val networkState: StateFlow<NetworkState> get() = networkErrorDelegate.networkState

    private val _appTheme = MutableStateFlow(AppTheme.SYSTEM)
    val appTheme = _appTheme.asStateFlow()

    private val _aroundPlaceInfo = MutableLiveData<List<AroundPlace>>()
    val aroundPlaceInfo : LiveData<List<AroundPlace>> = _aroundPlaceInfo

    private val _recommendPlaceInfo = MutableLiveData<List<RecommendPlace>>()
    val recommendPlaceInfo : LiveData<List<RecommendPlace>> = _recommendPlaceInfo

    private val _userActivationState = MutableSharedFlow<Boolean>()
    val userActivationState = _userActivationState.asSharedFlow()

    private val _area = MutableLiveData<String>()
    val area : LiveData<String> = _area

    private val _locationMessage = MutableLiveData<String>()
    val locationMessage: LiveData<String> get() = _locationMessage

    fun checkAppTheme() = viewModelScope.launch{
        val appTheme = appThemeRepository.getAppTheme()
        _appTheme.value = appTheme
    }

    private fun checkUserActivation() {
        viewModelScope.launch {
            activationRepository.userActivation.collect {
                _userActivationState.emit(it)
            }
        }
    }

    private fun setAppTheme(appTheme: AppTheme) {
        viewModelScope.launch {
            appThemeRepository.saveAppTheme(appTheme)
            _appTheme.update { appTheme }
        }
    }

    // 상단 테마 토글 버튼 클릭시
    fun onClickThemeToggleButton(isDarkTheme: Boolean) {

        val newAppTheme = when(_appTheme.value){
            AppTheme.LIGHT -> AppTheme.HIGH_CONTRAST
            AppTheme.HIGH_CONTRAST -> AppTheme.LIGHT
            AppTheme.SYSTEM -> {
                if (isDarkTheme) AppTheme.LIGHT else AppTheme.HIGH_CONTRAST
            }
        }

        setAppTheme(newAppTheme)
    }

    // 테마 설정 다이얼로그 클릭시
    fun onClickThemeChangeButton(theme: AppTheme) = viewModelScope.launch {
        Log.d("idasdw", "theme : $theme")
        setAppTheme(theme)
        activationRepository.saveUserActivation(false)
    }

    fun getPlaceMain(area: String, sigungu: String) = viewModelScope.launch(Dispatchers.IO) {

        var areaCode = getAreaCode(area)
        var sigunguCode = getSigunguCode(sigungu)

        if (areaCode == null || sigunguCode == null) {
            _locationMessage.postValue("위치를 찾을 수 없어 기본값($DEFAULT_AREA, $DEFAULT_SIGUNGU)으로 설정합니다.")
            areaCode = getAreaCode(DEFAULT_AREA)
            sigunguCode = getSigunguCode(DEFAULT_SIGUNGU)
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

    private suspend fun getAreaCode(area:String) = suspendCoroutine { continutation ->
        continutation.resume(areaCodeRepository.getAreaCodeByName(area))
    }

    private suspend fun getSigunguCode(sigungu:String) = suspendCoroutine { continutation ->
        continutation.resume(sigunguCodeRepository.getSigunguCodeByVillageName(sigungu))
    }

    fun getUserLocationRegion(coords: String) = viewModelScope.launch {
        naverMapRepository.getReverseGeoCode(coords).onSuccess {
            if(it.code == 0){
                _area.value = "${it.results[0].area} ${it.results[0].areaDetail}"
            } else {
                _area.value = "결과없음"
            }
        }.onError {
            networkErrorDelegate.handleNetworkError(it)
        }
    }
}