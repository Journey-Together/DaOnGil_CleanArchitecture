package kr.tekit.lion.data.repository

import kotlinx.coroutines.flow.Flow
import kr.tekit.lion.data.datasource.AuthDataSource
import kr.tekit.lion.data.datasource.TokenDataSource
import kr.tekit.lion.domain.repository.AuthRepository
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val tokenDataSource: TokenDataSource,
) : AuthRepository {

    override val loggedIn: Flow<Boolean>
        get() = authDataSource.loggedIn

    override suspend fun signIn(type: String, token: String) {

        authDataSource.signIn(type, token).onSuccess { response ->
            tokenDataSource.saveTokens(response.data.accessToken, response.data.refreshToken)
        }.onFailure {
            it.printStackTrace()
        }
    }
}