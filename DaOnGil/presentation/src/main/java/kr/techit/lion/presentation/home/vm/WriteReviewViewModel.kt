package kr.techit.lion.presentation.home.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.techit.lion.domain.model.placereview.NewReviewData
import kr.techit.lion.domain.repository.PlaceRepository
import kr.techit.lion.presentation.delegate.NetworkErrorDelegate
import java.time.LocalDate
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.domain.model.placereview.NewReviewImages
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.home.model.NewReviewImgs
import javax.inject.Inject

@HiltViewModel
class WriteReviewViewModel @Inject constructor(
    private val writePlaceReviewRepository: PlaceRepository
) : ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate
    val networkState: StateFlow<NetworkState> get() = networkErrorDelegate.networkState

    private val _placeVisitDate = MutableLiveData<LocalDate>()
    val placeVisitDate: LiveData<LocalDate> = _placeVisitDate

    private val _newReviewData = MutableLiveData<NewReviewData>()
    val newReviewData: LiveData<NewReviewData> = _newReviewData

    private val _newReviewImages = MutableLiveData<NewReviewImgs>()
    val newReviewImages: LiveData<NewReviewImgs> = _newReviewImages

    private val _numOfImages = MutableLiveData<Int>()
    val numOfImages: LiveData<Int> = _numOfImages

    init {
        _numOfImages.value = 0
    }

    fun setPlaceVisitDate(startDate: LocalDate) {
        _placeVisitDate.value = startDate
    }

    fun writePlaceReviewData(placeId: Long, date: LocalDate, grade: Float, content: String) {

        _newReviewData.value = newReviewData.value?.copy(
            date = date,
            grade = grade,
            content = content
        )

        viewModelScope.launch {
            val images: NewReviewImages =
                newReviewImages.value?.toDomainModel() ?: NewReviewImages(emptyList())
            writePlaceReviewRepository.writePlaceReviewData(
                placeId,
                NewReviewData(date, grade, content),
                images
            ).onSuccess {
                networkErrorDelegate.handleNetworkSuccess()
            }.onError {
                networkErrorDelegate.handleNetworkError(it)
            }
        }
    }

    fun setReviewImages(image: String) {
        val currentImages = _newReviewImages.value?.images?.toMutableList() ?: mutableListOf()
        currentImages.add(image)
        _newReviewImages.value = NewReviewImgs(currentImages)
        updateNumOfImages()
    }

    fun deleteImage(position: Int) {
        val currentImages = _newReviewImages.value?.images?.toMutableList() ?: mutableListOf()
        if (position in currentImages.indices) {
            currentImages.removeAt(position)
            _newReviewImages.value = NewReviewImgs(currentImages)
        }
        updateNumOfImages()
    }

    private fun updateNumOfImages() {
        val imgCount = _newReviewImages.value?.images?.size ?: 0
        _numOfImages.value = imgCount
    }
}