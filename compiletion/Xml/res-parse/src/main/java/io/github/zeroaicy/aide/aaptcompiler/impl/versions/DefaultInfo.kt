package io.github.zeroaicy.aide.aaptcompiler.impl.versions

import io.github.zeroaicy.aide.aaptcompiler.interfaces.versions.Info

/** @author Akash Yadav */
open class DefaultInfo(
    override val name: String,
    override val since: Int,
    override val removed: Int,
    override val deprecated: Int
) : Info {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DefaultInfo) return false

        if (name != other.name) return false
        if (since != other.since) return false
        if (removed != other.removed) return false
        if (deprecated != other.deprecated) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + since
        result = 31 * result + removed
        result = 31 * result + deprecated
        return result
    }

    override fun toString(): String {
        return "DefaultInfo(name='$name', since=$since, removed=$removed, deprecated=$deprecated)"
    }
}
