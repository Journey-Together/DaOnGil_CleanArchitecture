package kr.techit.lion.domain.exception

sealed class HttpError : NetworkError() {
    abstract override val title: String
    abstract override val message: String
}
data object BadRequestError : HttpError(){
    override val title: String
        get() = "인증 오류가 발생했어요"
    override val message: String
        get() = "요청 내용을 확인하고 다시 시도해주세요."
}
data object AuthenticationError : HttpError() {
    override val title: String
        get() = "로그인 문제가 발생했습니다."
    override val message: String
        get() = "문제가 반복 된다면 다시 로그인해주세요."
}

data object AuthorizationError : HttpError() {
    override val title: String
        get() = "서비스 이용 권한에 문제가 발생했습니다."
    override val message: String
        get() = "해당 서비스에 대한 유저 권한이 없어요."
}

data object NotFoundError : HttpError() {
    override val title: String
        get() = "해당 서비스를 찾을 수 없어요"
    override val message: String
        get() = "요청하신 페이지 또는 정보가 존재하지 않습니다."
}

data object ServerError : HttpError() {
    override val title: String
        get() = "서버에 오류가 발생했어요"
    override val message: String
        get() = "잠시후 다시 시도해주세요."
}
