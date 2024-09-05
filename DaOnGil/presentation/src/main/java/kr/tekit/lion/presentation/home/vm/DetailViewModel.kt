package kr.tekit.lion.presentation.home.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.model.detailplace.PlaceDetailInfo
import kr.tekit.lion.domain.model.detailplace.PlaceDetailInfoGuest
import kr.tekit.lion.domain.repository.AuthRepository
import kr.tekit.lion.domain.repository.BookmarkRepository
import kr.tekit.lion.domain.repository.PlaceRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import kr.tekit.lion.presentation.splash.model.LogInState
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getPlaceDetailInfoRepository: PlaceRepository,
    private val updateBookmarkRepository: BookmarkRepository
) : ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    private val _loginState = MutableStateFlow<LogInState>(LogInState.Checking)
    val loginState = _loginState.asStateFlow()

    private val _detailPlaceInfo = MutableLiveData<PlaceDetailInfo>()
    val detailPlaceInfo : LiveData<PlaceDetailInfo> = _detailPlaceInfo

    private val _detailPlaceInfoGuest = MutableLiveData<PlaceDetailInfoGuest>()
    val detailPlaceInfoGuest : LiveData<PlaceDetailInfoGuest> = _detailPlaceInfoGuest

    init {
        viewModelScope.launch {
            checkLoginState()
        }
    }

    private suspend fun checkLoginState(){
        authRepository.loggedIn.collect{ isLoggedIn ->
            if (isLoggedIn) _loginState.value = LogInState.LoggedIn
            else _loginState.value = LogInState.LoginRequired
        }
    }

    fun getDetailPlace(placeId: Long) = viewModelScope.launch {
        getPlaceDetailInfoRepository.getPlaceDetailInfo(placeId).onSuccess {
            _detailPlaceInfo.value = it
        }.onError {
            networkErrorDelegate.handleNetworkError(it)
        }
    }

    fun getDetailPlaceGuest(placeId: Long) = viewModelScope.launch {
        getPlaceDetailInfoRepository.getPlaceDetailInfoGuest(placeId).onSuccess {
            _detailPlaceInfoGuest.value = it
        }.onError {
            networkErrorDelegate.handleNetworkError(it)
        }
    }

    fun updateDetailPlaceBookmark(placeId: Long) = viewModelScope.launch {
        updateBookmarkRepository.updatePlaceBookmark(placeId).onSuccess {
            Log.d("updateDetailPlaceBookmark", it.toString())
        }.onError {
            networkErrorDelegate.handleNetworkError(it)
        }
    }
}