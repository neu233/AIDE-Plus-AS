

package io.github.zeroaicy.aide.xml.internal.widgets.util

import io.github.zeroaicy.aide.aaptcompiler.interfaces.widgets.WidgetType

/**
 * Parses widget information from the string representation.
 *
 * @author Akash Yadav
 */
object WidgetParser {

    /**
     * Parses a [DefaultWidget] from the given string representation. Returns `null` if cannot parse.
     */
    fun parse(line: String): DefaultWidget? {
        val split = line.split(' ')
        if (split.size < 2) {
            return null
        }

        val type = WidgetType.Factory.forChar(split[0][0]) ?: return null
        val name = split[0].substring(1)
        val simpleName = name.substringAfterLast('.')
        val superclasses = split.subList(1, split.size)
        return DefaultWidget(simpleName, name, type, superclasses)
    }
}
