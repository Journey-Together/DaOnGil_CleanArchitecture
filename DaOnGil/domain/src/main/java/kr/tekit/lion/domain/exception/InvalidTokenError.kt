package kr.tekit.lion.domain.exception

data class InvalidTokenError(
    val msg: String
): Throwable()