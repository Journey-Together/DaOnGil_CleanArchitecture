package kr.tekit.lion.data.datasource.base

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

open class BaseDataSource {
    protected inline fun <T> execute(block: () -> T): Result<T> = runCatching {
        Result.Success(block())
    }.getOrElse {
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