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
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage.asStateFlow()

    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Loading)
    val networkState: StateFlow<NetworkState> get() = _networkState.asStateFlow()

    fun handleNetworkError(exception: NetworkError) {
        val errorState = when (exception) {
            is ConnectError -> ConnectError.message
            is TimeoutError -> TimeoutError.message
            is UnknownHostError -> UnknownHostError.message
            is HttpException -> when (exception) {
                is BadRequestError -> BadRequestError.message
                is AuthenticationError -> AuthenticationError.message
                is AuthorizationError -> AuthorizationError.message
                is NotFoundError -> NotFoundError.message
                is ServerError -> ServerError.message
            }
            is UnknownError -> UnknownError.message
        }
        _errorMessage.value = errorState
    }

    fun handleNetworkSuccess(){
        _networkState.value = NetworkState.Success
    }
}

sealed class NetworkState(){
    data object Loading: NetworkState()
    data object Success: NetworkState()
}