package kr.tekit.lion.presentation.splash.model

sealed class LogInState {
    data object Checking : LogInState()
    data object LoggedIn : LogInState()
    data object LoginRequired : LogInState()
}