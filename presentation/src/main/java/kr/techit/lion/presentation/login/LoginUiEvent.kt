package kr.techit.lion.presentation.login

sealed interface LoginUiEvent {
    data object NavigateToMain: LoginUiEvent
    data object NavigateToSelectConcern: LoginUiEvent
}
