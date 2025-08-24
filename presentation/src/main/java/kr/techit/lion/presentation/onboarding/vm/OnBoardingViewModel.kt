package kr.techit.lion.presentation.onboarding.vm

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kr.techit.lion.presentation.onboarding.model.FocusOn
import kr.techit.lion.presentation.onboarding.model.OnBoardingUiState
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnBoardingUiState())
    val uiState: StateFlow<OnBoardingUiState> get() = _uiState.asStateFlow()

    fun setFocusOn(focusOn: FocusOn) {
        _uiState.value = _uiState.value.copy(focusOn = focusOn)
    }

    fun setCurrentPage(position: Int) {
        _uiState.value = _uiState.value.copy(currentPage = position)
    }
}
