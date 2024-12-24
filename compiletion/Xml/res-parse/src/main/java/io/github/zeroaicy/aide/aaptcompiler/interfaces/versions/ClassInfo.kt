package io.github.zeroaicy.aide.aaptcompiler.interfaces.versions

/**
 * Info about a class.
 *
 * @author Akash Yadav
 */
interface ClassInfo : Info {

    /** Get info about the field with the given name. */
    fun getField(name: String): FieldInfo?

    /** Get the method with the given [name] and the [parameterTypes]. */
    fun getMethod(
        name: String,
        vararg params: String
    ): MethodInfo?
}
