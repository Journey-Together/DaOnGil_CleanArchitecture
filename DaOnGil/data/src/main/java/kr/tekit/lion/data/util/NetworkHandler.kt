package kr.tekit.lion.data.util

import kr.tekit.lion.domain.model.ConnectError
import kr.tekit.lion.domain.model.HttpError
import kr.tekit.lion.domain.model.NetworkError
import kr.tekit.lion.domain.model.Result
import kr.tekit.lion.domain.model.TimeoutError
import kr.tekit.lion.domain.model.UnknownError
import kr.tekit.lion.domain.model.UnknownHostError
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class NetworkHandler {
    protected inline fun <T> execute(block: () -> T): Result<T> = runCatching {
        Result.Success(block())
    }.getOrElse {
        it.printStackTrace()
        Result.Error(handleNetworkError(it))
    }

    fun handleNetworkError(e: Throwable): NetworkError {
        return when (e) {
            is ConnectException -> ConnectError
            is SocketTimeoutException -> TimeoutError
            is UnknownHostException -> UnknownHostError
            is HttpException -> HttpError(e.code())
            else -> UnknownError
        }
    }
}