

@Suppress("JavaPluginLanguageLevel")
plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    api("androidx.annotation:annotation:1.9.1")
    api("com.android.tools:annotations:31.7.3")
    api("org.jetbrains:annotations:24.1.0")
    implementation("com.google.guava:guava:24.1-jre") {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }}
