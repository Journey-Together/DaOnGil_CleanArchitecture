package kr.tekit.lion.presentation.main.vm.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.model.MyMainSchedule
import kr.tekit.lion.domain.repository.AuthRepository
import kr.tekit.lion.domain.repository.PlanRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import kr.tekit.lion.presentation.splash.model.LogInState
import javax.inject.Inject

@HiltViewModel
class ScheduleMainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val planRepository: PlanRepository
) : ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    private val _myMainPlanList = MutableLiveData<List<MyMainSchedule?>?>()
    val myMainPlanList : LiveData<List<MyMainSchedule?>?> = _myMainPlanList

    private val _loginState = MutableStateFlow<LogInState>(LogInState.Checking)
    val loginState = _loginState.asStateFlow()

    init {
        viewModelScope.launch {
            checkLoginState()
        }
    }

    fun getMyMainPlanList() =
        viewModelScope.launch {
            planRepository.getMyMainSchedule().onSuccess {
                _myMainPlanList.value = it
            }.onError {
                networkErrorDelegate.handleNetworkError(it)
            }
        }

    private fun checkLoginState() =
        viewModelScope.launch {
            authRepository.loggedIn.collect { isLoggedIn ->
                if (isLoggedIn) _loginState.value = LogInState.LoggedIn
                else _loginState.value = LogInState.LoginRequired
            }
        }
}