import com.android.build.api.dsl.AndroidResources
import com.android.tools.build.apkzlib.zip.ZFile
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {

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

    namespace = "io.github.zeroaicy.aide"
    compileSdk = 35

    val gitCommitHash: String by lazy {
        @Suppress("DEPRECATION")
        Runtime.getRuntime()
            .exec("git rev-parse HEAD")
            .inputStream
            .bufferedReader()
            .use { it.readText().trim() }
    }

    val getCommitHash: () -> String = {
        val stdout = ByteArrayOutputStream()
        exec {
            commandLine("git", "rev-parse", "--short", "HEAD")
            standardOutput = stdout
        }
        stdout.toString().trim()
    }

    defaultConfig {
        applicationId = "io.github.zeroaicy.aide"
        minSdk = 24
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 28
        //noinspection HighAppVersionCode
        versionCode = 2008210017
        // [3.2.210316]
        versionName = "2.3.2.9-${getCommitHash()}"

        buildConfigField("String", "GIT_HASH", "\"${gitCommitHash}\"")

        //multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions.add("api")

    productFlavors {
        create("default") {

            dimension = "api"
            versionNameSuffix = ""
            isDefault = true
            signingConfig = signingConfigs.getByName("debug1")
            resourcePrefix = "default_"

            androidResources {
                val publicXmlFile =
                    project.rootProject.file("${project(":appAideBase").projectDir.path}/src/main/res/values/public.xml")
                val publicTxtFile =
                    project.rootProject.file("${layout.buildDirectory.asFile.get().path}/default_public.txt")
                val applicationId = "io.github.zeroaicy.aide"
                createPublicTxt(
                    packageName = applicationId,
                    publicXmlFile = publicXmlFile,
                    publicTxtFile = publicTxtFile
                )
            }
        }

        create("termux") {

            dimension = "api"
            versionNameSuffix = "-termux"
            applicationId = "io.github.zeroaicy.aide2"
            resourcePrefix = "termux_"
            signingConfig = signingConfigs.getByName("debug1")

            dependencies {
                api(projects.termux.termuxApp) // termux
            }

            androidResources {
                val publicXmlFile =
                    project.rootProject.file("${project(":appAideBase").projectDir.path}/src/main/res/values/public.xml")
                val publicTxtFile =
                    project.rootProject.file("${layout.buildDirectory.asFile.get().path}/termux_public.txt")
                val applicationId = "io.github.zeroaicy.aide2"
                createPublicTxt(
                    packageName = applicationId,
                    publicXmlFile = publicXmlFile,
                    publicTxtFile = publicTxtFile
                )
            }

        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug1")
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }


    kotlinOptions {
        jvmTarget = "21"
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


            pickFirsts += "*/*/*/*/*/*/*/*/*/*/*/*/*/*/*"
            pickFirsts += "*/*/*/*/*/*/*/*/*/*/*/*/*/*"
            pickFirsts += "*/*/*/*/*/*/*/*/*/*/*/*/*"
            pickFirsts += "*/*/*/*/*/*/*/*/*/*//*/*"
            pickFirsts += "*/*/*/*/*/*/*/*/*/*/*/*"
            pickFirsts += "*/*/*/*/*/*/*/*/*/*/*"
            pickFirsts += "*/*/*/*/*/*/*/*/*/*"
            pickFirsts += "*/*/*/*/*/*/*/*/*"
            pickFirsts += "*/*/*/*/*/*/*/*"
            pickFirsts += "*/*/*/*/*/*/*"
            pickFirsts += "*/*/*/*/*/*"
            pickFirsts += "*/*/*/*/*"
            pickFirsts += "*/*/*/*"
            pickFirsts += "*/*/*"
            pickFirsts += "*/*"
            pickFirsts += "/*"


            pickFirsts += "about_files/*"

        }
        jniLibs {
            useLegacyPackaging = true
            pickFirsts += "/lib/*/*"
        }
    }


    buildFeatures {
        buildConfig = true
        viewBinding = true
        renderScript = false
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
    implementation("com.google.android.material:material:1.13.0-alpha09")

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
    exclude("org.bouncycastle", "bcprov-jdk15on")
}


val isRelease: Boolean = gradle.startParameter.taskNames.any { taskName ->
    taskName.contains("Release", ignoreCase = true)
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}


configurations.all {
    resolutionStrategy {
        // 对冲突的依赖直接使用最新版本
        //force("")
        //failOnVersionConflict()

    }
}




afterEvaluate {

    tasks.register("launchApp") {
        doLast {
            // 定义包名和Activity
            val packageName = "io.github.zeroaicy.aide"
            val activityName = "io.github.zeroaicy.aide.activity.HomeActivity"
            // 执行adb命令
            exec {
                commandLine(
                    project.android.adbExecutable.absolutePath,
                    "shell",
                    "am",
                    "start",
                    "-n",
                    "$packageName/$activityName"
                )
            }
        }
    }


    var taskDefaultName: String?

    tasks.configureEach {
        if (name.startsWith("install")) {
            finalizedBy("launchApp")
        } else if (name.contains("mergeDex")) {
            taskDefaultName = name.removePrefix("mergeDex")

            doLast {
                val buildDir = project.layout.buildDirectory.asFile.get().path
                val dexFolder = project.layout.projectDirectory.file("Dex")

                val optimizeAp = Paths.get(
                    buildDir,
                    "intermediates",
                    "optimized_processed_res",
                    taskDefaultName!!,
                    "optimize${taskDefaultName}Resources",
                    "resources-${taskDefaultName!!.camelToKebab()}-optimize.ap_"
                )

                println("optimize.ap_ file in ${optimizeAp.toFile().absolutePath}")


                val appDexCount: () -> Int = {
                    val dexDir = Paths.get(
                        project.layout.buildDirectory.asFile.get().path,
                        "intermediates",
                        "dex",
                        taskDefaultName!!,
                        "mergeDex${taskDefaultName}"
                    )
                    if (!Files.exists(dexDir)) 0
                    else {
                        Files.list(dexDir).use { it.count().toInt() }
                    }
                }

                println("app_dex_count: ${appDexCount()}")

                check(appDexCount() != 0) { "Unexpected app dex count" }
                var dexCount = appDexCount() + 2

                try {
                    ZFile.openReadWrite(optimizeAp.toFile()).use { zip ->
                        dexFolder.asFile.listFiles()?.forEach {
                            /// 不允许先添加 AIDE+_2.3.dex
                            println("add dex to optimize.ap_ file ${it.absolutePath} rename to classes${dexCount}.dex")
                            if (it.name.endsWith(".dex") && it.name != "AIDE+_2.3.dex") {
                                val dexName = "classes${dexCount}.dex"
                                zip.add(dexName, it.inputStream())
                                dexCount++
                            }
                        }
                        val inputStream = project
                            .layout
                            .projectDirectory
                            .file("Dex/AIDE+_2.3.dex")
                            .asFile
                            .inputStream()
                        val dexName = "classes${dexCount}.dex"
                        zip.add(dexName, inputStream)
                    }
                } catch (e: Exception) {
                    throw e
                }
            }
        }

    }
}


fun String.camelToKebab(): String {
    val kebab = this.replace(Regex("([a-z])([A-Z])"), "$1-$2").lowercase()
    return kebab.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}


fun AndroidResources.createPublicTxt(
    packageName: String,
    publicXmlFile : File,
    publicTxtFile : File,
) {
    // 创建父目录并确保 publicTxtFile 存在
    publicTxtFile.parentFile?.mkdirs()
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
        publicTxtFile.appendText("$packageName:$type/$name = $id\n")
    }

    // 添加稳定 ID 参数
    @Suppress("DEPRECATION")
    additionalParameters("--stable-ids", publicTxtFile.path)
}