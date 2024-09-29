package kr.tekit.lion.presentation.myinfo.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.repository.AuthRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import javax.inject.Inject

@HiltViewModel
class DeleteUserViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    val networkState get() = networkErrorDelegate.networkState

    fun withdrawal(onSuccess: () -> Unit) = viewModelScope.launch{
        authRepository.withdraw().onSuccess {
            onSuccess()
        }.onError { e ->
            networkErrorDelegate.handleNetworkError(e)
        }
    }
}