package io.github.zeroaicy.aide.bean

import com.drake.brv.BindingAdapter
import com.drake.brv.annotaion.ItemOrientation
import com.drake.brv.item.ItemBind
import com.drake.brv.item.ItemSwipe
import java.io.Serializable


/*
author : 罪慾
date : 2024/12/29 22:28
description : QQ3115093767
*/

data class MessageBean(
    val id: Int = 0,
    val title: String = "",
    val actionText: String = "",
    val canClose: Boolean = true,
    val onClick: (() -> Unit) = {},
    override var itemOrientationSwipe: Int =
        if (canClose) ItemOrientation.RIGHT else ItemOrientation.NONE,
) : Serializable, ItemBind, ItemSwipe {
    override fun onBind(vh: BindingAdapter.BindingViewHolder) {}

}