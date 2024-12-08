package io.github.zeroaicy.aide.aaptcompiler

import android.util.Log
import com.android.SdkConstants
import com.android.SdkConstants.FN_INTENT_ACTIONS_ACTIVITY
import com.android.SdkConstants.FN_INTENT_ACTIONS_BROADCAST
import com.android.SdkConstants.FN_INTENT_ACTIONS_SERVICE
import com.android.SdkConstants.FN_INTENT_CATEGORIES
import com.android.SdkConstants.OS_PLATFORM_ATTRS_MANIFEST_XML
import com.android.aaptcompiler.AaptResourceType
import com.android.aaptcompiler.BlameLogger
import com.android.aaptcompiler.ConfigDescription
import com.android.aaptcompiler.IDELogger
import com.android.aaptcompiler.ResourceName
import com.android.aaptcompiler.ResourceTable
import com.android.aaptcompiler.Source
import com.android.aaptcompiler.TableExtractor
import com.android.aaptcompiler.TableExtractorOptions
import com.android.aaptcompiler.extractPathData
import io.github.zeroaicy.aide.layoutlib.resources.ResourceVisibility.PUBLIC
import java.io.File
import java.util.concurrent.ConcurrentHashMap


/**
 * @author: 罪慾
 * @github: https://github.com/neu233/
 * @mail: 3115093767@qq.com
 * @createTime: 2024/10/30
 */

const val PCK_ANDROID = "android"
const val PCK_APP = "app"

const val NAMESPACE_PREFIX = "http://schemas.android.com/apk/res/"
const val NAMESPACE_AUTO = "http://schemas.android.com/apk/res-auto"

const val MANIFEST_TAG_PREFIX = "AndroidManifest"


class ResourceUtils private constructor(val platform: File) {

    private val tables = ConcurrentHashMap<String, ResourceTable>()
    private val platformTables = ConcurrentHashMap<String, ResourceTable>()
    private val manifestAttrs = ConcurrentHashMap<String, ResourceTable>()
    private val singleLineValueEntries =
        ConcurrentHashMap<String, ConcurrentHashMap<SingleLineValueEntryType, List<String>>>()

    internal enum class SingleLineValueEntryType(val filename: String) {

        ACTIVITY_ACTIONS(FN_INTENT_ACTIONS_ACTIVITY),
        BROADCAST_ACTIONS(FN_INTENT_ACTIONS_BROADCAST),
        SERVICE_ACTIONS(FN_INTENT_ACTIONS_SERVICE),
        CATEGORIES(FN_INTENT_CATEGORIES),
        FEATURES("features.txt")
    }

    init {
        COMPLETION_MANIFEST_ATTR_RES = getManifestAttrTable()
        COMPLETION_FRAMEWORK_RES = getFrameworkResourceTable()
        getFeatures()
        getCategories()
        getServiceActions()
        getActivityActions()
        getBroadcastActions()

    }

    companion object {


        @JvmStatic
        fun getInstance(platform: File): ResourceUtils {
            return ResourceUtils(platform)
        }

        @JvmStatic
        val COMPLETION_MODULE_RES = mutableSetOf<ResourceTable>()

        @JvmStatic
        var COMPLETION_FRAMEWORK_RES: ResourceTable? = null

        @JvmStatic
        var COMPLETION_MANIFEST_ATTR_RES: ResourceTable? = null

    }

    fun forPackage(name: String, vararg resDirs: File): ResourceTable? {
        if (name == PCK_ANDROID) {
            return platformResourceTable(resDirs.iterator().next())
        }
        val resourceTable = tables[name]
            ?: createTable(*resDirs)?.also {
                tables[name] = it
                it.packages.firstOrNull()?.name = name

                resDirs.forEach { resDir ->
                    addFileReferences(it, name, resDir)
                }
            }
        if (resourceTable != null) {
            if (!COMPLETION_MODULE_RES.contains(resourceTable)) {
                COMPLETION_MODULE_RES.add(resourceTable)
            }
        }
        return resourceTable
    }


    fun removeTable(packageName: String) {
        tables.remove(packageName)
    }

    fun clear() {
        tables.clear()
    }

    fun getFrameworkResourceTable(): ResourceTable? {
        return forPackage(PCK_ANDROID, File(platform, "data/res"))
    }

    fun getManifestAttrTable(): ResourceTable? {
        return manifestAttrs[platform.path]
            ?: createManifestAttrTable(platform)?.also {
                manifestAttrs[platform.path] = it
                it.packages.firstOrNull()?.name = PCK_ANDROID
            }
    }

    fun getActivityActions(): List<String> {
        return getSingleLineEntry(SingleLineValueEntryType.ACTIVITY_ACTIONS)
    }

    fun getBroadcastActions(): List<String> {
        return getSingleLineEntry(SingleLineValueEntryType.BROADCAST_ACTIONS)
    }

    fun getServiceActions(): List<String> {
        return getSingleLineEntry(SingleLineValueEntryType.SERVICE_ACTIONS)
    }

    fun getCategories(): List<String> {
        return getSingleLineEntry(SingleLineValueEntryType.CATEGORIES)
    }

    fun getFeatures(): List<String> {
        return getSingleLineEntry(SingleLineValueEntryType.FEATURES)
    }


    private fun getSingleLineEntry(
        type: SingleLineValueEntryType
    ): List<String> {
        var entries = singleLineValueEntries[platform.path]
        if (entries == null) {
            entries = readSingleLineEntry(type)
            singleLineValueEntries[platform.path] = entries
        }

        return entries[type]
            ?: run {
                readSingleLineEntriesTo(type, entries)
                entries[type] ?: emptyList()
            }
    }

    private fun readSingleLineEntry(
        type: SingleLineValueEntryType
    ): ConcurrentHashMap<SingleLineValueEntryType, List<String>> {
        val map = ConcurrentHashMap<SingleLineValueEntryType, List<String>>()
        readSingleLineEntriesTo(type, map)
        return map
    }

    private fun readSingleLineEntriesTo(
        type: SingleLineValueEntryType,
        map: ConcurrentHashMap<SingleLineValueEntryType, List<String>>
    ) {
        val file = File(platform, "${SdkConstants.FD_DATA}/${type.filename}")
        if (!file.exists() || !file.canRead()) {
            return
        }

        map[type] = file.readLines()
    }


    private fun createManifestAttrTable(platform: File): ResourceTable? {
        val attrs = File(platform, OS_PLATFORM_ATTRS_MANIFEST_XML)
        if (!attrs.exists()) {
            return null
        }

        val logger = BlameLogger(IDELogger)
        val table = ResourceTable(logger = logger)
        val options = getDefaultOptions()
        extractTable(attrs, table, options, logger)

        return table
    }

    private fun getDefaultOptions(): TableExtractorOptions {
        return TableExtractorOptions(
            translatable = true, errorOnPositionalArgs = false,
            visibility = PUBLIC
        )
    }

    private fun extractTable(
        file: File,
        table: ResourceTable,
        options: TableExtractorOptions,
        logger: BlameLogger
    ) {
        val pathData = extractPathData(file)
        if (pathData.extension != "xml") {
            // Cannot parse any other file types
            return
        }

        val extractor =
            TableExtractor(
                table = table,
                source = pathData.source,
                config = pathData.config,
                options = options,
                logger = logger
            )

        pathData.file.inputStream().use { stream ->
            try {
                extractor.extract(stream)
            } catch (err: Exception) {
                Log.w("Failed to compile {}", pathData.file.absolutePath, err)
            }
        }
    }

    private fun platformResourceTable(dir: File): ResourceTable? {
        return platformTables[dir.path]
            ?: createTable(dir)?.also { table ->
                platformTables[dir.path] = table
                table.packages.firstOrNull()?.name = PCK_ANDROID

                addFileReferences(table, PCK_ANDROID, dir)
            }
    }

    private fun createTable(vararg resDirs: File): ResourceTable? {
        if (resDirs.isEmpty()) {
            return null
        }
        Log.i("Creating resource table for {} resource directories", resDirs.size.toString())
        val logger = BlameLogger(IDELogger)
        val table = ResourceTable()
        val options = getDefaultOptions()

        for (resDir in resDirs) {
            val values = File(resDir, "values")
            if (!values.exists() || !values.isDirectory) {
                continue
            }
            updateFromDirectory(values, table, options, logger)
        }

        return table
    }

    private fun addFileReferences(table: ResourceTable, pck: String, resDir: File) {
        resDir.listFiles()?.forEach { dir ->
            if (dir.name.startsWith(SdkConstants.FD_RES_VALUES)) {
                return@forEach
            }

            dir.listFiles()?.forEach { file ->
                var typeName = dir.name
                if (typeName.contains('-')) {
                    typeName = typeName.substringBefore('-')
                }

                val type = try {
                    AaptResourceType.valueOf(typeName.uppercase())
                } catch (error: Exception) {
                    Log.w("Unknown resource type: {} :: {}", typeName.uppercase(), error)
                    AaptResourceType.UNKNOWN
                }
                val resName = ResourceName(pck, type, file.nameWithoutExtension)
                table.addFileReference(resName, ConfigDescription(), Source(file.path), file.path)
            }
        }
    }

    private fun updateFromDirectory(
        directory: File,
        table: ResourceTable,
        options: TableExtractorOptions,
        logger: BlameLogger = BlameLogger(IDELogger)
    ) {
        directory.listFiles()?.forEach {
            if (it.isDirectory || it.extension != "xml") {
                return@forEach
            }

            updateFromFile(it, table, options, logger)
        }
    }

    private fun updateFromFile(
        it: File,
        table: ResourceTable,
        options: TableExtractorOptions,
        logger: BlameLogger
    ) {
        if (it.path.endsWith(OS_PLATFORM_ATTRS_MANIFEST_XML)) {
            // This is stored in another resource table
            return
        }
        extractTable(it, table, options, logger)
        return
    }


}


