package kr.techit.lion.data.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore

internal val Context.dataStore: DataStore<AppSettings> by dataStore(
    "app-settings.json",
    AppSettingsSerializer
)