package kr.tekit.lion.presentation.bookmark.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.model.PlaceBookmark
import kr.tekit.lion.domain.model.PlanBookmark
import kr.tekit.lion.domain.repository.BookmarkRepository
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
): ViewModel() {

    private val _placeBookmarkList = MutableLiveData<List<PlaceBookmark>>()
    val placeBookmarkList: LiveData<List<PlaceBookmark>> = _placeBookmarkList

    private val _planBookmarkList = MutableLiveData<List<PlanBookmark>>()
    val planBookmarkList: LiveData<List<PlanBookmark>> = _planBookmarkList

    init {
        getPlaceBookmark()
        getPlanBookmark()
    }

    private fun getPlaceBookmark() = viewModelScope.launch {
        bookmarkRepository.getPlaceBookmark().onSuccess {
            _placeBookmarkList.value = it
        }.onError {
            Log.d("getPlaceBookmark", it.toString())
        }
    }

    private fun getPlanBookmark() = viewModelScope.launch {
        bookmarkRepository.getPlanBookmark().onSuccess {
                _planBookmarkList.value = it
        }.onError {
            Log.d("getPlanBookmark", it.toString())
        }
    }

    fun updatePlaceBookmark(placeId: Long) = viewModelScope.launch {
        bookmarkRepository.updatePlaceBookmark(placeId).onSuccess {
            val updatedList = _placeBookmarkList.value.orEmpty().toMutableList()
            val index = updatedList.indexOfFirst { it.placeId == placeId }
            if (index != -1) {
                updatedList.removeAt(index)
                _placeBookmarkList.postValue(updatedList)
            }
        }.onError {
            Log.d("updatePlaceBookmark", it.toString())
        }
    }

    fun updatePlanBookmark(planId: Long) = viewModelScope.launch {
        bookmarkRepository.updatePlanBookmark(planId).onSuccess {
            val updatedList = _planBookmarkList.value.orEmpty().toMutableList()
            val index = updatedList.indexOfFirst { it.planId == planId }
            if (index != -1) {
                updatedList.removeAt(index)
                _planBookmarkList.postValue(updatedList)
            }
        }.onError {
            Log.d("updatePlanBookmark", it.toString())
        }
    }
}