

package io.github.zeroaicy.aide.aaptcompiler.interfaces.widgets

/**
 * A widget table holds information about all widgets defined in the `widgets.txt`.
 *
 * @author Akash Yadav
 */
interface WidgetTable {

    /**
     * Get the widget for the given fully qualified name. For example, `android.widget.TextView`.
     *
     * @return The widget or `null`.
     */
    fun getWidget(name: String): Widget?

    /**
     * Finds the first widget with the given simple name. This searches the whole table so [getWidget]
     * is preferable.
     */
    fun findWidgetWithSimpleName(name: String): Widget?

    /**
     * Get the set of all registered widgets.
     *
     * @return The set of widgets.
     */
    fun getAllWidgets(): Set<Widget>
}
