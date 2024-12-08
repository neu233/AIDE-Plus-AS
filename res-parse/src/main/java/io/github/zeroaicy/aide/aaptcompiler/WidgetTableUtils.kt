package io.github.zeroaicy.aide.aaptcompiler

import android.util.Log
import io.github.zeroaicy.aide.aaptcompiler.impl.widget.DefaultWidgetTable
import io.github.zeroaicy.aide.aaptcompiler.interfaces.widgets.WidgetTable
import java.io.File


/**
 * @author: 罪慾
 * @github: https://github.com/neu233/
 * @mail: 3115093767@qq.com
 * @createTime: 2024/10/30
 */


class WidgetTableUtils private constructor(val platform: File) {

    private var tables : WidgetTable?


    init {
        tables = createTable()
    }


    companion object {

        @JvmStatic
        fun getInstance(platform: File): WidgetTableUtils {
            return WidgetTableUtils(platform)
        }
    }

    fun getWidgetTable(): WidgetTable? {
        return tables
    }


    private fun createTable(): WidgetTable? {
        val widgets = File(platform, "data/widgets.txt")
        if (!widgets.exists() || !widgets.isFile) {
            Log.w(
                "'widgets.txt' file does not exist in {}/data directory",
                platform.absolutePath
            )
            return null
        }

        Log.i("Creating widget table for platform dir: {}", platform.absolutePath)


        return widgets.inputStream().bufferedReader().useLines {
            val table = DefaultWidgetTable()
            it.forEach { line ->
                table.putWidget(line)
            }
            table
        }
    }


}