package kr.tekit.lion.presentation.main.vm.search

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kr.tekit.lion.presentation.main.model.ScreenState

class SearchViewModel : ViewModel() {
    private val _screenState = MutableStateFlow(ScreenState.List)
    val screenState get() = _screenState.asStateFlow()

    fun changeScreenState(state: ScreenState) {
        _screenState.value = state
    }
}