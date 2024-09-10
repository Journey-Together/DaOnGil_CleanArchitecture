package kr.tekit.lion.presentation.bookmark.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.model.PlaceBookmark
import kr.tekit.lion.domain.model.PlanBookmark
import kr.tekit.lion.domain.repository.BookmarkRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
): ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    private val _placeBookmarkList = MutableLiveData<List<PlaceBookmark>>()
    val placeBookmarkList: LiveData<List<PlaceBookmark>> = _placeBookmarkList

    private val _planBookmarkList = MutableLiveData<List<PlanBookmark>>()
    val planBookmarkList: LiveData<List<PlanBookmark>> = _planBookmarkList

    val networkState get() = networkErrorDelegate.networkState

    init {
        getPlaceBookmark()
        getPlanBookmark()
    }

    private fun getPlaceBookmark() = viewModelScope.launch {
        bookmarkRepository.getPlaceBookmark().onSuccess {
            _placeBookmarkList.value = it
            networkErrorDelegate.handleNetworkSuccess()
        }.onError {
            networkErrorDelegate.handleNetworkError(it)
        }
    }

    private fun getPlanBookmark() = viewModelScope.launch {
        bookmarkRepository.getPlanBookmark().onSuccess {
            _planBookmarkList.value = it
            networkErrorDelegate.handleNetworkSuccess()
        }.onError {
            networkErrorDelegate.handleNetworkError(it)
        }
    }

    fun updatePlaceBookmark(placeId: Long) = viewModelScope.launch(Dispatchers.IO) {
        val currentList = _placeBookmarkList.value.orEmpty().toMutableList()
        val itemIndex = currentList.indexOfFirst { it.placeId == placeId }

        val itemToRestore = if (itemIndex != -1) currentList[itemIndex] else null

        if (itemIndex != -1) {
            currentList.removeAt(itemIndex)
            _placeBookmarkList.value = currentList
        }

        bookmarkRepository.updatePlaceBookmark(placeId).onError {
            itemToRestore?.let { placeBookmark ->
                currentList.add(itemIndex, placeBookmark)
                _placeBookmarkList.postValue(currentList)
            }
        }
    }

    fun updatePlanBookmark(planId: Long) = viewModelScope.launch(Dispatchers.IO) {
        val currentList = _planBookmarkList.value.orEmpty().toMutableList()
        val itemIndex = currentList.indexOfFirst { it.planId == planId }

        val itemToRestore = if (itemIndex != -1) currentList[itemIndex] else null

        if (itemIndex != -1) {
            currentList.removeAt(itemIndex)
            _planBookmarkList.value = currentList
        }

        bookmarkRepository.updatePlanBookmark(planId).onError {
            itemToRestore?.let { planBookmark ->
                currentList.add(itemIndex, planBookmark)
                _planBookmarkList.postValue(currentList)
            }
        }
    }
}