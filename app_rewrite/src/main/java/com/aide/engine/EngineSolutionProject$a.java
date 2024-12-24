//
// Decompiled by Jadx - 2544ms
//
package com.aide.engine;

import android.os.Parcel;
import android.os.Parcelable;
import io.github.zeroaicy.aide.extend.ZeroAicyExtensionInterface;


public class EngineSolutionProject$a implements Parcelable.Creator<EngineSolutionProject> {
	public EngineSolutionProject[] DW(int size) {
		return new EngineSolutionProject[size];
    }

	@Override
    public EngineSolutionProject createFromParcel(Parcel source) {
		// 解压缩
		Parcel parcelableParcel =  ZeroAicyExtensionInterface.decompressionParcel(source);
		
		// 反序列化
		EngineSolutionProject create = createEngineSolutionProjectFromParcel(parcelableParcel);
		
		// 释放Parcel
		ZeroAicyExtensionInterface.recycleParcelableParcel(source, parcelableParcel);
		
		return create;
    }

    public EngineSolutionProject createEngineSolutionProjectFromParcel(Parcel source) {
        return new EngineSolutionProject(source);
    }

    @Override
    public EngineSolutionProject[] newArray(int size) {
        return DW(size);
    }
}

