package kr.tekit.lion.data.service

import kr.tekit.lion.data.datasource.AuthDataSource
import kr.tekit.lion.data.datasource.TokenDataSource
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val tokenDataSource: TokenDataSource,
    private val authDataSource: AuthDataSource,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request {
        return response.request.newBuilder().header("Authorization", "Bearer ").build()
    }
}