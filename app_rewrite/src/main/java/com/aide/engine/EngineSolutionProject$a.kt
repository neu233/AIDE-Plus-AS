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
		
		Parcel parcelableParcel =  ZeroAicyExtensionInterface.decompressionParcel(source);
		
		EngineSolutionProject j6 = j6(parcelableParcel);
		
		// 判断并释放 parcelableParcel
		ZeroAicyExtensionInterface.recycleParcelableParcel(source, parcelableParcel);
		
		return j6;
    }

    public EngineSolutionProject j6(Parcel source) {
        return new EngineSolutionProject(source);
    }

    @Override
    public EngineSolutionProject[] newArray(int size) {
        return DW(size);
    }
}

