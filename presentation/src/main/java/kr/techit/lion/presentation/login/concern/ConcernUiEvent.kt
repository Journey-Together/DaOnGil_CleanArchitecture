package kr.techit.lion.presentation.login.concern

sealed interface ConcernUiEvent {
    data object NavigateToMain : ConcernUiEvent
}
