package kr.techit.lion.data.service

import kotlinx.coroutines.runBlocking
import kr.techit.lion.data.datasource.TokenDataSource
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * 네트워크 인터셉터로 Authorization 헤더를 추가
 *
 * @constructor AuthInterceptor는 TokenDataSource를 통해 토큰을 가져옵니다.
 * @property tokenDataSource 토큰을 가져오는 DataSource
 */
internal class AuthInterceptor @Inject constructor(
    private val tokenDataSource: TokenDataSource
) : Interceptor {

    /**
     * HTTP 요청을 인터셉트하여 Authorization 헤더를 추가
     *
     * @param chain Interceptor.Chain 객체로, 요청 및 응답을 관리
     * @return Response 객체로, 수정된 요청을 통해 받은 응답
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        val authType = request.tag(AuthType::class.java) ?: AuthType.ACCESS_TOKEN

        when(authType){
            AuthType.NO_AUTH -> {
                builder.addHeader("Content-Type", "application/json")
            }
            AuthType.ACCESS_TOKEN -> {
                val accessToken = runBlocking{ tokenDataSource.getAccessToken() }

                builder.addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer $accessToken")
            }
        }
        return chain.proceed(builder.build())
    }
}
/**
 * Authorization 타입을 나타내는 열거형 클래스
 */
enum class AuthType {
    NO_AUTH,        // 인증이 필요 없는 요청
    ACCESS_TOKEN    // Access Token이 필요한 요청
}