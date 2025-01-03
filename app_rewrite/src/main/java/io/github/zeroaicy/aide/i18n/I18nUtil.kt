package io.github.zeroaicy.aide.i18n

import android.os.Environment
import androidx.annotation.StringRes
import io.github.zeroaicy.util.ContextUtil
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File


/*
author : 罪慾
date : 2025/1/2 14:51
description : QQ3115093767
*/


const val I18N = ".aide/localization"
const val I18NJsonFileName = "strings.json"
const val I18NXmlFileName = "strings.xml"

private val hashMap = HashMap<String, String>()

private val context = ContextUtil.getApplication()

private val defaultI18nFile = File(getAbsolutePathOf("$I18N/zh-rCN/$I18NXmlFileName"))

fun getAbsolutePathOf(path: String): String {
    return File(Environment.getExternalStorageDirectory(), path).absolutePath
}

fun getString(@StringRes id: Int): String {
    val resourceEntryName: String = context.resources.getResourceEntryName(id)
    if (hashMap.isEmpty()) {
        initI18nXml("zh-rCN")
    }
    return try {
        if (!hashMap.containsKey(resourceEntryName) || hashMap[resourceEntryName].isNullOrEmpty()) {
            context.resources.getString(id)
        } else {
            hashMap[resourceEntryName]!!
                .replace("\\'", "'")
                .replace("\\\"", "\"")
                .replace("\\n", "\n")
        }
    } catch (unused: Exception) {
        context.resources.getString(id)
    }
}


fun getString(@StringRes id: Int, vararg formatArgs: Any): String {
    return String.format(getString(id), formatArgs)
}

fun initI18nXml(
    language: String,
) {
    defaultI18nFile.parentFile?.mkdirs()
    defaultI18nFile.createNewFile()

    val i18nFile = getAbsolutePathOf("$I18N/$language/$I18NXmlFileName")
    File(i18nFile).let {
        if (!it.exists()) defaultI18nFile else it
    }.inputStream().reader().use {
        val newPullParser = XmlPullParserFactory.newInstance().newPullParser()
        newPullParser.setInput(it)
        while (true) {
            val eventType = newPullParser.eventType
            if (eventType == XmlPullParser.END_DOCUMENT) break
            if (eventType == XmlPullParser.START_TAG && newPullParser.name == "string") {
                hashMap[newPullParser.getAttributeValue(null, "name")] = newPullParser.nextText()
            }
            newPullParser.next()
        }
    }
}