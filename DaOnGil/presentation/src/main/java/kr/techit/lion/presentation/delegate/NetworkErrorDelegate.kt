package kr.techit.lion.presentation.delegate

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kr.techit.lion.domain.exception.AuthenticationError
import kr.techit.lion.domain.exception.AuthorizationError
import kr.techit.lion.domain.exception.BadRequestError
import kr.techit.lion.domain.exception.ConnectError
import kr.techit.lion.domain.exception.HttpError
import kr.techit.lion.domain.exception.NetworkError
import kr.techit.lion.domain.exception.NotFoundError
import kr.techit.lion.domain.exception.ServerError
import kr.techit.lion.domain.exception.TimeoutError
import kr.techit.lion.domain.exception.UnknownError
import kr.techit.lion.domain.exception.UnknownHostError
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class NetworkErrorDelegate @Inject constructor() {
    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Loading)
    val networkState: StateFlow<NetworkState> get() = _networkState.asStateFlow()

    fun handleUsecaseNetworkError(e: Throwable): NetworkError {
        return when (e) {
            is ConnectException -> ConnectError
            is SocketTimeoutException -> TimeoutError
            is UnknownHostException -> UnknownHostError
            is HttpException -> { // HttpException으로 변경
                when (e.code()) {
                    400 -> BadRequestError
                    401 -> AuthenticationError
                    403 -> AuthorizationError
                    404 -> NotFoundError
                    else -> ServerError
                }
            }
            else -> UnknownError
        }
    }

    fun handleNetworkError(exception: NetworkError) {
        val errorState = when (exception) {
            is ConnectError -> "${ConnectError.title} \n ${ConnectError.message}"
            is TimeoutError -> "${TimeoutError.title} \n ${TimeoutError.message}"
            is UnknownHostError -> "${UnknownError.title} \n ${UnknownHostError.message}"
            is HttpError -> when (exception) {
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