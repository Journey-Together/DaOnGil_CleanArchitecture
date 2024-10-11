package kr.techit.lion.presentation.myreview.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.domain.model.MyPlaceReview
import kr.techit.lion.domain.model.MyPlaceReviewImages
import kr.techit.lion.domain.model.MyPlaceReviewInfo
import kr.techit.lion.domain.model.UpdateMyPlaceReview
import kr.techit.lion.domain.repository.PlaceRepository
import kr.techit.lion.presentation.delegate.NetworkErrorDelegate
import kr.techit.lion.presentation.home.model.ReviewInfo
import kr.techit.lion.presentation.home.model.toMyPlaceReviewInfo
import java.time.LocalDate
import javax.inject.Inject

private const val INITIAL_PAGE_NO = 0
private const val REVIEW_GET_SIZE = 5

@HiltViewModel
class MyReviewViewModel @Inject constructor(
    private val placeRepository: PlaceRepository
): ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    private val _myPlaceReview = MutableLiveData<MyPlaceReview>()
    val myPlaceReview: LiveData<MyPlaceReview> = _myPlaceReview

    private val _reviewData = MutableLiveData<MyPlaceReviewInfo>()
    val reviewData: LiveData<MyPlaceReviewInfo> = _reviewData

    private val _reviewImages = MutableLiveData<List<String>>()

    private val _newImages = MutableLiveData<List<String>>()

    private val _deletedImages = MutableLiveData<List<String>>()

    private val _visitDate = MutableLiveData<LocalDate>()
    val visitDate: LiveData<LocalDate> = _visitDate

    private val _numOfImages = MutableLiveData<Int>()
    val numOfImages: LiveData<Int> = _numOfImages

    private val _isLastPage = MutableLiveData(false)
    val isLastPage: LiveData<Boolean> = _isLastPage

    private val _isReviewDelete = MutableLiveData(false)
    val isReviewDelete: LiveData<Boolean> = _isReviewDelete

    private val _isFromDetail = MutableLiveData(false)
    val isFromDetail: LiveData<Boolean> = _isFromDetail

    private val _snackbarEvent = MutableLiveData<String?>()
    val snackbarEvent: LiveData<String?> = _snackbarEvent

    private var isRequesting = false

    val networkState get() = networkErrorDelegate.networkState

    fun getMyPlaceReview(size: Int = REVIEW_GET_SIZE, page: Int = INITIAL_PAGE_NO) = viewModelScope.launch {
        placeRepository.getMyPlaceReview(size, page).onSuccess {
            _myPlaceReview.value = it

            if (it.pageNo == it.totalPages) {
                _isLastPage.value = true
            }

            networkErrorDelegate.handleNetworkSuccess()
        }.onError {
            networkErrorDelegate.handleNetworkError(it)
        }
    }

    fun getNextMyPlaceReview(size: Int = REVIEW_GET_SIZE) = viewModelScope.launch {
        val page = _myPlaceReview.value?.pageNo ?:0

        if (_isLastPage.value == false && !isRequesting) {
            isRequesting = true

            networkErrorDelegate.handleNetworkLoading()

            placeRepository.getMyPlaceReview(size, page + 1).onSuccess { newReviews ->
                val currentReviews = _myPlaceReview.value?.myPlaceReviewInfoList ?: emptyList()
                val updatedReviews = currentReviews + newReviews.myPlaceReviewInfoList
                val updatedReviewData = newReviews.copy(myPlaceReviewInfoList = updatedReviews)
                _myPlaceReview.value = updatedReviewData

                if (newReviews.pageNo == newReviews.totalPages) {
                    _isLastPage.value = true
                }

                networkErrorDelegate.handleNetworkSuccess()
            }.onError {
                networkErrorDelegate.handleNetworkError(it)
            }.also {
                isRequesting = false
            }
        }
    }

    fun deleteMyPlaceReview(reviewId: Long) {
        viewModelScope.launch {
            placeRepository.deleteMyPlaceReview(reviewId).onSuccess {
                _myPlaceReview.value?.let { currentReviewData ->
                    val updatedReviews =
                        currentReviewData.myPlaceReviewInfoList.filter { it.reviewId != reviewId }
                    val updatedReviewData = currentReviewData.copy(
                        myPlaceReviewInfoList = updatedReviews,
                        reviewNum = currentReviewData.reviewNum - 1
                    )
                    _myPlaceReview.value = updatedReviewData
                    if(_isReviewDelete.value == false) _isReviewDelete.value = true
                }

                _snackbarEvent.postValue("후기가 삭제되었습니다.")
            }.onError {
                networkErrorDelegate.handleNetworkError(it)
            }
        }
    }

    fun updateMyPlaceReview(reviewId: Long, grade: Float, date: LocalDate, content: String) {
        val deleteImages = _deletedImages.value ?: listOf()
        val newImages = _newImages.value ?: listOf()
        val currentImages = _reviewImages.value ?: listOf()
        val updateImages = currentImages.plus(newImages)

        _reviewData.value = _reviewData.value?.copy(
            grade = grade,
            date = date,
            content = content,
            images = updateImages
        )

        viewModelScope.launch {
            placeRepository.updateMyPlaceReviewData(
                reviewId,
                UpdateMyPlaceReview(grade, date, content, deleteImages),
                MyPlaceReviewImages(newImages)
            ).onSuccess {
                _myPlaceReview.value?.let { currentReviewData ->
                    val updatedReviews = currentReviewData.myPlaceReviewInfoList.mapNotNull {
                        if (it.reviewId == reviewId) {
                            _reviewData.value
                        } else {
                            it
                        }
                    }
                    _myPlaceReview.value = currentReviewData.copy(myPlaceReviewInfoList = updatedReviews)

                    _snackbarEvent.postValue("후기가 수정되었습니다.")
                }

                networkErrorDelegate.handleNetworkSuccess()
            }.onError {
                networkErrorDelegate.handleNetworkError(it)
            }
        }
    }

    fun setReviewData(review: MyPlaceReviewInfo) {
        _reviewData.value = review
        _reviewImages.value = review.images.toMutableList()
        _newImages.value = listOf()
        _deletedImages.value = listOf()
        _visitDate.value = review.date

        updateNumOfImages()
    }

    fun setIsFromDetail(isModifyFromDetail: Boolean) {
        _isFromDetail.value = isModifyFromDetail
    }

    fun setDetailReviewData(review: ReviewInfo) {
        setReviewData(review.toMyPlaceReviewInfo())
    }

    fun setVisitDate(startDate: LocalDate) {
        _visitDate.value = startDate
    }

    fun setReviewImages(image: String) {
        val currentImages = _reviewImages.value?.toMutableList() ?: mutableListOf()
        currentImages.add(image)
        _reviewImages.value = currentImages

        updateNumOfImages()
    }

    fun deleteImage(position: Int) {
        val currentImages = _reviewImages.value?.toMutableList() ?: mutableListOf()
        val currentNewImages = _newImages.value?.toMutableList() ?: mutableListOf()

        if (position in currentImages.indices) {
            val deletedImage = currentImages.removeAt(position)
            _reviewImages.value = currentImages

            val deletedImageList = _deletedImages.value?.toMutableList() ?: mutableListOf()
            deletedImageList.add(deletedImage)
            _deletedImages.value = deletedImageList
        } else {
            val newPosition = position - currentImages.size
            if (newPosition in currentNewImages.indices) {
                currentNewImages.removeAt(newPosition)
                _newImages.value = currentNewImages
            }
        }

        updateNumOfImages()
    }


    fun addNewImage(image: String) {
        val currentNewImages = _newImages.value?.toMutableList() ?: mutableListOf()
        currentNewImages.add(image)
        _newImages.value = currentNewImages

        updateNumOfImages()
    }

    private fun updateNumOfImages() {
        val reviewImageCount = _reviewImages.value?.size ?: 0
        val newImageCount = _newImages.value?.size ?: 0
        _numOfImages.value = reviewImageCount + newImageCount
    }

    fun isMoreImageAttachable(): Boolean{
        val currentValue = _numOfImages.value ?: 0
        return currentValue in 0..3
    }

    fun resetSnackbarEvent() {
        _snackbarEvent.value = null
    }
}