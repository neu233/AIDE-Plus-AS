
plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "io.github.zeroaicy.aide.aaptcompiler"

    compileSdk = 35

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    // 太多错误了，直接引用jar包
    api(files("libs/xml-completion-jaxp.jar"))
    api(projects.compiletion.xml.layoutlibApi)


    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.24")
    api("androidx.collection:collection-ktx:1.4.5")

    api("org.jetbrains:annotations:24.1.0")


    api("com.android.tools:annotations:31.7.3")
    api("com.android.tools:common:31.7.3")
    api("com.android.tools.build:aapt2-proto:8.3.2-10880808")
    api("com.google.protobuf:protobuf-java:3.25.3")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}