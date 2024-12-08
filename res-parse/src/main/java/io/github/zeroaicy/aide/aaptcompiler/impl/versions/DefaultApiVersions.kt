

package io.github.zeroaicy.aide.aaptcompiler.impl.versions

import io.github.zeroaicy.aide.aaptcompiler.interfaces.versions.ApiVersions
import io.github.zeroaicy.aide.aaptcompiler.interfaces.versions.ClassInfo
import java.util.concurrent.ConcurrentHashMap

/** @author Akash Yadav */
internal class DefaultApiVersions : ApiVersions {

    val classes = ConcurrentHashMap<String, ClassInfo>()

    override fun getClass(name: String): ClassInfo? {
        return classes[name.replace('.', '/')]
    }

    internal fun putClass(name: String, info: ClassInfo) {
        classes[name] = info
    }
}
