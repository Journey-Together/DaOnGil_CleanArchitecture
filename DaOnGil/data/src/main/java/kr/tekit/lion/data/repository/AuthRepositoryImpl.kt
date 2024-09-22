package kr.tekit.lion.data.repository

import kotlinx.coroutines.flow.Flow
import kr.tekit.lion.data.datasource.AuthDataSource
import kr.tekit.lion.data.datasource.TokenDataSource
import kr.tekit.lion.domain.repository.AuthRepository
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val tokenDataSource: TokenDataSource,
) : AuthRepository {

    override val loggedIn: Flow<Boolean>
        get() = authDataSource.loggedIn

    override suspend fun signIn(type: String, accessToken: String, refreshToken: String) {
        authDataSource.signIn(type, accessToken, refreshToken.toRequestBody()).onSuccess { response ->
            tokenDataSource.saveTokens(response.data.accessToken, response.data.refreshToken)
        }.onFailure {
            it.printStackTrace()
        }
    }

    override suspend fun logout(): Result<Unit> = authDataSource.logout()
}