package kr.techit.lion.presentation.emergency.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.domain.model.EmergencyMapInfo
import kr.techit.lion.domain.repository.NaverMapRepository
import kr.techit.lion.domain.usecase.base.onError
import kr.techit.lion.domain.usecase.base.onSuccess
import kr.techit.lion.domain.usecase.emergency.GetEmergencyMapInfoUseCase
import kr.techit.lion.presentation.delegate.NetworkErrorDelegate
import javax.inject.Inject

@HiltViewModel
class EmergencyMapViewModel @Inject constructor(
    private val naverMapRepository: NaverMapRepository,
    private val getEmergencyMapInfoUseCase: GetEmergencyMapInfoUseCase
): ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    private val _area = MutableLiveData<String?>()
    val area : LiveData<String?> = _area

    private val _emergencyMapInfo = MutableLiveData<List<EmergencyMapInfo>>()
    val emergencyMapInfo: LiveData<List<EmergencyMapInfo>> = _emergencyMapInfo

    val networkState get() = networkErrorDelegate.networkState

    fun getUserLocationRegion(coords: String) = viewModelScope.launch {
        naverMapRepository.getReverseGeoCode(coords).onSuccess {
            if(it.code == 0){
                _area.value = "${it.results[0].area} ${it.results[0].areaDetail}"
            } else {
                _area.value = "서울특별시 중구"
            }
            networkErrorDelegate.handleNetworkSuccess()
        }.onError {
            networkErrorDelegate.handleNetworkError(it)
        }
    }

    fun getEmergencyMapInfo(area: String?, areaDetail: String?) =
        viewModelScope.launch {
            getEmergencyMapInfoUseCase(area, areaDetail).onSuccess {
                _emergencyMapInfo.value = it
                networkErrorDelegate.handleNetworkSuccess()
            }.onError {
                networkErrorDelegate.handleNetworkError(
                    networkErrorDelegate.handleUsecaseNetworkError(it)
                )
            }
        }

    fun setArea(area: String?, areaDetail: String?) {
        if(areaDetail.isNullOrEmpty()){
            _area.value = "$area"
        } else {
            _area.value = "$area $areaDetail"
        }
    }
}