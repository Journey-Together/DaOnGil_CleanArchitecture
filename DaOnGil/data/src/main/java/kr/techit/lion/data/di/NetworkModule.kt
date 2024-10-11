package kr.techit.lion.data.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.techit.lion.data.BuildConfig
import kr.techit.lion.data.dto.request.util.LocalDateAdapter
import kr.techit.lion.data.dto.response.aed.AedJsonAdapter
import kr.techit.lion.data.dto.response.emergency.message.EmergencyMessageJsonAdapter
import kr.techit.lion.data.dto.response.emergency.realtime.EmergencyRealtimeJsonAdapter
import kr.techit.lion.data.service.AedService
import kr.techit.lion.data.service.AuthService
import kr.techit.lion.data.service.BookmarkService
import kr.techit.lion.data.service.EmergencyService
import kr.techit.lion.data.service.KorWithService
import kr.techit.lion.data.service.MemberService
import kr.techit.lion.data.service.NaverMapService
import kr.techit.lion.data.service.PharmacyService
import kr.techit.lion.data.service.PlaceService
import kr.techit.lion.data.service.PlanService
import kr.techit.lion.data.service.ReportService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
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
    fun provideBookmarkService(retrofit: Retrofit): BookmarkService {
        return retrofit.create(BookmarkService::class.java)
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
    fun provideNaverMapService(@NaverMap okHttpClient: OkHttpClient): NaverMapService =
        Retrofit.Builder()
            .baseUrl(BuildConfig.NAVER_MAP_BASE)
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .client(okHttpClient)
            .build()
            .create()

    @Singleton
    @Provides
    fun provideAedService(okHttpClient: OkHttpClient, @AedMoshi moshi: Moshi): AedService =
        Retrofit.Builder()
            .baseUrl(BuildConfig.AED_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .client(okHttpClient)
            .build()
            .create()

    @Singleton
    @Provides
    fun provideEmergencyService(
        okHttpClient: OkHttpClient,
        @EmergencyMoshi moshi: Moshi
    ): EmergencyService =
        Retrofit.Builder()
            .baseUrl(BuildConfig.EMERGENCY_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .client(okHttpClient)
            .build()
            .create()

    @Provides
    @Singleton
    fun providePharmacyService(
        okHttpClient: OkHttpClient
    ): PharmacyService =
        Retrofit.Builder()
            .baseUrl(BuildConfig.PHARMACY_BASE_URL)
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

    @NaverMap
    @Singleton
    @Provides
    fun provideNaverMapClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-NCP-APIGW-API-KEY-ID", BuildConfig.NAVER_MAP_ID)
                    .addHeader("X-NCP-APIGW-API-KEY", BuildConfig.NAVER_MAP_SECRET)
                    .build()
                chain.proceed(request)
            }
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
            .add(LocalDateAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @AedMoshi
    @Provides
    @Singleton
    fun provideAedMoshi(): Moshi {
        return Moshi.Builder()
            .add(AedJsonAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @EmergencyMoshi
    @Provides
    @Singleton
    fun provideEmergencyMoshi(): Moshi {
        return Moshi.Builder()
            .add(EmergencyRealtimeJsonAdapter())
            .add(EmergencyMessageJsonAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun providePlanService(retrofit: Retrofit): PlanService {
        return retrofit.create(PlanService::class.java)
    }

    @Provides
    @Singleton
    fun provideReportService(retrofit: Retrofit): ReportService {
        return retrofit.create(ReportService::class.java)
    }
}
