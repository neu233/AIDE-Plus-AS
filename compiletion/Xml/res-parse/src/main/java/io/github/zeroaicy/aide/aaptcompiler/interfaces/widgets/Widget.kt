

package io.github.zeroaicy.aide.aaptcompiler.interfaces.widgets

/**
 * A widget defined in the `widgets.txt` file in the Android SDK.
 *
 * @author Akash Yadav
 */
interface Widget {
    /** The simple name of the widget. For example, `TextView`. */
    val simpleName: String

    /** The qualified name of the widget. For example, `android.widget.TextView`. */
    val qualifiedName: String

    /** The type of widget. */
    val type: WidgetType

    /** The fully qualified names of the superclasses of this widget. */
    val superclasses: List<String>
}
