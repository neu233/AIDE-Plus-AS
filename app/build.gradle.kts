import com.android.tools.build.apkzlib.zip.ZFile
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Paths

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
            pickFirsts += "/assets/*/*/*/*/*/*/*"
            pickFirsts += "/assets/*/*/*/*/*/*"
            pickFirsts += "/assets/*/*/*/*/*"
            pickFirsts += "/assets/*/*/*/*"
            pickFirsts += "/assets/*/*/*"
            pickFirsts += "/assets/*/*"
            pickFirsts += "/assets/*"
            pickFirsts += "/*"


            pickFirsts += "org/eclipse/jdt/internal/compiler/parser/**"
            pickFirsts += "org/eclipse/jdt/internal/*/**"

            pickFirsts += "org/eclipse/jdt/core/*/**"
            pickFirsts += "org/eclipse/jdt/core/**"

            pickFirsts += "org/eclipse/jdt/internal/*/*/*/*"
            pickFirsts += "org/eclipse/jdt/internal/compiler/parser/*/*/*"
            pickFirsts += "org/eclipse/jdt/internal/*/*/*/*/*/*"

        }
        jniLibs {
            useLegacyPackaging = true
            pickFirsts += "/lib/*/*"
        }
    }
    androidResources {
        val publicXmlFile = project.rootProject.file("${project(":appAideBase").projectDir.path}/src/main/res/values/public.xml")
        val publicTxtFile = project.rootProject.file("${layout.buildDirectory.asFile.get().path}/public.txt")

        // 创建父目录并确保 publicTxtFile 存在
        publicTxtFile.parentFile.mkdirs()
        publicTxtFile.createNewFile()

        // 解析 public.xml 文件并将内容写入 public.txt
        val nodes = javax.xml.parsers.DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(publicXmlFile)
            .documentElement
            .getElementsByTagName("public")

        for (i in 0 until nodes.length) {
            val node = nodes.item(i)
            val type = node.attributes.getNamedItem("type").nodeValue
            val name = node.attributes.getNamedItem("name").nodeValue
            val id = node.attributes.getNamedItem("id").nodeValue
            publicTxtFile.appendText("${android.defaultConfig.applicationId}:$type/$name = $id\n")
        }

        // 添加稳定 ID 参数
        additionalParameters("--stable-ids", publicTxtFile.path)
    }



    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    lint {
        abortOnError = false
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

configurations.all {
    //exclude("com.google.guava","listenablefuture")
    //exclude("com.google.guava","guava")
    exclude("net.java.dev.jna", "jna")
    exclude("net.java.dev.jna", "jna-platform")
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



afterEvaluate {


    val AppDexCount: (String, Boolean) -> Int = { buildDir, isDebug ->
        val dexDir = Paths.get(
            buildDir,
            "intermediates",
            "dex",
            if (isDebug) "debug" else "release",
            if (isDebug) "mergeDexDebug" else "mergeDexRelease"
        )
        if (!Files.exists(dexDir)) 0
        else {
            Files.list(dexDir).use { it.count().toInt() }
        }
    }

    val copy: (Project, File, Boolean) -> Unit = { project, resFile, isDebug ->
        val buildDir = project.layout.buildDirectory.asFile.get().path
        val dexBytes = mutableListOf<ByteArray>()
        val dexFolder = project.layout.projectDirectory.file("Dex")
        println(dexFolder.asFile.absolutePath)
        dexFolder.asFile.listFiles()?.forEach {
            if (it.name.endsWith(".dex")) {
                println(it.absolutePath)
                dexBytes.add(Files.readAllBytes(it.toPath()))
            }
        }
        val appDexCount = AppDexCount(buildDir, isDebug)
        check(appDexCount != 0) { "Unexpected app dex count" }

        try {
            ZFile.openReadWrite(resFile).use { zip ->
                val iterator = dexBytes.iterator()
                for (i in (appDexCount + 2)..Int.MAX_VALUE) {
                    if (!iterator.hasNext()) break
                    val name = "classes$i.dex"
                    println(name)
                    zip.add(name, ByteArrayInputStream(iterator.next()))
                }
            }
        }catch (e: Exception){
            throw e
        }


    }


    tasks.register("copyDexRelease") {
        doLast {
            val zip = layout.buildDirectory.file(
                "intermediates/optimized_processed_res/release/optimizeReleaseResources/resources-release-optimize.ap_"
            ).get().asFile
            copy(project, zip, false)
        }
    }

    tasks.register("copyDexDebug") {
        doLast {
            val zip = layout.buildDirectory.file(
                "intermediates/processed_res/debug/processDebugResources/out/resources-debug.ap_"
            ).get().asFile
            copy(project, zip, true)
        }
    }




    tasks.configureEach {
        if (name == "mergeDexRelease") {
            finalizedBy("copyDexRelease")
        } else if (name == "mergeDexDebug") {
            finalizedBy("copyDexDebug")
        }
    }
}

/*

/// 创建复制预编译 dex 文件的任务
tasks.register<Copy>("copyPrecompiledDex") {
    from("Dex")
    from(File("${layout.projectDirectory.asFile}", "Dex"))
    into("${layout.buildDirectory.get().asFile}/intermediates/dex/debug/mergeDexDebug")
    include("*.dex")
}

// 配置 dex 合并任务
tasks.whenTaskAdded {

    if (name == "packageRelease" || name == "packageDebug") {
        dependsOn("copyPrecompiledDex")
    }

}
*/
