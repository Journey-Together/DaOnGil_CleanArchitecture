import java.util.Properties

plugins {
    id("kotlinx-serialization")
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.room)
    alias(libs.plugins.hilt)
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())
val baseUrl = properties.getProperty("base_url") ?: ""
val emergencyBaseUrl = properties.getProperty("emergency_base_url")?:""
val emergencyApiKey = properties.getProperty("emergency_api_key")?:""
val aedBaseUrl = properties.getProperty("aed_base_url")?:""
val pharmacyBaseUrl = properties.getProperty("pharmacy_base_url")?:""
val korApiKey = properties.getProperty("kor_api_key")?:""
val naverMapBase = properties.getProperty("naver_map_base") ?: ""
val naverMapId = properties.getProperty("naver_map_id") ?: ""
val naverMapSecret = properties.getProperty("naver_map_secret") ?: ""

android {
    namespace = "kr.techit.lion.data"
    compileSdk = 34

    room {
        schemaDirectory("$projectDir/schemas")
    }

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "NAVER_MAP_BASE", "\"$naverMapBase\"")
        buildConfigField("String", "EMERGENCY_BASE_URL", "\"$emergencyBaseUrl\"")
        buildConfigField("String", "EMERGENCY_API_KEY", "\"$emergencyApiKey\"")
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "AED_BASE_URL", "\"$aedBaseUrl\"")
        buildConfigField("String", "PHARMACY_BASE_URL", "\"$pharmacyBaseUrl\"")
        buildConfigField("String", "KOR_API_KEY", "\"$korApiKey\"")
        buildConfigField("String", "NAVER_MAP_ID", "\"$naverMapId\"")
        buildConfigField("String", "NAVER_MAP_SECRET", "\"$naverMapSecret\"")
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
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(project(":domain"))
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.hilt)
    kapt(libs.hilt.compiler)

    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation(libs.datastore)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    implementation(libs.gson)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.converter.moshi)
    implementation(libs.moshi.adapters)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    kapt(libs.moshi.codegen)
}