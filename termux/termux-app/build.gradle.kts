plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {

    val ndkVersion: String by project
    val compileSdkVersion: String by project
    this.ndkVersion = ndkVersion
    namespace = "com.termux"
    compileSdk = compileSdkVersion.toInt()

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
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }


    kotlinOptions {
        jvmTarget = "21"
    }
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }

    lint {
        warningsAsErrors = true
    }

    lint {
        disable += "DefaultLocale"
        abortOnError = false
        baseline = file("lint-baseline.xml")
    }
}

dependencies {

    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))


    api(projects.termux.terminalView)
    api(projects.termux.termuxShared)

    val markwonVersion = "4.6.2"
    implementation("io.noties.markwon:core:$markwonVersion")
    implementation("io.noties.markwon:ext-strikethrough:$markwonVersion")
    implementation("io.noties.markwon:linkify:$markwonVersion")
    implementation("io.noties.markwon:recycler:$markwonVersion")


    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.13.0-alpha09")

    implementation("androidx.preference:preference-ktx:1.2.1")

    //noinspection GradleDependency
    implementation("com.google.guava:guava:24.1-jre") {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }    /// 测试用的
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")


}


