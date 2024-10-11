package kr.techit.lion.presentation.login.vm

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kr.techit.lion.presentation.login.model.FocusOn
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