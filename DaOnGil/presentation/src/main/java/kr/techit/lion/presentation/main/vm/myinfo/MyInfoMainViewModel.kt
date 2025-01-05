package kr.techit.lion.presentation.main.vm.myinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.domain.model.MyDefaultInfo
import kr.techit.lion.domain.repository.AuthRepository
import kr.techit.lion.domain.repository.MemberRepository
import kr.techit.lion.presentation.delegate.NetworkErrorDelegate
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.main.vm.myinfo.model.ProfileState
import kr.techit.lion.presentation.splash.model.LogInState
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

    private val _state = MutableStateFlow(ProfileState.create())
    val state get() = _state.asStateFlow()

    private suspend fun checkLoginState(){
        authRepository.loggedIn.collect{ isLoggedIn ->
            if (isLoggedIn){
                _state.value = _state.value.copy(loginState = LogInState.LoggedIn)
            }
            else{
                _state.value = _state.value.copy(loginState = LogInState.LoginRequired)
                networkErrorDelegate.handleNetworkSuccess()
            }
        }
    }

    suspend fun onStateLoggedIn(){
        memberRepository.getMyDefaultInfo().onSuccess { profile ->
            _state.value = _state.value.copy(myInfo = profile)
            networkErrorDelegate.handleNetworkSuccess()
        }.onError {
            networkErrorDelegate.handleNetworkError(it)
        }
    }

    fun logout(onSuccess: () -> Unit){
        viewModelScope.launch{
            authRepository.logout()
            onSuccess()
        }
    }
}