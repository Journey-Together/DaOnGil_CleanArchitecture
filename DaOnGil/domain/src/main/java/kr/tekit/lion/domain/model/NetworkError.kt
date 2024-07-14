package kr.tekit.lion.domain.model

sealed class NetworkError : Throwable()

data object ConnectError : NetworkError() {
    override val message: String
        get() = "서버에 연결할 수 없습니다. 인터넷 연결을 확인한 후 다시 시도해주세요. 문제가 지속되면 고객 지원에 문의하세요."
}

data object TimeoutError : NetworkError() {
    override val message: String
        get() = "서버 응답 시간이 초과되었습니다. 잠시 후 다시 시도해주세요. 문제가 지속되면 고객 지원에 문의하세요."
}

data object UnknownHostError : NetworkError() {
    override val message: String
        get() = "서버를 찾을 수 없습니다. 인터넷 연결 상태를 확인해주세요. 문제가 지속되면 고객 지원에 문의하세요."
}

data class HttpError(val code: Int) : NetworkError() {
    override val message: String
        get() = when (code) {
            400 -> "잘못된 요청입니다. 요청 내용을 확인하고 다시 시도해주세요. 계속 문제가 발생하면 고객 지원에 문의하세요."
            401 -> "인증이 필요합니다. 로그인 후 다시 시도해주세요. 문제가 지속되면 고객 지원에 문의하세요."
            403 -> "접근이 금지되었습니다. 권한을 확인하고 다시 시도해주세요. 문제가 지속되면 고객 지원에 문의하세요."
            404 -> "요청한 리소스를 찾을 수 없습니다. URL을 확인하고 다시 시도해주세요. 문제가 지속되면 고객 지원에 문의하세요."
            else -> "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요. 문제가 지속되면 고객 지원에 문의하세요."
        }
}

data object UnknownError : NetworkError() {
    override val message: String
        get() = "알 수 없는 오류가 발생했습니다. 고객 지원에 문의해주세요."
}
