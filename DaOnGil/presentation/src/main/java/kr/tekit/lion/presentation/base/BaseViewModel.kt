package kr.tekit.lion.presentation.base
import android.util.Log
import androidx.lifecycle.ViewModel
import kr.tekit.lion.domain.model.ConnectError
import kr.tekit.lion.domain.model.HttpError
import kr.tekit.lion.domain.model.NetworkError
import kr.tekit.lion.domain.model.TimeoutError
import kr.tekit.lion.domain.model.UnknownError
import kr.tekit.lion.domain.model.UnknownHostError

abstract class BaseViewModel : ViewModel() {
    protected fun handleNetworkError(exception: NetworkError) {
        when (exception) {
            is ConnectError -> {
                Log.e("SearchMainViewModel", exception.message)
            }
            is TimeoutError -> {
                Log.e("SearchMainViewModel", exception.message)
            }
            is UnknownHostError -> {
                Log.e("SearchMainViewModel", exception.message)
            }
            is HttpError -> {
                Log.e("SearchMainViewModel", "HTTP Error ${exception.code}: ${exception.message}")
            }
            is UnknownError -> {
                Log.e("SearchMainViewModel", exception.message)
            }
        }
    }
}
