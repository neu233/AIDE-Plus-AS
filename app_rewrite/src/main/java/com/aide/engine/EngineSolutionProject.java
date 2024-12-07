//
// Decompiled by Jadx - 614ms
//
package com.aide.engine;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Keep;
import com.aide.ui.util.FileSystem;
import io.github.zeroaicy.aide.extend.ZeroAicyExtensionInterface;
import java.util.ArrayList;
import java.util.List;

@Keep
public class EngineSolutionProject implements Parcelable {
	public static String getKind(EngineSolution.File file){
		return EngineSolution.File.DW(file);
	}
	public static String getPath(EngineSolution.File file){
		return EngineSolution.File.j6(file);
	}
	
	
	public static final Creator<EngineSolutionProject> CREATOR = new EngineSolutionProject$a();

	// id
    public final String projectName;

    final String AL;
    final List<String> Jl;
    final String Q6;
    final boolean Z1;
    final boolean cT;

    public final List<EngineSolution.File> fY;

	// targetVersion
    final String hK;
    final List<String> iW;
    final String jw;
    final boolean k2;
    final List<String> kf;
    final String mb;
    final boolean n5;
    final boolean q7;
    final List<String> qp;
    final String w9;
    final String zh;

	// 压缩标志
	// private boolean compress = false;

	private static final boolean enableJava17 = false;

	/*

	 EngineSolution.File 类型为CodeModel::getName()

	 */

	/**
	 * 没有被依赖的EngineSolutionProject就是主项目
	 */
    public EngineSolutionProject(
		// 项目名(Id) Assembly::rootNamespace
		String projectName, 
		
		String projectPath, 
		
		// 项目路径 Assembly::configuration
		String configuration, 
		// 项目文件集合
		List<EngineSolution.File> sourceSolutionFiles,
		// 依赖的项目名
		List<String> references,
		
		boolean checked, 
		String str4, 
		String debugOutputPath, 
		String releaseOutputPath, 
		String targetVersion, 
		boolean isExternal, 
		boolean isDebug, 
		boolean isRelease,
		boolean isSubProject, 
		String str8, 
		List<String> list3, 
		List<String> list4, 
		List<String> list5) {
		// 项目名 此EngineSolutionProject唯一id
		// 项目命名空间
		this.projectName = projectName;
		
		// rootNamespace
        this.mb = projectPath;
		
		// configuration
        this.jw = configuration;
		
        this.fY = sourceSolutionFiles;

		addKotlinSrcDir(sourceSolutionFiles);

        this.qp = references;
        this.k2 = checked;
		
		//  engineSolutionProject2.zh
        this.zh = str4;
        this.AL = debugOutputPath;
        this.w9 = releaseOutputPath;

		// targetVersion
        this.hK = targetVersion;

		/******************************测试Java17******************************************/
		// 启用Java17
		if (EngineSolutionProject.enableJava17) {
			this.hK = "17";
			if ("android.jar".equals(this.projectName) 
				|| "rt.jar".equals(this.projectName)) {
				// 添加 core-lambda-stubs.jar依赖
				sourceSolutionFiles.add(new EngineSolution.File(FileSystem.getParent(projectPath) + "/core-lambda-stubs.jar", "core-lambda-stubs.jar", null, false, false));
			}
		}
		/******************************测试Java17******************************************/

        this.cT = isExternal;
        this.q7 = isDebug;
        this.Z1 = isRelease;
        this.n5 = isSubProject;
        this.Q6 = str8;
        this.kf = list3;
        this.Jl = list4;
        this.iW = list5;
    }


	/**
	 * 添加kotlin源码路径
	 */
	private void addKotlinSrcDir(List<EngineSolution.File> sourceSolutionFiles) {
		String javaSrcDir = null;
		for (EngineSolution.File file : sourceSolutionFiles) {
			// EngineSolution.File -> mb[文件类型]
			if ("Java".equals(EngineSolution.File.DW(file))) {
				// EngineSolution.File j6 -> WB
				javaSrcDir = EngineSolution.File.j6(file);
				break;
			}
		}
		if (javaSrcDir != null) {
			//AppLog.d(this.projectName, "添加 " + javaSrcDir);
			sourceSolutionFiles.add(new EngineSolution.File(javaSrcDir, "Kotlin", null, false, false));
		}
	}



    @Override
    public int describeContents() {
		return 0;
    }


	/*
	 * 纯smali实现  需要替换 writeToParcelOriginal所在类名
	 * 需要将原始writeToParcel -> writeToParcelOriginal
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// dest里面已有有数据
		Parcel parcelableParcel = Parcel.obtain();
		writeToParcelOriginal(parcelableParcel, flags);
		ZeroAicyExtensionInterface.compressionParcel(parcelableParcel, dest);
	}

    public void writeToParcelOriginal(Parcel parcel, int flags) {
		parcel.writeString(this.projectName);
		parcel.writeString(this.mb);
		parcel.writeString(this.jw);
		parcel.writeInt(this.k2 ? 1 : 0);
		parcel.writeString(this.zh);
		parcel.writeString(this.AL);
		parcel.writeString(this.w9);
		parcel.writeString(this.hK);

		parcel.writeInt(this.cT ? 1 : 0);
		parcel.writeInt(this.q7 ? 1 : 0);
		parcel.writeInt(this.Z1 ? 1 : 0);

		parcel.writeInt(this.n5 ? 1 : 0);
		parcel.writeString(this.Q6);
		parcel.writeList(this.qp);
		parcel.writeList(this.kf);
		parcel.writeList(this.Jl);
		parcel.writeList(this.iW);
		parcel.writeList(this.fY);
    }


    public EngineSolutionProject(Parcel dest) {
		this.projectName = dest.readString();
		this.mb = dest.readString();
		this.jw = dest.readString();
		this.k2 = dest.readInt() != 0;
		this.zh = dest.readString();
		this.AL = dest.readString();
		this.w9 = dest.readString();
		this.hK = dest.readString();
		this.cT = dest.readInt() != 0;
		this.q7 = dest.readInt() != 0;
		this.Z1 = dest.readInt() != 0;

		this.n5 = dest.readInt() != 0;
		this.Q6 = dest.readString();

		ArrayList<String> arrayList = new ArrayList<>();
		this.qp = arrayList;
		dest.readList(arrayList, getClass().getClassLoader());

		ArrayList<String> arrayList2 = new ArrayList<>();
		this.kf = arrayList2;
		dest.readList(arrayList2, getClass().getClassLoader());

		ArrayList<String> arrayList3 = new ArrayList<>();
		this.Jl = arrayList3;
		dest.readList(arrayList3, getClass().getClassLoader());

		ArrayList<String> arrayList4 = new ArrayList<>();
		this.iW = arrayList4;
		dest.readList(arrayList4, getClass().getClassLoader());

		ArrayList<EngineSolution.File> arrayList5 = new ArrayList<EngineSolution.File>();
		this.fY = arrayList5;
		dest.readList(arrayList5, getClass().getClassLoader());
    }	
}

