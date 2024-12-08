

package io.github.zeroaicy.aide.xml.internal.widgets.util

import io.github.zeroaicy.aide.aaptcompiler.interfaces.widgets.Widget
import io.github.zeroaicy.aide.aaptcompiler.interfaces.widgets.WidgetType

/**
 * Default implementation of [Widget].
 * @author Akash Yadav
 */
class DefaultWidget(
    override val simpleName: String,
    override val qualifiedName: String,
    override val type: WidgetType,
    override val superclasses: List<String>
) : Widget
