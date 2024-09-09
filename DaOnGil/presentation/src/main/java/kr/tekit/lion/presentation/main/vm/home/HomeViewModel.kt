package kr.tekit.lion.presentation.main.vm.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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
import kr.tekit.lion.domain.repository.SigunguCodeRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appThemeRepository: AppThemeRepository,
    private val placeRepository: PlaceRepository,
    private val areaCodeRepository: AreaCodeRepository,
    private val sigunguCodeRepository: SigunguCodeRepository,
    private val activationRepository: ActivationRepository
): ViewModel() {

    init {
        viewModelScope.launch {
            checkFirstLogIn()
            val theme = appThemeRepository.getAppTheme()
            _appTheme.value = theme
        }
    }

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    private val _appTheme = MutableStateFlow(AppTheme.LIGHT)
    val appTheme = _appTheme.asStateFlow()

    private val _aroundPlaceInfo = MutableLiveData<List<AroundPlace>>()
    val aroundPlaceInfo : LiveData<List<AroundPlace>> = _aroundPlaceInfo

    private val _recommendPlaceInfo = MutableLiveData<List<RecommendPlace>>()
    val recommendPlaceInfo : LiveData<List<RecommendPlace>> = _recommendPlaceInfo

    private val _userActivationState = MutableSharedFlow<Boolean>()
    val userActivationState = _userActivationState.asSharedFlow()

    private fun setAppTheme(appTheme: AppTheme) {
        viewModelScope.launch {
            appThemeRepository.saveAppTheme(appTheme)
            _appTheme.update { appTheme }
        }
    }

    // 상단 테마 토글 버튼 클릭시
    fun onClickThemeToggleButton() {
        val newAppTheme = if (_appTheme.value == AppTheme.LIGHT) AppTheme.HIGH_CONTRAST else AppTheme.LIGHT
        setAppTheme(newAppTheme)
    }

    // 테마 설정 다이얼로그 클릭시
    fun onClickThemeChangeButton(theme: AppTheme, onSuccess: () -> Unit) = viewModelScope.launch {
        setAppTheme(theme)
        activationRepository.saveUserActivation(false)
        onSuccess()
    }

    fun getPlaceMain(area: String, sigungu: String) = viewModelScope.launch(Dispatchers.IO) {

      val areaCode = getAreaCode(area)
      val sigunguCode = getSigunguCode(sigungu)

      if (areaCode != null && sigunguCode != null) {
          placeRepository.getPlaceMainInfo(areaCode, sigunguCode).onSuccess {
              _aroundPlaceInfo.postValue(it.aroundPlaceList)
              _recommendPlaceInfo.postValue(it.recommendPlaceList)
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
}