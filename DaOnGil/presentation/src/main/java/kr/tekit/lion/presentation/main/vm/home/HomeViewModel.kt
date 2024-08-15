package kr.tekit.lion.presentation.main.vm.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.model.AppTheme
import kr.tekit.lion.domain.repository.AppThemeRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appThemeRepository: AppThemeRepository
): ViewModel() {

    init {
        viewModelScope.launch {
            _appTheme.value = appThemeRepository.getAppTheme()
        }
    }

    private val _appTheme = MutableStateFlow(AppTheme.LIGHT)
    val appTheme = _appTheme.asStateFlow()

    private fun setAppTheme(appTheme: AppTheme) {
        viewModelScope.launch {
            appThemeRepository.saveAppTheme(appTheme)
            _appTheme.update { appTheme }
        }
    }

    // 상단 테마 토글 버튼 클릭시
    fun onClickThemeToggleButton() {
        val newAppTheme = if (_appTheme.value == AppTheme.LIGHT) AppTheme.DARK else AppTheme.LIGHT
        setAppTheme(newAppTheme)
    }

    // 테마 설정 다이얼로그 클릭시
    fun onClickThemeChangeButton() {
        setAppTheme(AppTheme.DARK)
    }
}