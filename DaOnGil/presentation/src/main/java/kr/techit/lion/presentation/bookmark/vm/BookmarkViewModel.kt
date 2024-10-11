package kr.techit.lion.presentation.bookmark.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.domain.model.PlaceBookmark
import kr.techit.lion.domain.model.PlanBookmark
import kr.techit.lion.domain.repository.BookmarkRepository
import kr.techit.lion.presentation.delegate.NetworkErrorDelegate
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

    private val _isUpdateError = MutableLiveData(false)
    val isUpdateError: LiveData<Boolean> = _isUpdateError

    private val _snackbarEvent = MutableLiveData<String?>()
    val snackbarEvent: LiveData<String?> = _snackbarEvent

    val networkState get() = networkErrorDelegate.networkState

    init {
        getPlaceBookmark()
        getPlanBookmark()
    }

    fun getPlaceBookmark() = viewModelScope.launch {
        bookmarkRepository.getPlaceBookmark().onSuccess {
            _placeBookmarkList.value = it
            networkErrorDelegate.handleNetworkSuccess()
        }.onError {
            networkErrorDelegate.handleNetworkError(it)
        }
    }

    fun getPlanBookmark() = viewModelScope.launch {
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
            _placeBookmarkList.postValue(currentList)
        }

        bookmarkRepository.updatePlaceBookmark(placeId).onSuccess {
            _snackbarEvent.postValue("북마크가 삭제되었습니다.")
        }.onError {
            _isUpdateError.postValue(true)

            itemToRestore?.let { placeBookmark ->
                currentList.add(itemIndex, placeBookmark)
                _placeBookmarkList.postValue(currentList)
            }

            networkErrorDelegate.handleNetworkError(it)
        }
    }

    fun updatePlanBookmark(planId: Long) = viewModelScope.launch(Dispatchers.IO) {
        val currentList = _planBookmarkList.value.orEmpty().toMutableList()
        val itemIndex = currentList.indexOfFirst { it.planId == planId }

        val itemToRestore = if (itemIndex != -1) currentList[itemIndex] else null

        if (itemIndex != -1) {
            currentList.removeAt(itemIndex)
            _planBookmarkList.postValue(currentList)
        }

        bookmarkRepository.updatePlanBookmark(planId).onSuccess {
            _snackbarEvent.postValue("북마크가 삭제되었습니다.")
        }.onError {
            _isUpdateError.postValue(true)
            itemToRestore?.let { planBookmark ->
                currentList.add(itemIndex, planBookmark)
                _planBookmarkList.postValue(currentList)
            }

            networkErrorDelegate.handleNetworkError(it)
        }
    }

    fun resetSnackbarEvent() {
        _snackbarEvent.value = null
    }
}