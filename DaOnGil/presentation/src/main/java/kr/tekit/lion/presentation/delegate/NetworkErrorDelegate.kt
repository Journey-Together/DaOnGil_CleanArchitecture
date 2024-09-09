package kr.tekit.lion.presentation.delegate

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kr.tekit.lion.domain.exception.AuthenticationError
import kr.tekit.lion.domain.exception.AuthorizationError
import kr.tekit.lion.domain.exception.BadRequestError
import kr.tekit.lion.domain.exception.ConnectError
import kr.tekit.lion.domain.exception.HttpException
import kr.tekit.lion.domain.exception.NetworkError
import kr.tekit.lion.domain.exception.NotFoundError
import kr.tekit.lion.domain.exception.ServerError
import kr.tekit.lion.domain.exception.TimeoutError
import kr.tekit.lion.domain.exception.UnknownError
import kr.tekit.lion.domain.exception.UnknownHostError
import javax.inject.Inject

class NetworkErrorDelegate @Inject constructor() {
    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Loading)
    val networkState: StateFlow<NetworkState> get() = _networkState.asStateFlow()

    fun handleNetworkError(exception: NetworkError) {
        val errorState = when (exception) {
            is ConnectError -> "${ConnectError.title} \n ${ConnectError.message}"
            is TimeoutError -> "${TimeoutError.title} \n ${TimeoutError.message}"
            is UnknownHostError -> "${UnknownError.title} \n ${UnknownHostError.message}"
            is HttpException -> when (exception) {
                is BadRequestError -> "${BadRequestError.title} \n ${BadRequestError.message}"
                is AuthenticationError -> "${AuthenticationError.title} \n ${AuthenticationError.message}"
                is AuthorizationError -> "${AuthorizationError.title} \n ${AuthorizationError.message}"
                is NotFoundError -> "${NotFoundError.title} \n ${NotFoundError.message}"
                is ServerError -> "${ServerError.title} \n ${ServerError.message}"
            }
            is UnknownError -> "${UnknownError.title} \n ${UnknownError.message}"
        }
        _networkState.value = NetworkState.Error(errorState)
    }

    fun handleNetworkSuccess(){
        _networkState.value = NetworkState.Success
    }

    fun handleNetworkLoading(){
        _networkState.value = NetworkState.Loading
    }
}

sealed class NetworkState{
    data object Loading: NetworkState()
    data object Success: NetworkState()
    data class Error(val msg: String): NetworkState()
}