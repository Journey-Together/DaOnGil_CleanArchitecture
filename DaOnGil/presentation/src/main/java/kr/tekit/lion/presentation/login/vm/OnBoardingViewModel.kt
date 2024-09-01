package kr.tekit.lion.presentation.login.vm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kr.tekit.lion.presentation.login.model.FocusOn

class OnBoardingViewModel: ViewModel() {
    private val _focusOn = MutableStateFlow(FocusOn.ViewPager)
    val focusOn = _focusOn.asStateFlow()

    fun setFocusOn(focusOn: FocusOn) {
        _focusOn.value = focusOn
    }
}