<p align="center">
  <img src="assets/Icon.svg" style="width: 30%;" />
</p>

# AIDE-Plus

[![GitHub contributors](https://img.shields.io/github/contributors/AndroidIDE-CN/AIDE-Plus)](https://github.com/AndroidIDE-CN/AIDE-Plus/graphs/contributors)
[![GitHub last commit](https://img.shields.io/github/last-commit/AndroidIDE-CN/AIDE-Plus)](https://github.com/AndroidIDE-CN/AIDE-Plus/commits/)
[![Total downloads](https://img.shields.io/github/downloads/AndroidIDE-CN/AIDE-Plus/total)](https://github.com/AndroidIDE-CN/AIDE-Plus/releases)
[![Repository Size](https://img.shields.io/github/repo-size/AndroidIDE-CN/AIDE-Plus)](https://github.com/AndroidIDE-CN/AIDE-Plus)

- [中文版 README](README_zh.md)

### Implemented Features
- [x] Build service optimization and rewrite
- [x] Replaced aapt with aapt2
- [x] Replaced dx with D8
- [x] Implementation of some Java 8 syntax (default syntax analysis)
- [x] Implementation of some Java 9 syntax (default syntax analysis)
- [x] Implementation of some Java 11 syntax (default syntax analysis)
- [x] Java 23 compilation (implemented through ecj, needs to be enabled in settings)
- [x] High version Java formatting (implemented through ecj, needs to be enabled in settings)
- [x] Custom class parser to implement high version class features
- [x] Implemented `runtimeOnly`, `compileOnly`, `libgdxNatives`
- [x] Rewritten Gradle parser
- [x] New Maven downloader (bom supported)
- [x] Application cold start optimization
- [x] Added more syntax highlighting
- [x] Using D8 for obfuscation
- [x] Replaced AIDE default framework with Androidx
- [x] Restored some of AIDE's dex obfuscation
- [x] Custom code colors
- [x] ViewBinding support
- [x] Manifest merger tool update
- [x] Fixed silent installation and support for more installers (shizuku)
- [x] New UI implementation
- [x] Fixed completion and highlighting loss issues
- [x] Fixed signature creation issues
- [x] ApkSign now supports v1-v4 signatures
- [x] Added some basic syntax completion
- [x] Fixed git issues within the software
- [x] APK resource alignment
- [x] Java projects support Android API
- [x] Partially implemented log loss recovery

### Planned Features
- [x] Lambda implementation (ecj)
- [ ] More completions (LSP)
- [ ] CMake build support (Java projects)
- [ ] Apks/AAB generation (not added)
- [ ] Manifest file editing
- [ ] Vector graphics acquisition

> [!TIP]
> Don't use debug mode, otherwise dex won't be added to the apk

## Building Non-Termux Version
```shell
chmod +x ./gradlew
./gradlew :app:assembleDefault
```
Test run
```shell
chmod +x ./gradlew
./gradlew :app:installDefaultRelease
```

## Building Termux Version
```shell
chmod +x ./gradlew
./gradlew :app:assembleTermux
```
Test run
```shell
chmod +x ./gradlew
./gradlew :app:installTermuxRelease
```

## Related resources
- [AIDE-Ndk-Install](https://github.com/ZeroAicy/AIDE-Ndk-Install)
- [AIDE-Repair](https://github.com/ZeroAicy/AIDE-Repair)
- [Cmake-Build]()

# Related Information
- QQ Groups
  * [487145957](https://qm.qq.com/q/W0WJq5qne2)
  * [1002980489](https://qm.qq.com/q/W0WJq5qne2)
- [QQ Channel](https://pd.qq.com/s/auq589py2)
- [Website](https://plus.androidide.cn)

# Special Thanks
