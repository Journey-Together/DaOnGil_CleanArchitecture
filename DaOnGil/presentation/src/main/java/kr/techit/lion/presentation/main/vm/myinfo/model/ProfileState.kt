package kr.techit.lion.presentation.main.vm.myinfo.model

import kr.techit.lion.domain.model.MyDefaultInfo
import kr.techit.lion.presentation.splash.model.LogInState

data class ProfileState(
    val loginState: LogInState,
    val myInfo: MyDefaultInfo
){
    companion object{
        fun create(): ProfileState{
            return ProfileState(
                loginState = LogInState.Checking,
                myInfo = MyDefaultInfo()
            )
        }
    }
}