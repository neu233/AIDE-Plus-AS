package io.github.zeroaicy.aide


/**
 * @author: 罪慾
 * @github: https://github.com/neu233/
 * @mail: 3115093767@qq.com
 * @createTime: 2024/11/3
 */
import android.content.Context
import android.preference.PreferenceManager
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

fun Context.unzipAssetsToFilesDirAsync(zipFileName: String) {
    if (!getPlatformDir().exists()) {
        CoroutineScope(Dispatchers.IO).launch {
            assets.open(zipFileName).use { inputStream ->
                unzip(inputStream, getPlatformDir())
            }
        }
    }
}


fun unzipAsync(fileInput: File, destinationDir: File) {
    if (!destinationDir.exists()) {
        destinationDir.mkdirs()
    }
    if (fileInput.exists()) {
        CoroutineScope(Dispatchers.IO).launch {
            fileInput.inputStream().use { inputStream ->
                unzip(inputStream, destinationDir)
            }
        }
    }
}


private suspend fun unzip(zipInputStream: InputStream, destinationDir: File) {
    withContext(Dispatchers.IO) {
        ZipInputStream(zipInputStream).use { zis ->
            var entry: ZipEntry?
            while (zis.nextEntry.also { entry = it } != null) {
                entry?.let { zipEntry ->
                    val entryFile = File(destinationDir, zipEntry.name)
                    if (zipEntry.isDirectory) {
                        entryFile.mkdirs()
                    } else {
                        entryFile.parentFile?.mkdirs()
                        FileOutputStream(entryFile).use { output ->
                            zis.copyTo(output)
                        }
                    }
                    zis.closeEntry()
                }
            }
        }
    }
}

fun Context.getAndroidJar(): File {
    val androidJar = File(noBackupFilesDir, ".aide/android.jar")

    CoroutineScope(Dispatchers.IO).launch {
        if (!androidJar.exists()) {
            androidJar.parentFile?.mkdirs()
            getAndroidJar().outputStream().use { output ->
                assets.open("android.jar").use { input ->
                    input.copyTo(output)
                }
            }
        }
    }

    return File(
        PreferenceManager.getDefaultSharedPreferences(this)
            .getString("user_androidjar", androidJar.absolutePath)!!
    )
}

fun String.toFile(): File {
    return File(this)
}


fun TextView.appendLineArg(vararg charSequence: String?){
    charSequence.iterator().forEach {
        append("\n")
        append(it)
    }

}

fun Context.getPlatformDir(): File {
    return File(filesDir, "sdk")
}
