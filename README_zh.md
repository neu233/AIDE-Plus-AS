<p align="center">
  <img src="assets/Icon.svg" style="width: 30%;" />
</p>


# AIDE-Plus

[![GitHub contributors](https://img.shields.io/github/contributors/AndroidIDE-CN/AIDE-Plus)](https://github.com/AndroidIDE-CN/AIDE-Plus/graphs/contributors)
[![GitHub last commit](https://img.shields.io/github/last-commit/AndroidIDE-CN/AIDE-Plus)](https://github.com/AndroidIDE-CN/AIDE-Plus/commits/)
[![Total downloads](https://img.shields.io/github/downloads/AndroidIDE-CN/AIDE-Plus/total)](https://github.com/AndroidIDE-CN/AIDE-Plus/releases)
[![Repository Size](https://img.shields.io/github/repo-size/AndroidIDE-CN/AIDE-Plus)](https://github.com/AndroidIDE-CN/AIDE-Plus)


- [README of English](README.md)


## 功能

### 已实现功能
- [x] 构建服务优化和重写
- [x] aapt更替为aapt2
- [x] dx更替为D8
- [x] 一些Java8语法的实现(默认语法分析)
- [x] 一些Java9语法的实现(默认语法分析)
- [x] 一些Java11语法的实现(默认语法分析)
- [x] Java23编译(通过ecj实现，需要设置开启)
- [x] Java高版本格式化(通过ecj实现，需要设置开启)
- [x] 自定义class解析器，以实现高版本的class特性
- [x] 实现了`runtimeOnly`，`compileOnly`，`libgdxNatives`
- [x] 重写Gradle解析器
- [x] 新的Maven下载器(bom已支持)
- [x] 应用冷启动优化
- [x] 添加更多语法高亮
- [x] 使用D8进行混淆
- [x] AIDE默认框架替换为Androidx
- [x] 还原了部分AIDE的dex混淆
- [x] 代码自定义颜色
- [x] ViewBinding支持
- [x] 清单合并工具更新
- [x] 修复静默安装和支持更多安装器(shizuku)
- [x] 新UI的实现
- [x] 修复了补全和高亮丢失问题
- [x] 修复了创建签名的问题
- [x] ApkSign支持了的v1-v4签名
- [x] 新增一些基础的语法补全
- [x] 修复软件内的git问题
- [x] apk资源对齐

### 计划实现
- [x] Lambda实现 (ecj)
- [ ] 更多补全 (Lsp)
- [ ] 支持cmake构建(Java项目)
- [ ] Apks/AAB的生成(未添加)

> [!TIP]
> 不要使用debug进行，不然dex没有添加进apk

## 构建无Termux版本
```shell
chmod +x ./gradlew
./gradlew :app:assembleDefault
```
运行测试
```shell
chmod +x ./gradlew
./gradlew :app:installDefaultRelease
```

## 构建Termux版本
```shell
chmod +x ./gradlew
./gradlew :app:assembleTermux
```
运行测试
```shell
chmod +x ./gradlew
./gradlew :app:installTermuxRelease
```






# 相关信息
- QQ群
  * [487145957](https://qm.qq.com/q/W0WJq5qne2)
  * [1002980489](https://qm.qq.com/q/W0WJq5qne2) 
- [QQ频道](https://pd.qq.com/s/auq589py2)
- [网站](https://plus.androidide.cn)

# 特别感谢


