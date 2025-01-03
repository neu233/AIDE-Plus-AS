@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")



pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
        maven("https://jitpack.io")
        maven("https://repo1.maven.org/maven2/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://dl.bintray.com/ppartisan/maven/")
        maven("https://clojars.org/repo/")
        maven("https://plugins.gradle.org/m2/")
        maven("https://www.jetbrains.com/intellij-repository/releases")
        maven("https://repo.eclipse.org/content/groups/releases/")
        maven("https://storage.googleapis.com/r8-releases/raw")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://dl.bintray.com/ppartisan/maven/")
        maven("https://clojars.org/repo/")
        maven("https://repo1.maven.org/maven2/")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
        maven("https://jitpack.io")
        maven("https://repo1.maven.org/maven2/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://dl.bintray.com/ppartisan/maven/")
        maven("https://clojars.org/repo/")
        maven("https://plugins.gradle.org/m2/")
        maven("https://www.jetbrains.com/intellij-repository/releases")
        maven("https://repo.eclipse.org/content/groups/releases/")
        maven("https://storage.googleapis.com/r8-releases/raw")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://dl.bintray.com/ppartisan/maven/")
        maven("https://clojars.org/repo/")
        maven("https://repo1.maven.org/maven2/")
    }
}

rootProject.name = "AIDE-Plus"
include(":app")

include(
    ":app_rewrite",
    ":appAideBase"
)


include(":compiletion:jaxp")

include(
    ":compiletion:Xml:aaptcompiler",
    ":compiletion:Xml:layoutlib-api",
    ":compiletion:Xml:res-parse"
)

include(
    ":compiletion:Kotlin:kotlinc",
    ":compiletion:Kotlin:the-unsafe"
)

include(
    ":termux:termux-shared",
    ":termux:terminal-emulator",
    ":termux:terminal-view",
    ":termux:termux-app",
)

include(
    ":Test:testTermuxApp",
    ":Test:testXmlApp"

)
