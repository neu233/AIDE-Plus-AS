

package io.github.zeroaicy.aide.aaptcompiler.interfaces.versions


/**
 * API information about classes.
 *
 * @author Akash Yadav
 */
interface ApiVersions {

    /**
     * Get the information about the class with the given name.
     *
     * @param name The fully qualified name of the class.
     */
    fun getClass(name: String): ClassInfo?
}
