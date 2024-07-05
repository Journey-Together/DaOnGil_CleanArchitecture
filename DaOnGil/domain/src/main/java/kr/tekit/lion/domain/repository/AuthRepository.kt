package kr.tekit.lion.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(type: String, token: String)
    val loggedIn: Flow<Boolean>
}