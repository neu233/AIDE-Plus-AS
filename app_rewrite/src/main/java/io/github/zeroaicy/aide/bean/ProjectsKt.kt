package io.github.zeroaicy.aide.bean

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View


/*
author : 罪慾
date : 2025/1/2 22:52
description : QQ3115093767
*/


/**
 *   项目列表顶部 chip 使用
 * */
data class ProjectChipBean(
    val title: String = "",
    val onClick: ((View) -> Unit) = {},
)


data class CreateProjectCategory(
    val title: String = "",
    val mutableList: MutableList<CreateProjectTemplate> = mutableListOf()
)



data class CreateProjectTemplate(
    val title: String = "",
    var templateIcon : Bitmap,
    var description: String = "",

)