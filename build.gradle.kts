// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    dependencies {
        //26.08.2026
        // Google Maps API anahtarını kaynak koduna yazmadan kullanmak için
        classpath(
            "com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1"
        )
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}