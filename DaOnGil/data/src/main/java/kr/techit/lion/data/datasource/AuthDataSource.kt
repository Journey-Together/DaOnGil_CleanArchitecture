package kr.techit.lion.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kr.techit.lion.data.common.execute
import kr.techit.lion.data.database.AppSettings
import kr.techit.lion.data.database.dataStore
import kr.techit.lion.data.dto.response.SignUpResponse
import kr.techit.lion.data.service.AuthService
import kr.techit.lion.domain.exception.Result
import okhttp3.RequestBody
import javax.inject.Inject

internal class AuthDataSource @Inject constructor(
    private val context: Context,
    private val authService: AuthService
) {
    private val dataStore: DataStore<AppSettings>
        get() = context.dataStore

    private val data: Flow<AppSettings>
        get() = dataStore.data

    val loggedIn: Flow<Boolean>
        get() = data.map { it.accessToken.isNotBlank() }

    suspend fun signIn(type: String, accessToken: String, refreshToken: RequestBody) = runCatching {
        authService.signIn(type = type, token = "Bearer $accessToken", requestBody = refreshToken)
    }

     suspend fun logout(): kotlin.Result<Unit> = runCatching{
        val accessToken = data.map { it.accessToken }.first()
        localLogout()
        authService.signOut("Bearer $accessToken")
    }

    suspend fun refresh(): kotlin.Result<SignUpResponse?> = runCatching{
        val refreshToken = data.map { it.refreshToken }.first()

        if (refreshToken.isNotBlank()) authService.refresh("Bearer $refreshToken") else null
    }

    suspend fun withdraw(): Result<Unit> = execute{
        val accessToken = data.map { it.accessToken }.first()
        localLogout()
        authService.withdraw("Bearer $accessToken")
    }

    private suspend fun localLogout() {
        dataStore.updateData {
            it.copy(
                accessToken = "",
                refreshToken = "",
            )
        }
    }
}