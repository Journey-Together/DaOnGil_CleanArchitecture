package kr.tekit.lion.data.repository

import kotlinx.coroutines.flow.Flow
import kr.tekit.lion.data.datasource.AuthDataSource
import kr.tekit.lion.data.datasource.TokenDataSource
import kr.tekit.lion.data.dto.request.SignInRequest
import kr.tekit.lion.data.dto.request.toRequestBody
import kr.tekit.lion.domain.repository.AuthRepository
import kr.tekit.lion.domain.exception.Result
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val tokenDataSource: TokenDataSource,
) : AuthRepository {

    override val loggedIn: Flow<Boolean>
        get() = authDataSource.loggedIn

    override suspend fun signIn(type: String, accessToken: String, refreshToken: String) {
        val request = SignInRequest(refreshToken).toRequestBody()
        authDataSource.signIn(type, accessToken, request).onSuccess { response ->
            tokenDataSource.saveTokens(response.data.accessToken, response.data.refreshToken)
        }.onFailure {
            it.printStackTrace()
        }
    }

    override suspend fun logout(): kotlin.Result<Unit> = authDataSource.logout()

    override suspend fun withdraw(): Result<Unit> = authDataSource.withdraw()
}