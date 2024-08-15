package kr.tekit.lion.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kr.tekit.lion.data.database.AppSettings
import kr.tekit.lion.data.database.dataStore
import kr.tekit.lion.domain.model.AppTheme
import javax.inject.Inject

class AppThemeDataSource @Inject constructor(
    private val context: Context,
){
    private val dataStore: DataStore<AppSettings>
        get() = context.dataStore

    private val data: Flow<AppSettings>
        get() = dataStore.data

    suspend fun getAppTheme(): AppTheme = data.first().appTheme

    suspend fun saveAppTheme(appTheme: AppTheme){
        dataStore.updateData {
            it.copy(
                appTheme = appTheme
            )
        }
    }
}