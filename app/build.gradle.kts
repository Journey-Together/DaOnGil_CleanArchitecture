import java.util.Properties

plugins {
    alias(libs.plugins.daongil.app)
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
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }

    namespace = "kr.techit.lion.daongil_cleanarchitecture"

    defaultConfig {
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
    buildFeatures {
        buildConfig = true
    }
}