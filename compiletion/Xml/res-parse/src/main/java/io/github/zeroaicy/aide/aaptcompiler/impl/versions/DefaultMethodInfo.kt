package io.github.zeroaicy.aide.aaptcompiler.impl.versions

import io.github.zeroaicy.aide.aaptcompiler.interfaces.versions.MethodInfo

/** @author Akash Yadav */
internal class DefaultMethodInfo(
    override val simpleName: String,
    name: String,
    since: Int,
    removed: Int,
    deprecated: Int
) : DefaultInfo(name, since, removed, deprecated),
    MethodInfo {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DefaultMethodInfo) return false
        if (!super.equals(other)) return false

        if (simpleName != other.simpleName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + simpleName.hashCode()
        return result
    }

    override fun toString(): String {
        return "DefaultMethodInfo(simpleName='$simpleName')"
    }
}
