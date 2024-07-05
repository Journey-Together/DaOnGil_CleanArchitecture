package kr.tekit.lion.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.tekit.lion.data.datasource.AuthDataSource
import kr.tekit.lion.data.datasource.TokenDataSource
import kr.tekit.lion.data.service.AuthService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataSourceModule {

    @Singleton
    @Provides
    fun provideAuthDataStore(
        @ApplicationContext context: Context,
        authService: AuthService
    ): AuthDataSource = AuthDataSource(context, authService)

    @Singleton
    @Provides
    fun provideTokenDataSource(
        @ApplicationContext context: Context
    ): TokenDataSource = TokenDataSource(context)
}