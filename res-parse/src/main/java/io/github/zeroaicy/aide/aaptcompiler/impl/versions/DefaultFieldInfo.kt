package io.github.zeroaicy.aide.aaptcompiler.impl.versions

import io.github.zeroaicy.aide.aaptcompiler.interfaces.versions.FieldInfo

/** @author Akash Yadav */
class DefaultFieldInfo(name: String, since: Int, removed: Int, deprecated: Int) :
    DefaultInfo(name, since, removed, deprecated),
    FieldInfo
