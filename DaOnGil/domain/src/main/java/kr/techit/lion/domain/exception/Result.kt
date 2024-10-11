package kr.techit.lion.domain.exception

sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Error<T>(val error: NetworkError) : Result<T>()
}

fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    return when (this) {
        is Result.Success<T> ->{
            action(value)
            this
        }
        is Result.Error<T> -> {
            this
        }
    }
}

fun <T> Result<T>.onError(action: (NetworkError) -> Unit): Result<T> {
    return when (this) {
        is Result.Success<T> ->{
            this
        }
        is Result.Error<T> -> {
            action(error)
            this
        }
    }
}