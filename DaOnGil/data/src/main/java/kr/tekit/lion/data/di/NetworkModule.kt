package kr.tekit.lion.data.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.tekit.lion.data.BuildConfig
import kr.tekit.lion.data.service.AuthService
import kr.tekit.lion.data.service.KorWithService
import kr.tekit.lion.data.service.MemberService
import kr.tekit.lion.data.service.PlaceService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(@Auth client: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideMemberService(retrofit: Retrofit): MemberService {
        return retrofit.create(MemberService::class.java)
    }

    @Provides
    @Singleton
    fun providePlaceService(retrofit: Retrofit): PlaceService {
        return retrofit.create(PlaceService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthService(okHttpClient: OkHttpClient): AuthService =
         Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .client(okHttpClient)
            .build()
            .create()

    @Singleton
    @Provides
    fun provideKorWithService(okHttpClient: OkHttpClient): KorWithService =
        Retrofit.Builder()
            .baseUrl("https://apis.data.go.kr/B551011/KorWithService1/")
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .client(okHttpClient)
            .build()
            .create()

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(LocalDateTime::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .add(LocalDate::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .add(KotlinJsonAdapterFactory())
            .build()
    }
}
