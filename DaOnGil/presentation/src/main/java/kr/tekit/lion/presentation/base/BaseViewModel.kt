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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage get() = _errorMessage.asStateFlow()

    protected open fun handleNetworkError(exception: NetworkError) {
        val errorState: String? = when (exception) {
            is ConnectError -> {
                ConnectError.message
            }
            is TimeoutError -> {
                TimeoutError.message
            }
            is UnknownHostError -> {
                UnknownHostError.message
            }
            is HttpError -> {
                HttpError(exception.code).message
            }
            is UnknownError -> {
                UnknownError.message
            }
        }
        _errorMessage.value = errorState
    }
}
