package kr.tekit.lion.presentation.main.vm.search

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kr.tekit.lion.presentation.main.model.ScreenState

class SearchViewModel : ViewModel() {
    private val _screenState = MutableStateFlow(ScreenState.List)
    val screenState get() = _screenState.asStateFlow()

    private val _isFirstScreen = MutableStateFlow(0)
    val firstScreen get() = _isFirstScreen.asStateFlow()

    fun changeScreenState(state: ScreenState) {
        _screenState.value = state
        _isFirstScreen.value++
    }
}