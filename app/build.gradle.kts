plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.github.zeroaicy.aide"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.github.zeroaicy.aide"
        minSdk = 24
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 28
        //noinspection HighAppVersionCode
        versionCode = 2008210017
        // [3.2.210316]
        versionName = "2.3.2.0"

        //multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        create("release") {
            keyAlias = "AIDE+"
            keyPassword = "123789456"
            storePassword = "123789456"
            storeFile = file("release.jks")
        }
        create("debug1") {
            keyAlias = "androiddebug"
            keyPassword = "123789456"
            storePassword = "123789456"
            storeFile = file("debug.jks")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug1")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = isRelease
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            /// 排除文件
            //excludes += "/META-INF/{AL2.0,LGPL2.1}"

            /// 重复替换
            pickFirsts += "/META-INF/{AL2.0,LGPL2.1}"
            pickFirsts += "/assets/*/*/*/*"
            pickFirsts += "/assets/*/*/*"
            pickFirsts += "/assets/*/*"
            pickFirsts += "/assets/*"
            pickFirsts += "/*"
            pickFirsts += "assets/templates/*"

        }
        jniLibs {
            useLegacyPackaging = true
            pickFirsts += "/lib/*/*"
        }
    }


    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    lint {
        abortOnError = true
        checkReleaseBuilds = false
    }
}

dependencies {
    /// 依赖libs目录文件
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))


    /// 项目主体
    api(projects.appRewrite)
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    /// 测试用的
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    /// 发版的时候进行脱糖
    if (isRelease) {
        coreLibraryDesugaring("com.android.tools:desugar_jdk_lib:2.1.3")
    }
}


val isRelease: Boolean = gradle.startParameter.taskNames.any { taskName ->
    taskName.contains("Release", ignoreCase = true)
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

/*
configurations.all {
    resolutionStrategy {
        // 对冲突的依赖直接使用最新版本
        force("")
        //failOnVersionConflict()

    }
}
*/




/// 创建复制预编译 dex 文件的任务
tasks.register<Copy>("copyPrecompiledDex") {
    from("Dex")
    from(File("${layout.projectDirectory.asFile}","Dex"))
    into("${layout.buildDirectory.get().asFile}/intermediates/dex/debug/mergeDexDebug")
    include("*.dex")
}

// 配置 dex 合并任务
tasks.whenTaskAdded {
    if (name == "mergeDexDebug" || name == "mergeDexRelease") {
        dependsOn("copyPrecompiledDex")
    }
    if (name == "packageRelease"|| name == "packageDebug"){
        dependsOn("")
    }
}
