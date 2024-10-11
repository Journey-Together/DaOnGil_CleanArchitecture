package kr.techit.lion.presentation.schedulereview.vm

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.domain.model.schedule.BriefScheduleInfo
import kr.techit.lion.domain.model.schedule.NewScheduleReview
import kr.techit.lion.domain.model.schedule.ReviewImg
import kr.techit.lion.domain.repository.PlanRepository
import kr.techit.lion.presentation.delegate.NetworkErrorDelegate
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.convertPeriodToDate
import kr.techit.lion.presentation.ext.convertStringToDate
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class WriteScheduleReviewViewModel @Inject constructor (
    private val planRepository : PlanRepository,
): ViewModel(){

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    val networkState: StateFlow<NetworkState> get() = networkErrorDelegate.networkState

    private val _briefSchedule = MutableLiveData<BriefScheduleInfo?>()
    val briefSchedule: LiveData<BriefScheduleInfo?> get() = _briefSchedule

    private val _imageUriList = MutableLiveData<List<Uri>>()
    val imageUriList: LiveData<List<Uri>> get() = _imageUriList

    private val _imagePaths = MutableLiveData<List<ReviewImg>>()
    val imagePaths: LiveData<List<ReviewImg>> get() = _imagePaths

    private val _numOfImages = MutableLiveData<Int>(0)
    val numOfImages: LiveData<Int> get() = _numOfImages

    fun resetNetworkState() {
        networkErrorDelegate.handleNetworkSuccess()
    }

    fun getBriefScheduleInfo(planId: Long){
        viewModelScope.launch {
            planRepository.getBriefScheduleInfo(planId).onSuccess {
                _briefSchedule.postValue(it)
            }.onError {
                networkErrorDelegate.handleNetworkError(it)
            }
        }
    }

    fun addNewReviewImage(imgUri: Uri, imagePath: String){
        val currentUriList = _imageUriList.value?.toMutableList() ?: mutableListOf<Uri>()
        currentUriList.add(imgUri)
        currentUriList.let { _imageUriList.value = it }

        val currentPaths = _imagePaths.value?.toMutableList() ?: mutableListOf<ReviewImg>()
        currentPaths.add(ReviewImg(imagePath))
        currentPaths.let { _imagePaths.value = it }

        updateNumOfImages()
    }

    fun removeReviewImageFromList(position: Int){
        val currentUriList = _imageUriList.value?.toMutableList() ?: mutableListOf<Uri>()
        currentUriList.removeAt(position)
        currentUriList.let { _imageUriList.value = it }

        val currentPaths = _imagePaths.value?.toMutableList() ?: mutableListOf<ReviewImg>()
        currentPaths.removeAt(position)
        currentPaths.let { _imagePaths.value = it }

        updateNumOfImages()
    }

    private fun updateNumOfImages(){
        _numOfImages.value = _imageUriList.value?.size ?: 0
    }

    fun isMoreImageAttachable(): Boolean{
        val currentValue = _numOfImages.value ?: 0
        return currentValue in 0..3
    }

    fun submitScheduleReview(planId: Long, reviewDetail: NewScheduleReview, callback: (Boolean, Boolean) -> Unit){
        val images = _imagePaths.value?.toMutableList() ?: mutableListOf<ReviewImg>()
        viewModelScope.launch {
            var requestFlag = false
            val success = try {
                networkErrorDelegate.handleNetworkLoading()

                planRepository.addNewScheduleReview(planId, reviewDetail, images)
                    .onSuccess {
                        requestFlag = true

                        networkErrorDelegate.handleNetworkSuccess()
                    }
                    .onError {
                        networkErrorDelegate.handleNetworkError(it)
                    }
                true
            } catch (e: Exception) {
                Log.d("submitScheduleReview", "Error: ${e.message}")
                false
            }
            callback(success, requestFlag)
        }
    }

    fun getScheduleInfoAccessibilityText(): String {
        val title = _briefSchedule.value?.title ?: ""

        val startDate = _briefSchedule.value?.startDate?.convertStringToDate() ?: Date()
        val endDate = _briefSchedule.value?.endDate?.convertStringToDate() ?: Date()
        val periodString = startDate.convertPeriodToDate(endDate)

        val scheduleInfo = listOf("여행 일정", title, periodString).joinToString(" ")

        return scheduleInfo
    }
}