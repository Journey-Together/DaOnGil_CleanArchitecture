package kr.tekit.lion.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.usecase.areacode.InitAreaCodeInfoUseCase
import kr.tekit.lion.domain.usecase.base.onError
import kr.tekit.lion.domain.usecase.base.onSuccess
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val initAreaCodeInfoUseCase: InitAreaCodeInfoUseCase
) : ViewModel() {
    init {
        viewModelScope.launch {
            initAreaCodeInfoUseCase().onSuccess {
                Log.d("Dasds", it.toString())
            }.onError {
                Log.d("Dasds", it.toString())
            }
        }
    }
}