plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.github.zeroaicy.aide"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.github.zeroaicy.aide.aaptcompiler"
        minSdk = 26
        //noinspection EditedTargetSdkVersion
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        resources {
            excludes += "META-INF/AL2.0"
            excludes += "META-INF/LGPL2.1"
            //excludes += "kotlin/*/*.kotlin_builtins"
            excludes += "*/*/*.kotlin_builtins"
            excludes += "*/*.kotlin_builtins"

        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    api(projects.compiletion.xml.resParse)



    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    /// 测试用的
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")


    api(files("libs/MTDataFilesProvider.jar"))



    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.2")


}

configurations.all {
    //exclude("com.google.guava","listenablefuture")
    //exclude("com.google.guava","guava")


    exclude("net.java.dev.jna", "jna")
    exclude("net.java.dev.jna", "jna-platform")
}

