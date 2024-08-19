package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.model.AppTheme

interface AppThemeRepository {
    suspend fun getAppTheme(): AppTheme
    suspend fun saveAppTheme(appTheme: AppTheme)
}