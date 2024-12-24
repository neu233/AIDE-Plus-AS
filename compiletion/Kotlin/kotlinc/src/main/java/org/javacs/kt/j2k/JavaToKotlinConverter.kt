/*
package org.javacs.kt.j2k

import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiFileFactory
import org.javacs.kt.LOG
import org.javacs.kt.util.LoggingMessageCollector
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.jvm.compiler.CliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.cli.jvm.config.addJavaSourceRoots
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.cli.jvm.config.configureJdkClasspathRoots
import org.jetbrains.kotlin.config.ApiVersion
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmProtoBufUtil
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.BindingTraceContext
import org.jetbrains.kotlin.resolve.lazy.declarations.FileBasedDeclarationProviderFactory
import java.io.Closeable
import java.io.File
import java.nio.file.Path

private fun defaultCompileEnvironment(
    javaSourcePath: Set<Path>,
    classPath: Set<Path>
) = CompilationEnvironment(javaSourcePath, classPath)

fun convertJavaToKotlin(
    javaSourcePath: Set<Path>,
    classPath: Set<Path>,
    javaCode: String
): String {


    val psiFactory = PsiFileFactory.getInstance(
        defaultCompileEnvironment(
            javaSourcePath,
            classPath
        ).environment.project
    )

    //val psiFactory = compiler.psiFileFactoryFor(CompilationKind.DEFAULT)
    val javaAST = psiFactory.createFileFromText("snippet.java", JavaLanguage.INSTANCE, javaCode)
    LOG.info("Parsed {} to {}", javaCode, javaAST)

    return JavaElementConverter().also(javaAST::accept).translatedKotlinCode ?: run {
        LOG.warn("Could not translate code")
        ""
    }


}

class CompilationEnvironment(
    javaSourcePath: Set<Path>,
    classPath: Set<Path>
) : Closeable {

    private val disposable = Disposer.newDisposable()

    val environment: KotlinCoreEnvironment
    val parser: KtPsiFactory

    init {

        environment = KotlinCoreEnvironment.createForProduction(
            parentDisposable = disposable,
            configuration = CompilerConfiguration().apply {
                val langFeatures = mutableMapOf<LanguageFeature, LanguageFeature.State>()
                for (langFeature in LanguageFeature.values()) {
                    langFeatures[langFeature] = LanguageFeature.State.ENABLED
                }
                val languageVersionSettings = LanguageVersionSettingsImpl(
                    LanguageVersion.LATEST_STABLE,
                    ApiVersion.createByLanguageVersion(LanguageVersion.LATEST_STABLE),
                    emptyMap(),
                    langFeatures
                )
                put(CommonConfigurationKeys.MODULE_NAME, JvmProtoBufUtil.DEFAULT_MODULE_NAME)
                put(CommonConfigurationKeys.LANGUAGE_VERSION_SETTINGS, languageVersionSettings)
                put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, LoggingMessageCollector)
                put(JVMConfigurationKeys.USE_PSI_CLASS_FILES_READING, true)

                // configure jvm runtime classpaths
                configureJdkClasspathRoots()

                // Kotlin 1.8.20 requires us to specify the JDK home, otherwise java.* classes won't resolve
                // See https://github.com/JetBrains/kotlin-compiler-server/pull/626
                val jdkHome = File(System.getProperty("java.home")!!)
                put(JVMConfigurationKeys.JDK_HOME, jdkHome)

                addJvmClasspathRoots(classPath.map { it.toFile() })
                addJavaSourceRoots(javaSourcePath.map { it.toFile() })


            },
            configFiles = EnvironmentConfigFiles.JVM_CONFIG_FILES
        )
        val project = environment.project
        parser = KtPsiFactory(project)
    }

    fun updateConfiguration(config: KotlinCompilerConfiguration) {
        JvmTarget.fromString(config.jvm.target)
            ?.let { environment.configuration.put(JVMConfigurationKeys.JVM_TARGET, it) }
    }

    fun createContainer(sourcePath: Collection<KtFile>): Pair<ComponentProvider, BindingTraceContext> {
        val trace = CliBindingTrace()
        val container = TopDownAnalyzerFacadeForJVM.createContainer(
            project = environment.project,
            files = sourcePath,
            trace = trace,
            configuration = environment.configuration,
            packagePartProvider = environment::createPackagePartProvider,
            // TODO FileBasedDeclarationProviderFactory keeps indices, re-use it across calls
            declarationProviderFactory = ::FileBasedDeclarationProviderFactory
        )
        return Pair(container, trace)
    }


    override fun close() {
        Disposer.dispose(disposable)
    }


}

public data class KotlinCompilerConfiguration(
    val jvm: JVMConfiguration = JVMConfiguration()
)


public data class JVMConfiguration(
    */
/** Which JVM target the Kotlin compiler uses. See Compiler.jvmTargetFrom for possible values. *//*

    var target: String = "default"
)*/
