package kr.tekit.lion.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kr.tekit.lion.data.database.AppSettings
import kr.tekit.lion.data.database.dataStore
import kr.tekit.lion.data.dto.response.SignUpResponse
import kr.tekit.lion.data.service.AuthService
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

    suspend fun signIn(type: String, token: String) = runCatching {
        authService.signIn(type = type, token = token)
    }

    suspend fun refresh(): Result<SignUpResponse?> = runCatching {
        val refreshToken = data.map { it.refreshToken }.first()

        if (refreshToken.isNotBlank()) authService.refresh(refreshToken) else null
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