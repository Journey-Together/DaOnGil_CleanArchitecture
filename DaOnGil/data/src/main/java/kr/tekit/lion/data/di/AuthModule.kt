package kr.tekit.lion.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.tekit.lion.data.BuildConfig
import kr.tekit.lion.data.datasource.AuthDataSource
import kr.tekit.lion.data.datasource.TokenDataSource
import kr.tekit.lion.data.service.AuthAuthenticator
import kr.tekit.lion.data.service.AuthInterceptor
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AuthModule {

    @Singleton
    @Provides
    fun provideAuthInterceptor(tokenDataSource: TokenDataSource)
       : AuthInterceptor = AuthInterceptor(tokenDataSource)

    @Singleton
    @Provides
    fun provideAuthenticator(
        tokenDataSource: TokenDataSource,
        authDataSource: AuthDataSource
    ): Authenticator = AuthAuthenticator(tokenDataSource, authDataSource)

    @Auth
    @Singleton
    @Provides
    fun provideAuthClient(
        authenticator: AuthAuthenticator,
        interceptor: AuthInterceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .authenticator(authenticator)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }
}
