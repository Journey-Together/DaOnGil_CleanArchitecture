package kr.techit.lion.presentation.onboarding.vm

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kr.techit.lion.presentation.onboarding.FocusOn
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
): ViewModel() {
    private val _focusOn = MutableStateFlow(FocusOn.ViewPager)
    val focusOn = _focusOn.asStateFlow()

    fun setFocusOn(focusOn: FocusOn) {
        _focusOn.value = focusOn
    }
}
