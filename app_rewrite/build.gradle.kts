plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.aide.ui.rewrite"
    compileSdk = 35
    /*
    targetSdkVersion 28
    versionCode 2008210017
    versionName "3.2.210316"
    */

    defaultConfig {
        minSdk = 24

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    compileOnly(fileTree(mapOf("dir" to "provider", "include" to listOf("*.jar"))))

    api(fileTree(mapOf("dir" to "libs-xml-completion", "include" to listOf("*.jar"))))

    api("androidx.core:core-ktx:1.15.0")
    api("androidx.preference:preference-ktx:1.2.1")
    api("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
    // r8 必须脱糖否则只能安卓13使用
    //api project(':kotlin')
    api(projects.appAideBase){
        isTransitive = true
    }

    // https://mvnrepository.com/artifact/org.eclipse.jdt/ecj
    // api("org.eclipse.jdt:ecj:3.39.0:sources")
    compileOnly("org.eclipse.jdt:ecj:3.39.0")

    api("net.margaritov.preference.colorpicker.ColorPickerPreference:ColorPickerPreference:1.0.0")


    // xml-completion
    api("com.google.guava:guava:33.2.1-android")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.24")
    api("androidx.annotation:annotation:1.9.1")
    api("androidx.collection:collection-ktx:1.4.5")
    api("org.jetbrains:annotations:24.1.0")
    api("com.android.tools:annotations:31.7.3")
    api("com.android.tools:common:31.7.3")
    api("com.android.tools.build:aapt2-proto:8.3.2-10880808")
    api("com.google.protobuf:protobuf-java:3.25.3")
    // jgit 7.0.0.202409031743-r
    api("org.eclipse.jgit:org.eclipse.jgit:7.0.0.202409031743-r")

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}