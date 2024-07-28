package kr.tekit.lion.presentation.delegate

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kr.tekit.lion.domain.model.ConnectError
import kr.tekit.lion.domain.model.HttpError
import kr.tekit.lion.domain.model.NetworkError
import kr.tekit.lion.domain.model.TimeoutError
import kr.tekit.lion.domain.model.UnknownError
import kr.tekit.lion.domain.model.UnknownHostError
import javax.inject.Inject

class ViewModelDelegate @Inject constructor() {
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage.asStateFlow()

    fun handleNetworkError(exception: NetworkError) {
        val errorState: String = when (exception) {
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