package io.github.zeroaicy.aide.aaptcompiler

import java.io.OutputStreamWriter
import java.io.PrintWriter
import kotlin.io.path.absolutePathString
import kotlin.io.path.createTempFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.readText
import kotlin.io.path.writeText

/**
 * @author: 罪慾
 * @github: https://github.com/neu233/
 * @mail: 3115093767@qq.com
 * @createTime: 2024/11/1
 */

/*--style可以填"dropbox" or "google" or "kotlinlang"*/


/*
fun formatKotlinCode(
    code: String = "",
    style: String = "google"
): String {
    val file = createTempFile("file", ".kt").apply { writeText(code) }

    val args = listOf("--style", style, file.toAbsolutePath().toString())
    com.facebook.ktfmt.cli.Main(System.`in`, System.out, System.err, args.toTypedArray()).run()
    val formattedCode = file.readText()
    file.deleteIfExists()
    return formattedCode
}
*/


/**
 * googleJavaFormatOptions可以添加
 * --fix-imports-only
 * --skip-sorting-imports
 * --skip-removing-unused-imports
 * --skip-reflowing-long-strings
 * --skip-javadoc-formatting
 *
 * googleJavaFormatStyle可以填
 * "aosp" or "google"
 * */
/*

fun formatJavaCode(
    code: String = "",
    googleJavaFormatOptions: List<String> = mutableListOf("--skip-javadoc-formatting"),
    googleJavaFormatStyle: String = "aosp"
): String {
    val file = createTempFile("file", ".java")
    file.writeText(code)
    println("Formatting code...")
    println("Formatting with options: ${googleJavaFormatOptions}, style: ${googleJavaFormatStyle}")

    val args = mutableListOf<String>().apply {
        if (googleJavaFormatStyle == "aosp") {
            add("-a")
        }
        addAll(googleJavaFormatOptions)
        add("-r")
        add(file.absolutePathString())
    }

    com.google.googlejavaformat.java.Main(
        PrintWriter(OutputStreamWriter(System.out)),
        PrintWriter(OutputStreamWriter(System.out)),
        System.`in`
    ).format(*args.toTypedArray())

    val formattedCode = file.readText()
    file.deleteIfExists()

    return formattedCode
}

*/
