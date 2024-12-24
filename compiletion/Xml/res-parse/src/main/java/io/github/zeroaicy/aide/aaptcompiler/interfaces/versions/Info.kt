

package io.github.zeroaicy.aide.aaptcompiler.interfaces.versions

/**
 * Base class for info about class, fields and methods.
 *
 * @author Akash Yadav
 */
interface Info {

    /** Name of this element. */
    val name: String

    /** The introducing API version. */
    val since: Int

    /** The removing API version. */
    val removed: Int

    /** The deprecating API version. */
    val deprecated: Int
}
