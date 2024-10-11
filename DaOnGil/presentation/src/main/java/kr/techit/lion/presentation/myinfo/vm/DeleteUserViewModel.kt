package kr.techit.lion.presentation.myinfo.vm

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.domain.repository.AuthRepository
import kr.techit.lion.presentation.base.BaseViewModel
import kr.techit.lion.presentation.delegate.NetworkErrorDelegate
import javax.inject.Inject

@HiltViewModel
class DeleteUserViewModel @Inject constructor(
    private val authRepository: AuthRepository
): BaseViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    val networkState get() = networkErrorDelegate.networkState

    fun withdrawal(onSuccess: () -> Unit) = viewModelScope.launch(recordExceptionHandler){
        authRepository.withdraw().onSuccess {
            onSuccess()
        }.onError { e ->
            networkErrorDelegate.handleNetworkError(e)
        }
    }
}