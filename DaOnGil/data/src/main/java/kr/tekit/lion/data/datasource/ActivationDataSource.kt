package kr.tekit.lion.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kr.tekit.lion.data.database.AppSettings
import kr.tekit.lion.data.database.dataStore
import javax.inject.Inject

internal class ActivationDataSource @Inject constructor(
    private val context: Context
){
    private val dataStore: DataStore<AppSettings>
        get() = context.dataStore

    private val data: Flow<AppSettings>
        get() = dataStore.data

    val activation: Flow<Boolean>
        get() = data.map { it.activation }

    suspend fun saveUserActivation(active: Boolean) {
        dataStore.updateData {
            it.copy(
                activation = active
            )
        }
    }
}