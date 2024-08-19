package kr.tekit.lion.presentation.main.vm.myinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.model.MyDefaultInfo
import kr.tekit.lion.domain.model.onError
import kr.tekit.lion.domain.model.onSuccess
import kr.tekit.lion.domain.repository.AuthRepository
import kr.tekit.lion.domain.repository.MemberRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import kr.tekit.lion.presentation.splash.model.LogInState
import javax.inject.Inject

@HiltViewModel
class MyInfoMainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val memberRepository: MemberRepository
): ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    val errorMessage: StateFlow<String?> get() = networkErrorDelegate.errorMessage

    private val _loginState = MutableStateFlow<LogInState>(LogInState.Checking)
    val loginState = _loginState.asStateFlow()

    private val _myInfo = MutableStateFlow(MyDefaultInfo())
    val myInfo = _myInfo.asStateFlow()

    init {
        viewModelScope.launch {
            checkLoginState()
        }
    }

    private suspend fun checkLoginState(){
        authRepository.loggedIn.collect{ isLoggedIn ->
            if (isLoggedIn) _loginState.value = LogInState.LoggedIn
            else _loginState.value = LogInState.LoginRequired
        }
    }

    fun onStateLoggedIn() = viewModelScope.launch(Dispatchers.IO){
        memberRepository.getMyDefaultInfo().onSuccess {
            _myInfo.value = it
        }.onError {
            _loginState.value = LogInState.LoginRequired
            networkErrorDelegate.handleNetworkError(it)
        }
    }
}