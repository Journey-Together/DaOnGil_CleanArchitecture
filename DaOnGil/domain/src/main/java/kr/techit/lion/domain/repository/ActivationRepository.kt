package kr.techit.lion.domain.repository

import kotlinx.coroutines.flow.Flow

interface ActivationRepository {
    suspend fun saveUserActivation(active: Boolean)
    val userActivation: Flow<Boolean>
}