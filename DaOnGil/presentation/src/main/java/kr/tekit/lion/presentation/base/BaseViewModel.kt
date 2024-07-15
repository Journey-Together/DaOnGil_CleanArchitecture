package kr.tekit.lion.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kr.tekit.lion.domain.model.ConnectError
import kr.tekit.lion.domain.model.HttpError
import kr.tekit.lion.domain.model.NetworkError
import kr.tekit.lion.domain.model.TimeoutError
import kr.tekit.lion.domain.model.UnknownError
import kr.tekit.lion.domain.model.UnknownHostError

open class BaseViewModel : ViewModel() {

    private val _networkState = MutableStateFlow<NetworkError?>(null)
    val networkState get() = _networkState.asStateFlow()

    protected open fun handleNetworkError(exception: NetworkError) {
        val errorState: NetworkError = when (exception) {
            is ConnectError -> {
                ConnectError
            }
            is TimeoutError -> {
                TimeoutError
            }
            is UnknownHostError -> {
                UnknownHostError
            }
            is HttpError -> {
                HttpError(exception.code)
                //Log.e("SearchMainViewModel", "HTTP Error ${exception.code}: ${exception.message}")
            }
            is UnknownError -> {
                UnknownError
            }
        }
        _networkState.value = errorState
    }
}
