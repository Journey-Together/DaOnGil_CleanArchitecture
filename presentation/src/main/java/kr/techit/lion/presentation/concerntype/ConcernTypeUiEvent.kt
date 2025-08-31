package kr.techit.lion.presentation.concerntype

sealed interface ConcernTypeUiEvent {
    data object NavigateToBack : ConcernTypeUiEvent
}
