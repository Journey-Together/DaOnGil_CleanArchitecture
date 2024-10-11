package kr.techit.lion.domain.repository

import kr.techit.lion.domain.model.AppTheme

interface AppThemeRepository {
    suspend fun getAppTheme(): AppTheme
    suspend fun saveAppTheme(appTheme: AppTheme)
}