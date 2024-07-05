buildscript {
    dependencies{
        classpath(libs.navigation.safe.args)
        classpath (libs.google.services.v441)
        classpath(libs.kotlin.serialization.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.navigation.safe.args) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
}