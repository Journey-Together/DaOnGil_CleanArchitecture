package kr.tekit.lion.presentation.emergency.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.model.EmergencyMapInfo
import kr.tekit.lion.domain.repository.NaverMapRepository
import kr.tekit.lion.domain.usecase.base.onSuccess
import kr.tekit.lion.domain.usecase.emergency.GetEmergencyMapInfoUseCase
import javax.inject.Inject

@HiltViewModel
class EmergencyMapViewModel @Inject constructor(
    private val naverMapRepository: NaverMapRepository,
    private val getEmergencyMapInfoUseCase: GetEmergencyMapInfoUseCase
): ViewModel() {

    private val _area = MutableLiveData<String?>()
    val area : LiveData<String?> = _area

    private val _emergencyMapInfo = MutableLiveData<List<EmergencyMapInfo>>()
    val emergencyMapInfo: LiveData<List<EmergencyMapInfo>> = _emergencyMapInfo

    fun getUserLocationRegion(coords: String) = viewModelScope.launch {
        naverMapRepository.getReverseGeoCode(coords).onSuccess {
            if(it.code == 0){
                _area.value = "${it.results[0].area} ${it.results[0].areaDetail}"
            }
        }
    }

    fun getEmergencyMapInfo(area: String?, areaDetail: String?) =
        viewModelScope.launch {
            getEmergencyMapInfoUseCase(area, areaDetail).onSuccess {
                _emergencyMapInfo.value = it
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