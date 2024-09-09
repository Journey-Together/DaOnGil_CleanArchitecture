package kr.tekit.lion.presentation.main.vm.myinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.tekit.lion.domain.model.MyDefaultInfo
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.repository.AuthRepository
import kr.tekit.lion.domain.repository.MemberRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import kr.tekit.lion.presentation.delegate.NetworkState
import kr.tekit.lion.presentation.splash.model.LogInState
import javax.inject.Inject

@HiltViewModel
class MyInfoMainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val memberRepository: MemberRepository
): ViewModel() {

    init {
        viewModelScope.launch {
            checkLoginState()
        }
    }

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    val networkState: StateFlow<NetworkState> get() = networkErrorDelegate.networkState

    private val _loginState = MutableStateFlow<LogInState>(LogInState.Checking)
    val loginState = _loginState.asStateFlow()

    private val _myInfo = MutableSharedFlow<MyDefaultInfo>()
    val myInfo = _myInfo.asSharedFlow()

    private suspend fun checkLoginState(){
        authRepository.loggedIn.collect{ isLoggedIn ->
            if (isLoggedIn){
                _loginState.update { LogInState.LoggedIn }
            }
            else{
                _loginState.update { LogInState.LoginRequired }
            }
            networkErrorDelegate.handleNetworkSuccess()
        }
    }

    suspend fun onStateLoggedIn(){
        memberRepository.getMyDefaultInfo().onSuccess { myInfo ->
            viewModelScope.launch {
                _myInfo.emit(myInfo)
            }
            networkErrorDelegate.handleNetworkSuccess()
        }.onError {
            networkErrorDelegate.handleNetworkError(it)
        }
    }
}