package kr.tekit.lion.presentation.main.vm.search

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kr.tekit.lion.domain.model.search.ListSearchOption
import kr.tekit.lion.domain.repository.PlaceRepository
import javax.inject.Inject

@HiltViewModel
class KeywordSearchViewModel @Inject constructor(
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val _keyword = MutableStateFlow("")
    val keyword = _keyword.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val autocompleteKeyword = keyword
        .debounce(DEBOUNCE_INTERVAL)
        .flatMapLatest { keyword ->
            placeRepository.getAutoCompleteKeyword(keyword)
        }
        .flowOn(Dispatchers.IO)
        .catch { e: Throwable ->
            e.printStackTrace()
        }

    fun updateKeyword(keyword: String) {
        _keyword.value = keyword
    }

    fun onClickSearchButton(keyword: String){
    }

    companion object {
        private const val DEBOUNCE_INTERVAL = 800L
    }
}