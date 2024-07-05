import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())
val kakaoApiKey = properties.getProperty("kakao_api_key") ?: ""
val kakaoNativeKey = properties.getProperty("kakao_native_key") ?: ""
val naverMapId = properties.getProperty("naver_map_id") ?: ""
val naverClientId = properties.getProperty("naver_client_id") ?: ""
val naverClientSecret = properties.getProperty("naver_client_secret") ?: ""
val naverClientName = properties.getProperty("naver_client_name") ?: ""

android {
    namespace = "kr.tekit.lion.daongil_cleanarchitecture"
    compileSdk = 34

    defaultConfig {
        applicationId = "kr.tekit.lion.daongil_cleanarchitecture"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "KAKAO_API_KEY", "\"$kakaoApiKey\"")
        buildConfigField("String", "NAVER_CLIENT_ID", "\"$naverClientId\"")
        buildConfigField("String", "NAVER_CLIENT_SECRET", "\"$naverClientSecret\"")
        buildConfigField("String", "NAVER_CLIENT_NAME", "\"$naverClientName\"")
        buildConfigField("String", "KAKAO_NATIVE_KEY", "\"$kakaoNativeKey\"")
        manifestPlaceholders["KAKAO_NATIVE_KEY"] = kakaoNativeKey
        manifestPlaceholders["NAVER_MAP_ID"] = naverMapId
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    kapt{
        correctErrorTypes = true
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":presentation"))

    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.hilt)
    kapt(libs.hilt.compiler)

    implementation(libs.kakao.user)
    implementation(libs.navercorp)
}