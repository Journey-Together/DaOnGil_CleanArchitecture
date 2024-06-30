package kr.tekit.lion.data.di

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.tekit.lion.data.service.KorWithService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)
            ).build()
    }

    @Singleton
    @Provides
    fun provideKorWithService(okHttpClient: OkHttpClient): KorWithService =
        Retrofit.Builder()
            .baseUrl("https://apis.data.go.kr/B551011/KorWithService1/")
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .client(okHttpClient)
            .build()
            .create()
}