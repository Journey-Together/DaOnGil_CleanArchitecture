package kr.tekit.lion.presentation.home.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.model.placereviewlist.PlaceReviewInfo
import kr.tekit.lion.domain.repository.PlaceRepository
import javax.inject.Inject

@HiltViewModel
class ReviewListViewModel @Inject constructor(
    private val getPlaceReviewListRepository: PlaceRepository
) : ViewModel() {
    private val _placeReviewInfo = MutableLiveData<PlaceReviewInfo>()
    val placeReviewInfo : LiveData<PlaceReviewInfo> = _placeReviewInfo

    private val _isLastPage = MutableLiveData<Boolean>()
    val isLastPage : LiveData<Boolean> = _isLastPage

    companion object {
        const val PAGE_SIZE = 5
    }

    init {
        _isLastPage.value = false
    }

    fun getPlaceReview(placeId: Long) = viewModelScope.launch {
        getPlaceReviewListRepository.getPlaceReviewList(placeId, PAGE_SIZE, 0).onSuccess {
            Log.d("getPlaceReview", it.toString())
            _placeReviewInfo.value = it
        }.onError {
            Log.d("getPlaceReview", it.toString())
        }
    }

    fun getNewPlaceReview(placeId: Long) = viewModelScope.launch {
        val page = _placeReviewInfo.value?.pageNo

        if (page != null) {
            getPlaceReviewListRepository.getPlaceReviewList(placeId, size = PAGE_SIZE, page = page + 1).onSuccess {
                if (it.pageNo == it.totalPages) {
                    _isLastPage.value = true
                } else {
                    val reviews = _placeReviewInfo.value?.placeReviewList ?: emptyList()
                    val newReviews = reviews + it.placeReviewList
                    val newReviewData = it.copy(placeReviewList = newReviews)

                    _placeReviewInfo.value = newReviewData
                }
            }.onError {
                Log.d("getNewPlaceReview", it.toString())
            }
        }
    }
}