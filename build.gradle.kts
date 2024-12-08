@file:Suppress("DEPRECATION")

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") apply false version "8.7.3"
    id("org.jetbrains.kotlin.android") apply false version "2.0.21"
    id("com.android.library") version "8.7.3" apply false
}


allprojects {
    //noinspection GrDeprecatedAPIUsage
    buildDir = file("${rootProject.rootDir.path}/build/${project.path.substring(1).replace(':', '-')}")
}
