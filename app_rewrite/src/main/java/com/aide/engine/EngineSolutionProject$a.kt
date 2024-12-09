package com.aide.engine

import android.os.Parcel
import android.os.Parcelable
import io.github.zeroaicy.aide.extend.ZeroAicyExtensionInterface


/**
 * @author: 罪慾
 * @github: https://github.com/neu233/
 * @mail: 3115093767@qq.com
 * @createTime: 2024/12/9
 */


class `EngineSolutionProject$a` : Parcelable.Creator<EngineSolutionProject>{

    fun DW(size: Int): Array<EngineSolutionProject?> {
        return arrayOfNulls(size)
    }
    override fun createFromParcel(source: Parcel?): EngineSolutionProject {
        val parcelableParcel = ZeroAicyExtensionInterface.decompressionParcel(source)
        val j6 = j6(parcelableParcel)
        // 判断并释放 parcelableParcel
        ZeroAicyExtensionInterface.recycleParcelableParcel(source, parcelableParcel)
        return j6
    }

    fun j6(source: Parcel?): EngineSolutionProject {
        return EngineSolutionProject(source)
    }

    override fun newArray(size: Int): Array<EngineSolutionProject?> {
        return DW(size)
    }
}