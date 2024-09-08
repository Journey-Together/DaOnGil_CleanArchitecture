package kr.tekit.lion.presentation.login.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.repository.ActivationRepository
import kr.tekit.lion.presentation.login.model.FocusOn
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val activationRepository: ActivationRepository
): ViewModel() {
    private val _focusOn = MutableStateFlow(FocusOn.ViewPager)
    val focusOn = _focusOn.asStateFlow()

    fun setFocusOn(focusOn: FocusOn) {
        _focusOn.value = focusOn
    }

    fun saveUserActivation() = viewModelScope.launch{
        activationRepository.saveUserActivation(true)
    }
}