package kr.techit.lion.data.service

import kotlinx.coroutines.runBlocking
import kr.techit.lion.data.datasource.AuthDataSource
import kr.techit.lion.data.datasource.TokenDataSource
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

/**
 * 401 HTTP 에러 코드를 받았을 때 호출되는 클래스
 *
 * Access Token이 만료되었을 때 Refresh Token으로 Access Token을 갱신하고 API를 재요청함.
 *
 * @property tokenDataSource 토큰을 관리하는 데이터 소스.
 * @property authDataSource 인증을 관리하는 데이터 소스.
 */
internal class AuthAuthenticator @Inject constructor(
    private val tokenDataSource: TokenDataSource,
    private val authDataSource: AuthDataSource
) : Authenticator {

    /**
     * 만료된 Access Token을 새로운 토큰으로 교체하여 요청을 인증함.
     *
     * @param route 요청을 생성한 경로 (null일 수 있음)
     * @param response 401 상태 코드와 함께 수신된 응답.
     * @return 업데이트된 Authorization 헤더를 가진 새로운 요청 또는 인증이 실패한 경우 null.
     */
    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken = runBlocking { getNewAccessToken() } ?: return null
        return response.request.newBuilder().header("Authorization", "Bearer $accessToken").build()
    }

    /**
     * Refresh Token을 사용하여 새로운 Access Token을 가져옴.
     *
     * @return 새로운 Access Token, 실패 시 null.
     */
    private suspend fun getNewAccessToken(): String? {
        val newToken = authDataSource.refresh().getOrNull()
        return newToken?.let {
            tokenDataSource.saveTokens(
                accessToken = it.accessToken,
                refreshToken = it.refreshToken,
            )
            it.accessToken
        } ?: run {
            authDataSource.logout()
            null
        }
    }
}
