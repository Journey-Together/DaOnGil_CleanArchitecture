package kr.tekit.lion.presentation.schedulereview.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.model.schedule.ModifiedScheduleReview
import kr.tekit.lion.domain.model.schedule.ReviewImage
import kr.tekit.lion.domain.model.schedule.ScheduleReviewInfo
import kr.tekit.lion.domain.repository.PlanRepository
import kr.tekit.lion.domain.usecase.base.onError
import kr.tekit.lion.domain.usecase.base.onSuccess
import kr.tekit.lion.domain.usecase.plan.GetScheduleReviewInfoUseCase
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import kr.tekit.lion.presentation.delegate.NetworkState
import java.net.URI
import javax.inject.Inject

@HiltViewModel
data class ModifyScheduleReviewViewModel @Inject constructor(
    private val planRepository: PlanRepository,
    private val getScheduleReviewUseCase: GetScheduleReviewInfoUseCase,
) : ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    val networkState: StateFlow<NetworkState> get() = networkErrorDelegate.networkState

    // 이미지 url, uri, path
    private val _imageList = MutableLiveData<List<ReviewImage>>()
    val imageList: LiveData<List<ReviewImage>> get() = _imageList

    private val _numOfImages = MutableLiveData<Int>(0)
    val numOfImages: LiveData<Int> get() = _numOfImages

    private val _originalReview = MutableLiveData<ScheduleReviewInfo>()
    val originalReview: LiveData<ScheduleReviewInfo> get() = _originalReview

    private val _deleteImgUrls = MutableLiveData<List<String>>()

    fun resetNetworkState() {
        networkErrorDelegate.handleNetworkSuccess()
    }

    fun addNewReviewImage(newImage: ReviewImage) {
        val currentImageList = _imageList.value?.toMutableList() ?: mutableListOf<ReviewImage>()
        currentImageList.add(newImage)
        currentImageList.let { _imageList.value = it }

        updateNumOfImages()
    }

    fun removeReviewImageFromList(position: Int) {
        val imageUrl = _imageList.value?.get(position)?.imageUrl
        if(imageUrl != null){
            addDeletedImageUrl(imageUrl)
        }

        val currentImageList = _imageList.value?.toMutableList() ?: mutableListOf<ReviewImage>()
        currentImageList.removeAt(position)
        currentImageList.let { _imageList.value = it }

        updateNumOfImages()
    }

    private fun addDeletedImageUrl(url: String) {
        val currentUrlList = _deleteImgUrls.value?.toMutableList() ?: mutableListOf<String>()
        currentUrlList.add(url)
        currentUrlList.let { _deleteImgUrls.value = it }
    }

    private fun updateNumOfImages() {
        _numOfImages.value = _imageList.value?.size ?: 0
    }

    fun isMoreImageAttachable(): Boolean {
        val currentValue = _numOfImages.value ?: 0
        return currentValue in 0..3
    }

    fun getScheduleReviewInfo(planId: Long) = viewModelScope.launch {
        getScheduleReviewUseCase(planId)
            .onSuccess {
                _originalReview.value = it
                initReviewImages()
            }.onError {

            }
    }

    private fun initReviewImages() {
        _originalReview.value?.imageList?.let {
            val reviewImages = it.map { imageUrl ->
                ReviewImage(
                    imageUrl = imageUrl,
                    imageUri = URI(imageUrl),
                    imagePath = null
                )
            }

            _imageList.value = reviewImages
            updateNumOfImages()
        }
    }

    fun updateScheduleReview(
        grade: Float,
        content: String,
        callback: (Boolean, Boolean) -> Unit
    ) {
        val newGrade = if (isGradeSame(grade)) null else grade
        val newContent = if (isContentSame(content)) null else content
        val deleteImgUrls = _deleteImgUrls.value

        val modifiedReview = ModifiedScheduleReview(newGrade, newContent, deleteImgUrls)

        val images = getNewImages()

        _originalReview.value?.reviewId?.let { reviewId ->
            viewModelScope.launch {
                var requestFlag = false
                val success = try {
                    networkErrorDelegate.handleNetworkLoading()

                    planRepository.modifyScheduleReview(reviewId, modifiedReview, images)
                        .onSuccess {
                            requestFlag = true

                            networkErrorDelegate.handleNetworkSuccess()
                        }.onError {
                            networkErrorDelegate.handleNetworkError(it)
                        }
                    true
                } catch (e: Exception) {
                    Log.d("updateScheduleReview", "Error: ${e.message}")
                    false
                }
                callback(success, requestFlag)
            }
        }
    }

    private fun isContentSame(newContent: String) : Boolean {
        return _originalReview.value?.content?.let {
            it == newContent
        } ?: true
    }

    private fun isGradeSame(newGrade: Float) : Boolean {
        return _originalReview.value?.grade?.let {
            it == newGrade
        } ?: true
    }

    private fun getNewImages() : List<ReviewImage> {
        val originalImageSize = _originalReview.value?.imageList?.size ?: 0
        val deletedImageSize = _deleteImgUrls.value?.size ?: 0
        val currentImageSize = _imageList.value?.size ?: 0

        val startPoint = originalImageSize - deletedImageSize

        val newImages = _imageList.value?.subList(startPoint, currentImageSize) ?: emptyList()

        return newImages
    }
}
