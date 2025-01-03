plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.aide.ui.base"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        /*
        versionCode 2008210017
        versionName "3.2.210316"
        */

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }


    lint {
        abortOnError = false
        baseline = file("lint-baseline.xml")
    }
}

dependencies {

    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    compileOnly(fileTree(mapOf("dir" to "provider", "include" to listOf("*.jar"))))


    api("dev.rikka.shizuku:provider:13.1.5")
    api("dev.rikka.shizuku:api:13.1.5")

    api("androidx.browser:browser:1.8.0")
    api("androidx.drawerlayout:drawerlayout:1.2.0")
    api("androidx.legacy:legacy-support-v4:1.0.0")
    api("androidx.multidex:multidex:2.0.1")

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.13.0-alpha09")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}