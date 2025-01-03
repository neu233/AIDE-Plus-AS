import java.io.ByteArrayOutputStream

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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        viewBinding = true
        //noinspection DataBindingWithoutKapt
        dataBinding = true
        buildConfig = false
    }

    lint {
        abortOnError = false
        baseline = file("lint-baseline.xml")
    }
}

dependencies {

    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    compileOnly(fileTree(mapOf("dir" to "provider", "include" to listOf("*.jar"))))

    // api(fileTree(mapOf("dir" to "libs-xml-completion", "include" to listOf("*.jar"))))

    api("androidx.core:core-ktx:1.15.0")
    api("androidx.preference:preference-ktx:1.2.1")
    api("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
    // r8 必须脱糖否则只能安卓13使用
    //api project(':kotlin')
    api(projects.appAideBase) {
        isTransitive = true
    }


    api(projects.compiletion.xml.resParse) {
        isTransitive = true
    }


    // https://mvnrepository.com/artifact/org.eclipse.jdt/ecj
    // api("org.eclipse.jdt:ecj:3.39.0:sources")
    api("org.eclipse.jdt:ecj:3.39.0")

    api("net.margaritov.preference.colorpicker.ColorPickerPreference:ColorPickerPreference:1.0.0")


    // xml-completion
    /*
    api("com.google.guava:guava:33.2.1-android")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.24")
    api("androidx.annotation:annotation:1.9.1")
    api("androidx.collection:collection-ktx:1.4.5")
    api("org.jetbrains:annotations:24.1.0")
    api("com.android.tools:annotations:31.7.3")
    api("com.android.tools:common:31.7.3")
    api("com.android.tools.build:aapt2-proto:8.3.2-10880808")
    api("com.google.protobuf:protobuf-java:3.25.3")

    */

    // jgit 7.0.0.202409031743-r
    api("org.eclipse.jgit:org.eclipse.jgit:7.0.0.202409031743-r")

    // https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk18on
    implementation("org.bouncycastle:bcprov-jdk18on:1.79")

    // java-formatter依赖库 后期与java-formatter合并
    api("org.eclipse.platform:org.eclipse.text:3.14.100")
    compileOnly("org.osgi:org.osgi.framework:1.10.0")

    //material和androidx库
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.13.0-alpha09")

    //导航栏工具
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.5")
    //多语言工具
    implementation("com.github.getActivity:MultiLanguages:9.3")
    //gson
    implementation("com.google.code.gson:gson:2.11.0")
    // okhttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    //工具库
    implementation("com.blankj:utilcodex:1.31.1")
    // svg库
    implementation("com.github.megatronking:svg-support:1.3.2")
    implementation("com.romainpiel.svgtoandroid:svgtoandroid:0.1.0")
    // 权限库
    implementation("com.github.getActivity:XXPermissions:20.0")
    // brv比较强大的一个recycler的框架
    implementation("com.github.liangjingkanji:BRV:1.5.8")

    implementation(platform("io.github.Rosemoe.sora-editor:bom:0.23.5"))
    implementation("io.github.Rosemoe.sora-editor:editor")

    /*
    implementation("io.github.Rosemoe.sora-editor:editor-lsp")
    implementation("io.github.Rosemoe.sora-editor:language-java")
    implementation("io.github.Rosemoe.sora-editor:language-textmate")
    implementation("io.github.Rosemoe.sora-editor:language-treesitter")*/


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
