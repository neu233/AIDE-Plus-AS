package io.github.zeroaicy.aide.aaptcompiler.impl.versions

import io.github.zeroaicy.aide.aaptcompiler.utils.jdt.core.Signature
import io.github.zeroaicy.aide.aaptcompiler.interfaces.versions.ClassInfo
import io.github.zeroaicy.aide.aaptcompiler.interfaces.versions.FieldInfo
import io.github.zeroaicy.aide.aaptcompiler.interfaces.versions.MethodInfo
import java.util.concurrent.ConcurrentHashMap

/**
 * Default implementation of [ClassInfo]
 *
 * @author Akash Yadav
 */
open class DefaultClassInfo(name: String, since: Int, removed: Int, deprecated: Int) :
    DefaultInfo(
        name,
        since,
        removed,
        deprecated
    ), ClassInfo {

     val fields =
        ConcurrentHashMap<String, FieldInfo>()
     val methods =
        ConcurrentHashMap<String, MutableList<MethodInfo>>()

    override fun getField(name: String): FieldInfo? {
        return fields[name]
    }

    override fun getMethod(
        name: String,
        vararg params: String
    ): MethodInfo? {
        val methods = methods[name] ?: return null
        val paramTypes = Array(size = params.size) { "" }
        params.forEachIndexed { index, type ->
            paramTypes[index] =
                Signature.createTypeSignature(
                    type.replace(
                        '.',
                        '/'
                    ), true
                )
        }

        return methods.find {
            val methodParams =
                Signature.getParameterTypes(it.name)
            methodParams != null && paramTypes.contentDeepEquals(methodParams)
        }
    }
}
