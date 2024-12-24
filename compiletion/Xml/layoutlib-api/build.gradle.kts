

@Suppress("JavaPluginLanguageLevel")
plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    api("androidx.annotation:annotation:1.9.1")
    api("com.android.tools:annotations:31.7.3")
    api("org.jetbrains:annotations:24.1.0")
    api("com.google.guava:guava:33.3.1-android")
}
