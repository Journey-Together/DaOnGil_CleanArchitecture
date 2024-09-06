package kr.tekit.lion.presentation.emergency.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.model.EmergencyMessageInfo
import kr.tekit.lion.domain.repository.EmergencyRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import javax.inject.Inject

@HiltViewModel
class EmergencyInfoViewModel @Inject constructor(
    private val emergencyRepository: EmergencyRepository
): ViewModel() {


    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    private val _messageList = MutableLiveData<List<EmergencyMessageInfo>>()
    val messageList: LiveData<List<EmergencyMessageInfo>> = _messageList

    fun getEmergencyMessage(hospitalId: String) =
        viewModelScope.launch {
            emergencyRepository.getEmergencyMessage(hospitalId).onSuccess {
                _messageList.value = it
            }.onError {
                networkErrorDelegate.handleNetworkError(it)
            }
        }
}