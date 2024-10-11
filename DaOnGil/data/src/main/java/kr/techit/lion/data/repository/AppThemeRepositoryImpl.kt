package kr.techit.lion.data.repository

import kr.techit.lion.data.datasource.AppThemeDataSource
import kr.techit.lion.domain.model.AppTheme
import kr.techit.lion.domain.repository.AppThemeRepository
import javax.inject.Inject

class AppThemeRepositoryImpl @Inject constructor(
    private val appThemeDataSource: AppThemeDataSource
): AppThemeRepository {

    override suspend fun getAppTheme(): AppTheme {
        return appThemeDataSource.getAppTheme()
    }

    override suspend fun saveAppTheme(appTheme: AppTheme) {
        appThemeDataSource.saveAppTheme(appTheme)
    }
}