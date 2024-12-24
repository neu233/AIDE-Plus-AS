

package io.github.zeroaicy.aide.aaptcompiler.utils

import java.util.regex.Pattern

val attrValue_unqualifiedRef: Pattern by lazy { Pattern.compile("@(\\w+)/(.*)") }
val attrValue_qualifiedRef: Pattern by lazy { Pattern.compile("@((\\w|\\.)+):(\\w+)/(.*)") }
val attrValue_qualifiedRefWithIncompleteType: Pattern by lazy { Pattern.compile("@((\\w|\\.)+):(\\w*)") }
val attrValue_qualifiedRefWithIncompletePckOrType: Pattern by lazy { Pattern.compile("@((\\w|\\.)*)") }