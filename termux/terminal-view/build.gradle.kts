plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    val compileSdkVersion: String by project
    compileSdk = compileSdkVersion.toInt()
    namespace = "com.termux.view"

    defaultConfig {
        val minSdkVersion: String by project
        minSdk = minSdkVersion.toInt()

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

    api(projects.termux.terminalEmulator)
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.13.0-alpha09")

    /// 测试用的
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

}