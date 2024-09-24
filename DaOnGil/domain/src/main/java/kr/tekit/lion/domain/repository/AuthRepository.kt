package kr.tekit.lion.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(type: String, accessToken: String, refreshToken: String)
    suspend fun logout(): Result<Unit>
    val loggedIn: Flow<Boolean>
}