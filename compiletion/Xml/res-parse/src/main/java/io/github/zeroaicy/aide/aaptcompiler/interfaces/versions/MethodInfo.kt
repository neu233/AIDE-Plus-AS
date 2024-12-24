

package io.github.zeroaicy.aide.aaptcompiler.interfaces.versions

/**
 * Info about a method.
 *
 * @author Akash Yadav
 */
interface MethodInfo : Info {

    /**
     * In case of a method, the [Info.name] contains signature of the method This field contains the
     * actual simple name
     */
    val simpleName: String
}
