package kr.techit.lion.domain.repository

import kotlinx.coroutines.flow.Flow
import kr.techit.lion.domain.exception.Result

interface AuthRepository {
    suspend fun signIn(type: String, accessToken: String, refreshToken: String)
    suspend fun logout(): kotlin.Result<Unit>
    suspend fun withdraw(): Result<Unit>
    val loggedIn: Flow<Boolean>
}