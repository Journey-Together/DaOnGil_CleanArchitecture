plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.navigation.safe.args)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "kr.tekit.lion.presentation"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    viewBinding {
        enable = true
    }
}

dependencies {

    implementation(project(":domain"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.hilt)
    kapt(libs.hilt.compiler)

    implementation(libs.navigation.ktx)
    implementation(libs.navigation)

    implementation(libs.google.services.v441)
    implementation(libs.simple.rating.bar)
    implementation(libs.droidsonroids)
    implementation(libs.glide)
    kapt(libs.glide.compiler)

    implementation(libs.circleindicator)

    implementation(libs.naverMap)
    implementation(libs.navercorp)
    implementation(libs.kakao.user)
    implementation(libs.splash)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.core.jvm)
}