

package io.github.zeroaicy.aide.aaptcompiler.interfaces.widgets

/**
 * The type of [Widget].
 *
 * @author Akash Yadav
 */
enum class WidgetType(internal val c: Char) {
    /** A widget. */
    WIDGET('W'),

    /** A layout (ViewGroup). */
    LAYOUT('L'),

    /** A layout param. */
    LAYOUT_PARAM('P');

    internal class Factory {
        companion object {
            @JvmStatic
            fun forChar(c: Char): WidgetType? {
                for (value in entries) {
                    if (value.c == c) {
                        return value
                    }
                }
                return null
            }
        }
    }
}
