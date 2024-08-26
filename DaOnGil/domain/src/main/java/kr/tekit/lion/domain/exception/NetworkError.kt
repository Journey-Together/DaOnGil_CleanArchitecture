package kr.tekit.lion.domain.exception

sealed class NetworkError : Throwable(){
    abstract val title: String
    abstract override val message: String
}

data object ConnectError : NetworkError() {
    override val title: String
        get() = "서버에 연결할 수 없어요"
    override val message: String
        get() = "인터넷 연결을 확인한 후 다시 시도해주세요."
}

data object TimeoutError : NetworkError() {
    override val title: String
        get() = "서버 응답 시간이 초과되었어요"
    override val message: String
        get() = "잠시 후 다시 시도해주세요."
}

data object UnknownHostError : NetworkError() {
    override val title: String
        get() = "서버를 찾을 수 없습니다."
    override val message: String
        get() = "인터넷 연결 상태를 확인해주세요."
}

data object UnknownError : NetworkError() {
    override val title: String
        get() = "알 수 없는 오류가 발생했어요"
    override val message: String
        get() = "잠시 후 다시 시도해주세요."
}
