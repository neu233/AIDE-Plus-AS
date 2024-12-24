plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.android")
    id("com.android.library")
}

android {
    namespace = "org.jetbrains.kotlin"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
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
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {

    // This dependency is exported to consumers, that is to say found on their compile classpath.
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
    implementation("io.github.itsaky:nb-javac-android:17.0.0.3")
    implementation("org.jetbrains.intellij.deps:trove4j:1.0.20200330")
    implementation("org.jdom:jdom:2.0.2")

    implementation(projects.compiletion.jaxp)

    //api(files("libs/kotlin-compiler-1.9.20.jar")) /// k2j

    api(files("libs/kotlin-compiler-2.0.0.jar"))  /// 缺少k2j的api



    api(files("libs/ktfmt-0.44.jar"))

    // https://mvnrepository.com/artifact/com.google.googlejavaformat/google-java-format
    api("com.google.googlejavaformat:google-java-format:1.24.0")

    compileOnly(projects.compiletion.kotlin.theUnsafe)



}
configurations.all {
    //exclude("org.jetbrains","annotations")

    //exclude("com.google.guava","guava")
    //exclude(group = "org.jline", module = "jline")
    exclude("net.java.dev.jna","jna")
    exclude("net.java.dev.jna","jna-platform")
}

