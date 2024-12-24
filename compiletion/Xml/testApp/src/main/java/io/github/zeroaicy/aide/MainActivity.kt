package io.github.zeroaicy.aide

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.SdkConstants
import com.android.aapt.Resources.Attribute.FormatFlags
import com.android.aapt.Resources.Attribute.FormatFlags.BOOLEAN
import com.android.aapt.Resources.Attribute.FormatFlags.COLOR
import com.android.aapt.Resources.Attribute.FormatFlags.DIMENSION
import com.android.aapt.Resources.Attribute.FormatFlags.ENUM
import com.android.aapt.Resources.Attribute.FormatFlags.FLAGS
import com.android.aapt.Resources.Attribute.FormatFlags.INTEGER
import com.android.aapt.Resources.Attribute.FormatFlags.REFERENCE
import com.android.aapt.Resources.Attribute.FormatFlags.STRING
import com.android.aaptcompiler.AaptResourceType
import com.android.aaptcompiler.AaptResourceType.BOOL
import com.android.aaptcompiler.AaptResourceType.DIMEN
import com.android.aaptcompiler.AttributeResource
import com.android.aaptcompiler.ConfigDescription
import com.android.aaptcompiler.ResourceGroup
import com.android.aaptcompiler.ResourceTable
import com.android.aaptcompiler.ResourceTablePackage
import com.android.aaptcompiler.Styleable
import com.android.aaptcompiler.findEntries
import io.github.zeroaicy.aide.aaptcompiler.ApiVersionsUtils
import io.github.zeroaicy.aide.aaptcompiler.JavaViewUtils
import io.github.zeroaicy.aide.aaptcompiler.NAMESPACE_AUTO
import io.github.zeroaicy.aide.aaptcompiler.ResourceUtils
import io.github.zeroaicy.aide.aaptcompiler.WidgetTableUtils
import io.github.zeroaicy.aide.aaptcompiler.interfaces.widgets.Widget
import io.github.zeroaicy.aide.aaptcompiler.interfaces.widgets.WidgetTable
import io.github.zeroaicy.aide.aaptcompiler.utils.attrValue_qualifiedRef
import io.github.zeroaicy.aide.aaptcompiler.utils.attrValue_qualifiedRefWithIncompletePckOrType
import io.github.zeroaicy.aide.aaptcompiler.utils.attrValue_qualifiedRefWithIncompleteType
import io.github.zeroaicy.aide.aaptcompiler.utils.attrValue_unqualifiedRef
import io.github.zeroaicy.aide.databinding.ActivityAttrBinding
import io.github.zeroaicy.aide.databinding.ActivityMainBinding
import io.github.zeroaicy.aide.databinding.ActivityTagBinding
import io.github.zeroaicy.aide.databinding.ActivityValueBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var activityAttrBinding: ActivityAttrBinding
    private lateinit var activityTagBinding: ActivityTagBinding
    private lateinit var activityValueBinding: ActivityValueBinding
    private lateinit var resourceUtil: ResourceUtils
    private lateinit var apiVersionsUtil: ApiVersionsUtils
    private lateinit var widgetTableUtil: WidgetTableUtils
    private lateinit var javaViewUtils: JavaViewUtils


    val dimensionUnits =
        arrayOf(
            SdkConstants.UNIT_DP,
            SdkConstants.UNIT_SP,
            SdkConstants.UNIT_PX,
            SdkConstants.UNIT_IN,
            SdkConstants.UNIT_MM,
            SdkConstants.UNIT_PT
        )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        activityAttrBinding = ActivityAttrBinding.inflate(layoutInflater)
        activityTagBinding = ActivityTagBinding.inflate(layoutInflater)
        activityValueBinding = ActivityValueBinding.inflate(layoutInflater)

        resourceUtil = ResourceUtils.getInstance(getPlatformDir());
        apiVersionsUtil = ApiVersionsUtils.getInstance(getPlatformDir());
        widgetTableUtil = WidgetTableUtils.getInstance(getPlatformDir());
        javaViewUtils = JavaViewUtils.getInstance();



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)!!) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        unzipAssetsToFilesDirAsync("data.zip")
        getAndroidJar()


        testFindViewAttrValue()


    }


    fun testFindViewAttrValue() {
        setContentView(activityValueBinding.root)
        activityValueBinding.apply {
            loadAar.setOnClickListener {
                val aarPath = editorAar.text.toString().toFile()
                val outPath = File(filesDir, "aar/${aarPath.nameWithoutExtension}")
                outputText.appendLineArg("输出路径；", outPath.absolutePath)
                if (aarPath.exists()) {
                    unzipAsync(aarPath, outPath)
                    resourceUtil.forPackage("app", File(outPath, "res"))
                } else {
                    resourceUtil.forPackage("app", File(outPath, "res"))
                }
            }

            clear.setOnClickListener {
                outputText.text = ""
            }

            findAttr.setOnClickListener {
                var namespace = editorNamespace.text.toString()
                var attrName = editorAttr.text.toString()
                var prefix = editorPrefix.text.toString()
                var attrValue: String? = null

                if (namespace.isBlank()) {
                    return@setOnClickListener
                }

                val tables = findResourceTables(namespace)
                if (tables.isEmpty()) {
                    return@setOnClickListener
                }

                val pck = namespace.substringAfter(SdkConstants.URI_PREFIX)
                val attr =
                    findAttr(tables, namespace, pck, attrName)
                        ?: run {
                            outputText.appendLineArg(
                                "No attribute found with name '{}' in package '{}'", attrName,
                                if (namespace == NAMESPACE_AUTO) "<auto>" else pck
                            )
                            return@setOnClickListener
                        }

                // If user is directly typing the entry name. For example 'app_name'
                if (!prefix.startsWith('@')) {
                    addValuesForAttr(attr, pck, prefix)
                    return@setOnClickListener
                }


                // If user is typign entry with package name and resource type. For example
                // '@com.itsaky.test.app:string/app_name' or '@android:string/ok'
                var matcher = attrValue_qualifiedRef.matcher(prefix)
                if (matcher.matches()) {
                    val valPck = matcher.group(1)
                    val typeStr = matcher.group(3)
                    val valType = AaptResourceType.values().firstOrNull { it.tagName == typeStr }
                        ?: return@setOnClickListener
                    val newPrefix = matcher.group(4) ?: ""
                    addValues(valType, newPrefix) { it == valPck }
                    return@setOnClickListener
                }

                // If user is typing qualified reference but with incomplete type
                // For example: '@android:str' or '@com.itsaky.test.app:str'
                matcher = attrValue_qualifiedRefWithIncompleteType.matcher(prefix)
                if (matcher.matches()) {
                    val valPck = matcher.group(1)!!
                    val incompleteType = matcher.group(3) ?: ""
                    addResourceTypes(valPck, incompleteType)
                    return@setOnClickListener
                }

                // If user is typing qualified reference but with incomplete type or package name
                // For example: '@android:str' or '@str'
                matcher = attrValue_qualifiedRefWithIncompletePckOrType.matcher(prefix)
                if (matcher.matches()) {
                    val valPck = matcher.group(1)!!
                    if (!valPck.contains('.')) {
                        addResourceTypes("", valPck)
                    }
                    addPackages(valPck)
                    return@setOnClickListener
                }

                // If user is typing entry name with resource type. For example '@string/app_name'
                matcher = attrValue_unqualifiedRef.matcher(prefix)
                if (matcher.matches()) {
                    val typeStr = matcher.group(1)
                    val newPrefix = matcher.group(2) ?: ""
                    val valType = AaptResourceType.values().firstOrNull { it.tagName == typeStr }
                        ?: return@setOnClickListener
                    addValues(valType, newPrefix)
                    return@setOnClickListener
                }
            }
        }
    }


    fun testFindViewAttr() {
        setContentView(activityAttrBinding.root)

        activityAttrBinding.apply {
            loadAar.setOnClickListener {
                val aarPath = editorAar.text.toString().toFile()
                val outPath = File(filesDir, "aar/${aarPath.nameWithoutExtension}")
                outputText.appendLineArg("输出路径；", outPath.absolutePath)
                if (aarPath.exists()) {
                    unzipAsync(aarPath, outPath)
                    resourceUtil.forPackage("app", File(outPath, "res"))
                } else {
                    resourceUtil.forPackage("app", File(outPath, "res"))
                }
            }

            findAttr.setOnClickListener {
                val clazzName = editorClazz.text.toString()
                val editorParentClazz = editorParentClazz.text.toString()
                val editorNamespace = editorNamespace.text.toString()

                val namespace = editorNamespace
                val tables = findResourceTables(namespace)
                if (tables.isEmpty()) {
                    activityAttrBinding.outputText.appendLineArg(
                        "No resource tables found for namespace: {}",
                        namespace
                    )
                    return@setOnClickListener
                }
                val pck = namespace.substringAfter(SdkConstants.URI_PREFIX)
                val packages = mutableSetOf<ResourceTablePackage>()
                for (table in tables) {
                    for (tablePackage in table.packages) {
                        val styleables = tablePackage.findGroup(AaptResourceType.STYLEABLE)
                            ?: return@setOnClickListener
                        val nodeStyleables =
                            findNodeStyleables(clazzName, editorParentClazz, styleables)
                        for (nodeStyleable in nodeStyleables) {
                            for (ref in nodeStyleable.entries) {
                                activityAttrBinding.outputText.appendLineArg(ref.name.entry, pck)
                            }
                        }
                    }
                    activityAttrBinding.outputText.appendLineArg(

                    )
                    if (namespace == NAMESPACE_AUTO) {
                        packages.addAll(table.packages.filter { it.name.isNotBlank() })
                    } else {
                        val tablePackage = table.findPackage(pck)
                        tablePackage?.also { packages.add(it) }
                    }
                }

                for (tablePackage in packages) {
                    val styleables = tablePackage.findGroup(AaptResourceType.STYLEABLE)
                        ?: return@setOnClickListener
                    val nodeStyleables =
                        findNodeStyleables(clazzName, editorParentClazz, styleables)
                    if (nodeStyleables.isEmpty()) {
                        return@setOnClickListener
                    }

                    for (nodeStyleable in nodeStyleables) {
                        for (ref in nodeStyleable.entries) {

                            //activityAttrBinding.outputText.appendLineArg(ref.name.entry, pck)
                        }
                    }

                }


            }

            clear.setOnClickListener {
                outputText.text = ""
            }

        }


    }

    fun testJavaView() {
        setContentView(activityMainBinding.root)
        ///加载jar然后获取其中的class否是view
        val viewUtils = JavaViewUtils.getInstance()
        activityMainBinding.loadjar.setOnClickListener {
            viewUtils.loadJar(File(activityMainBinding.editor.text.toString()))
        }
        activityMainBinding.printclass.setOnClickListener {
            activityMainBinding.textview.text = ""
            JavaViewUtils.getJavaViewClasses().forEach { (s, javaClass) ->
                activityMainBinding.textview.append("\n")
                activityMainBinding.textview.append(s)
            }
        }
    }


    private fun addValuesForAttr(
        attr: AttributeResource,
        pck: String,
        prefix: String,
    ) {
        if (attr.typeMask == FormatFlags.REFERENCE_VALUE) {
            completeReferences(prefix)
        } else {
            // Check for specific attribute formats
            if (attr.hasType(STRING)) {
                addValues(type = AaptResourceType.STRING, prefix = prefix)
            }

            if (attr.hasType(INTEGER)) {
                addValues(type = AaptResourceType.INTEGER, prefix = prefix)
            }

            if (attr.hasType(COLOR)) {
                addValues(type = AaptResourceType.COLOR, prefix = prefix)
            }

            if (attr.hasType(BOOLEAN)) {
                addValues(type = BOOL, prefix = prefix)
            }

            if (attr.hasType(DIMENSION)) {
                if (prefix.isNotBlank() && prefix[0].isDigit()) {
                    addConstantDimensionValues(prefix)
                } else addValues(type = DIMEN, prefix = prefix)
            }

            if (attr.hasType(INTEGER)) {
                addValues(type = AaptResourceType.INTEGER, prefix = prefix)
            }

            if (attr.hasType(ENUM) || attr.hasType(FLAGS)) {
                for (symbol in attr.symbols) {
                    createEnumOrFlagCompletionItem(
                        pck = pck,
                        name = symbol.symbol.name.entry!!,
                    )
                }
            }

            if (attr.hasType(REFERENCE)) {
                completeReferences(prefix)
            }
        }
    }

    private fun addResourceTypes(
        pck: String,
        incompleteType: String,
    ) {
        activityValueBinding.outputText.appendLineArg(incompleteType)
        listResTypes().forEach {
            createEnumOrFlagCompletionItem(pck, it)
        }
    }


    private fun listResTypes(): List<String> = AaptResourceType.entries.map { it.tagName }


    private fun addPackages(incompletePck: String) {
        val packages =
            findResourceTables(SdkConstants.ANDROID_URI).flatMap {
                it.packages.filter { pck -> true }
            }
        packages.forEach {
            val item = createEnumOrFlagCompletionItem(it.name, it.name)
        }
    }

    private fun AttributeResource.hasType(check: FormatFlags): Boolean {
        return hasType(check.number)
    }

    private fun AttributeResource.hasType(check: Int): Boolean {
        return this.typeMask and check != 0
    }

    private fun completeReferences(prefix: String) {
        for (value in AaptResourceType.entries) {
            if (value == AaptResourceType.UNKNOWN) {
                continue
            }
            addValues(value, prefix)
        }
    }


    private fun addValues(
        type: AaptResourceType,
        prefix: String,
        checkPck: (String) -> Boolean = { true }
    ) {
        val allNamespaces: Set<Pair<String, String>> = mutableSetOf(
            SdkConstants.ANDROID_NS_NAME to SdkConstants.ANDROID_URI,
            SdkConstants.APP_PREFIX to SdkConstants.AUTO_URI,
            SdkConstants.TOOLS_NS_NAME to SdkConstants.TOOLS_URI
        )
        val entries =
            allNamespaces
                .flatMap { findResourceTables(it.second) }
                .flatMap { table ->
                    table.packages.mapNotNull { pck ->
                        if (!checkPck(pck.name)) {
                            return@mapNotNull null
                        }
                        pck.name to
                                pck.findGroup(type)?.findEntries { entryName ->
                                    return@findEntries true
                                }
                    }
                }
                .toHashSet()

        entries.forEach { pair ->
            pair.second?.forEach {
                createAttrValueCompletionItem(
                    pair.first,
                    type.tagName,
                    it.name
                )

            }
        }
    }

    private fun createAttrValueCompletionItem(
        pck: String = "",
        type: String,
        name: String,
    ) {
        val sb = StringBuilder()
        sb.append("@")
        if (pck.isNotBlank() && pck == SdkConstants.ANDROID_NS_NAME) {
            sb.append(pck)
            sb.append(":")
        }
        sb.append(type)
        sb.append("/")
        sb.append(name)
        activityValueBinding.outputText.appendLineArg(sb.toString())
    }

    private fun createEnumOrFlagCompletionItem(
        pck: String = "",
        name: String,
    ) {
        activityValueBinding.outputText.appendLineArg("$pck:$name")
    }


    private fun addConstantDimensionValues(prefix: String) {
        var i = 0
        while (i < prefix.length && prefix[i].isDigit()) {
            ++i
        }
        val dimen = prefix.substring(0, i)
        for (unit in dimensionUnits) {
            val value = "${dimen}${unit}"
            createEnumOrFlagCompletionItem(name = value)
        }
    }


    fun findAttr(
        tables: Set<ResourceTable>,
        namespace: String,
        pck: String,
        attr: String
    ): AttributeResource? {
        if (namespace != NAMESPACE_AUTO && pck == SdkConstants.ANDROID_NS_NAME) {
            // AndroidX dependencies include attribute declarations with the 'android' package
            // Those must not be included when completing values
            val attrEntry =
                ResourceUtils.COMPLETION_FRAMEWORK_RES
                    ?.findPackage(SdkConstants.ANDROID_NS_NAME)
                    ?.findGroup(AaptResourceType.ATTR)
                    ?.findEntry(attr)
                    ?.findValue(ConfigDescription())
                    ?.value
            return if (attrEntry is AttributeResource) attrEntry else null
        }
        return if (namespace == NAMESPACE_AUTO) {
            findAttr(tables.flatMap { it.packages }, attr)
        } else {
            findAttr(tables.mapNotNull { it.findPackage(pck) }, attr)
        }
    }

    private fun findAttr(
        packages: Collection<ResourceTablePackage>,
        attr: String
    ): AttributeResource? {
        for (pck in packages) {
            val entry =
                pck.findGroup(AaptResourceType.ATTR)?.findEntry(attr)
                    ?.findValue(ConfigDescription())?.value ?: continue
            if (entry is AttributeResource) {
                return entry
            }
        }
        return null
    }


    fun findNodeStyleables(
        nodeName: String,
        parentName: String? = null,
        styleables: ResourceGroup
    ): Set<Styleable> {
        val widgets = widgetTableUtil.getWidgetTable() ?: return emptySet()
        // Find the widget
        val widget =
            if (nodeName.contains(".")) {
                widgets.getWidget(nodeName)
            } else {
                widgets.findWidgetWithSimpleName(nodeName)
            }

        if (widget != null) {
            // This is a widget from the Android SDK
            // we can get its superclasses and other stuff
            return findStyleablesForWidget(styleables, widgets, widget, parentName = parentName)
        } else if (nodeName.contains('.')) {
            // Probably a custom view or a view from libraries
            // If the developer follows the naming convention then only the completions will be provided
            // This must be called if and only if the tag name is qualified
            return findStyleablesForName(
                styleables,
                nodeName = nodeName,
                parentName = parentName,
                true
            )
        }

        activityAttrBinding.outputText.appendLineArg("Cannot find styleable entries for tag: null")
        return emptySet()
    }


    fun findStyleablesForWidget(
        styleables: ResourceGroup,
        widgets: WidgetTable,
        widget: Widget,
        parentName: String? = null,
        addFromParent: Boolean = true,
        suffix: String = ""
    ): Set<Styleable> {
        val result = mutableSetOf<Styleable>()

        // Find the <declare-styleable> for the widget in the resource group
        addWidgetStyleable(styleables, widget, result, suffix = suffix)

        // Find styleables for all the superclasses
        addSuperclassStyleables(styleables, widgets, widget, result, suffix = suffix)

        // Add attributes provided by the layout params
        if (addFromParent && parentName != null) {
            val parentWidget =
                if (parentName.contains(".")) {
                    widgets.getWidget(parentName)
                } else {
                    widgets.findWidgetWithSimpleName(parentName)
                }

            if (parentWidget != null) {
                result.addAll(
                    findStyleablesForWidget(
                        styleables,
                        widgets,
                        parentWidget,
                        parentName,
                        false,
                        "_Layout"
                    )
                )
            } else {
                result.addAll(findLayoutParams(styleables, parentName))
            }
        }

        return result
    }

    fun findStyleablesForName(
        styleables: ResourceGroup,
        nodeName: String,
        parentName: String? = null,
        addFromParent: Boolean = false,
        suffix: String = ""
    ): Set<Styleable> {
        val result = mutableSetOf<Styleable>()

        // Styles must be defined by the View class' simple name
        var name = nodeName
        if (name.contains('.')) {
            name = name.substringAfterLast('.')
        }

        // Common attributes for all views
        addWidgetStyleable(styleables = styleables, widget = "View", result = result)

        // Find the declared styleable
        val entry = findStyleableEntry(styleables, "$name$suffix")
        if (entry != null) {
            result.add(entry)
        }

        // If the layout params from the parent must be added, check for parent and then add them
        // Layout param attributes must be added only from the direct parent
        if (addFromParent) {
            parentName?.also { result.addAll(findLayoutParams(styleables, parentName)) }
        }

        return result
    }

    fun findLayoutParams(
        styleables: ResourceGroup,
        parentName: String,
    ): Set<Styleable> {
        val result = mutableSetOf<Styleable>()
        // Add layout params common for all view groups and the ones supporting child margins
        addWidgetStyleable(styleables, "ViewGroup", result, suffix = "_Layout")
        addWidgetStyleable(styleables, "ViewGroup", result, suffix = "_MarginLayout")
        var name = parentName
        if (name.contains('.')) {
            name = name.substringAfterLast('.')
        }
        addWidgetStyleable(styleables, name, result, suffix = "_Layout")
        return result
    }

    fun addWidgetStyleable(
        styleables: ResourceGroup,
        widget: Widget,
        result: MutableSet<Styleable>,
        suffix: String = ""
    ) {
        addWidgetStyleable(styleables, widget.simpleName, result, suffix)
    }

    fun addWidgetStyleable(
        styleables: ResourceGroup,
        widget: String,
        result: MutableSet<Styleable>,
        suffix: String = ""
    ) {
        val entry = findStyleableEntry(styleables, "${widget}${suffix}")
        if (entry != null) {
            result.add(entry)
        }
    }

    fun addSuperclassStyleables(
        styleables: ResourceGroup,
        widgets: WidgetTable,
        widget: Widget,
        result: MutableSet<Styleable>,
        suffix: String = ""
    ) {
        for (superclass in widget.superclasses) {
            // When a ViewGroup is encountered in the superclasses, add the margin layout params
            if ("android.view.ViewGroup" == superclass) {
                addWidgetStyleable(styleables, "ViewGroup", result, suffix = "_MarginLayout")
            }

            val superr = widgets.getWidget(superclass) ?: continue
            addWidgetStyleable(styleables, superr.simpleName, result, suffix = suffix)
        }
    }

    fun findStyleableEntry(styleables: ResourceGroup, name: String): Styleable? {
        val value = styleables.findEntry(name)?.findValue(ConfigDescription())?.value
        if (value !is Styleable) {
            activityAttrBinding.outputText.appendLineArg("Cannot find styleable for {}", name)
            return null
        }
        return value
    }


    fun findAllModuleResourceTables(): Set<ResourceTable> {
        return ResourceUtils.COMPLETION_MODULE_RES
    }

    fun findResourceTables(nsUri: String?): Set<ResourceTable> {
        if (nsUri.isNullOrBlank()) {
            return emptySet()
        }
        if (nsUri == SdkConstants.AUTO_URI) {
            return findAllModuleResourceTables()
        }
        val pck = nsUri.substringAfter(SdkConstants.URI_PREFIX)
        if (pck.isBlank()) {
            activityValueBinding.outputText.appendLineArg("Invalid namespace: {}", nsUri)
            activityAttrBinding.outputText.appendLineArg("Invalid namespace: {}", nsUri)
            return emptySet()
        }

        if (pck == SdkConstants.ANDROID_NS_NAME) {
            val platformResTable = ResourceUtils.COMPLETION_FRAMEWORK_RES ?: run {
                return emptySet()
            }
            return setOf(platformResTable)
        }

        val table =
            resourceUtil.forPackage(pck)
                ?: run {
                    activityValueBinding.outputText.appendLineArg("Cannot find resource table for package: $pck")
                    activityAttrBinding.outputText.appendLineArg("Cannot find resource table for package: $pck")
                    return emptySet()
                }

        return setOf(table)
    }


}