package kr.tekit.lion.presentation.schedulereview.vm

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.model.schedule.BriefScheduleInfo
import kr.tekit.lion.domain.model.schedule.NewScheduleReview
import kr.tekit.lion.domain.model.schedule.ReviewImg
import kr.tekit.lion.domain.repository.PlanRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import javax.inject.Inject

@HiltViewModel
class WriteScheduleReviewViewModel @Inject constructor (
    private val planRepository : PlanRepository,
): ViewModel(){

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    private val _briefSchedule = MutableLiveData<BriefScheduleInfo?>()
    val briefSchedule: LiveData<BriefScheduleInfo?> get() = _briefSchedule

    private val _imageUriList = MutableLiveData<List<Uri>>()
    val imageUriList: LiveData<List<Uri>> get() = _imageUriList

    private val _imagePaths = MutableLiveData<List<ReviewImg>>()
    val imagePaths: LiveData<List<ReviewImg>> get() = _imagePaths

    private val _numOfImages = MutableLiveData<Int>(0)
    val numOfImages: LiveData<Int> get() = _numOfImages

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
                planRepository.addNewScheduleReview(planId, reviewDetail, images)
                    .onSuccess {
                        requestFlag = true
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
}