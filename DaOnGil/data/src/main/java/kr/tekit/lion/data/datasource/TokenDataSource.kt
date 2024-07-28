package kr.tekit.lion.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kr.tekit.lion.data.database.AppSettings
import kr.tekit.lion.data.database.dataStore
import javax.inject.Inject

class TokenDataSource @Inject constructor(
    private val context: Context
) {
    private val dataStore: DataStore<AppSettings>
        get() = context.dataStore

    private val data: Flow<AppSettings>
        get() = dataStore.data

    suspend fun getAccessToken(): String = data.first().accessToken

    suspend fun saveTokens(accessToken: String, refreshToken: String){
        dataStore.updateData {
            it.copy(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        }
    }
}
