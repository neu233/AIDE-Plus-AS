package io.github.zeroaicy.aide.services;

import java.util.zip.ZipEntry;

public interface ZipEntryTransformer {
	/**
	 * 返回null，表示过滤掉
	 */
	public ZipEntry transformer(ZipEntry zipEntry, PackagingStream packagingStream);

	/**
	 * 处理libgdx库
	 */
	public class LibgdxNativesTransformer implements ZipEntryTransformer {

		private String curLibgdxNativesABI;
		private boolean androidExtractNativeLibs;
		public LibgdxNativesTransformer(boolean androidExtractNativeLibs) {
			this.androidExtractNativeLibs = androidExtractNativeLibs;
		}
		public void setCurLibgdxNativesLibsPath(String path) {
			if (path.endsWith("natives-arm64-v8a.jar")) {
				curLibgdxNativesABI = "arm64-v8a";
			} else if (path.endsWith("natives-armeabi-v7a.jar")) {
				curLibgdxNativesABI = "armeabi-v7a";
			} else if (path.endsWith("natives-armeabi.jar")) {
				curLibgdxNativesABI = "armeabi";
			} else if (path.endsWith("natives-x86_64.jar")) {
				curLibgdxNativesABI = "x86_64";
			} else if (path.endsWith("natives-x86.jar")) {
				curLibgdxNativesABI = "x86";
			} else {
				curLibgdxNativesABI = null;
			}
		}

		@Override
		public ZipEntry transformer(ZipEntry zipEntry, PackagingStream packagingStream) {

			String zipEntryName = zipEntry.getName();
			if (curLibgdxNativesABI == null
				|| zipEntry.isDirectory()
				|| !zipEntryName.endsWith(".so")) {
				return null;				
			}
			ZipEntry newZipEntry = new ZipEntry("lib/" + this.curLibgdxNativesABI + "/" + zipEntryName);
			if (!androidExtractNativeLibs) {
				//android:extractNativeLibs="false"时必须无压缩
				newZipEntry.setMethod(ZipEntry.STORED);
			}
			return newZipEntry;
		}
	}
	/**
	 * dex.zip转换器
	 * 不过滤任何资源，即都会添加
	 * 但是会重命名classes%d.dex式文件
	 */
	public class DexZipTransformer implements ZipEntryTransformer {
		protected int classesCountDex = 1;
		@Override
		public ZipEntry transformer(ZipEntry zipEntry, PackagingStream packagingStream) {
			String zipEntryFileName = zipEntry.getName();
			//因为classes.dex会重名命，所以必须先判断
			if (isNotClassesDex(zipEntry, zipEntryFileName)) {
				//过滤已存在的
				if (packagingStream.contains(zipEntryFileName)) {
					return null;
				}
				return zipEntry;
			}

			String dexEntryName = classesCountDex > 1 ? String.format("classes%d.dex", classesCountDex) : "classes.dex";

			//查询 dexEntryName是否已添加
			while (packagingStream.contains(dexEntryName) 
				   && classesCountDex < packagingStream.getZipEntryCount() + 1) {
				classesCountDex++;
				dexEntryName = String.format("classes%d.dex", classesCountDex);
			}

			return new ZipEntry(dexEntryName);
		}

		/**
		 * 不是ClassesDex文件
		 */
		private boolean isNotClassesDex(ZipEntry zipEntry, String zipEntryFileName) {
			zipEntryFileName = zipEntryFileName.toLowerCase();
			boolean endsWith = zipEntry.isDirectory()
				|| zipEntryFileName.contains("/")
				|| ! zipEntryFileName.startsWith("classes") 
				|| ! zipEntryFileName.endsWith(".dex");
			return endsWith;
		}
	}


	/**
	 * zip资源，因为是从jar库添加资源，所以 class与java文件都不能添加
	 */
	public class ZipResourceTransformer implements ZipEntryTransformer {
		@Override
		public ZipEntry transformer(ZipEntry zipEntry, PackagingStream packagingStream) {
			String zipEntryName = zipEntry.getName();
			//过滤已存在的
			if (packagingStream.contains(zipEntryName)) {
				return null;
			}
			zipEntryName = zipEntryName.toLowerCase();
			if (zipEntryName.endsWith(".class")
				|| zipEntryName.endsWith(".java")) {
				return null;
			}
			return zipEntry;
		}
	}
	
	/**
	 * 从文件夹添加的so转换器
	 */
	public class NativeLibFileTransformer implements ZipEntryTransformer {
		private boolean androidExtractNativeLibs;
		public NativeLibFileTransformer(boolean androidExtractNativeLibs) {
			this.androidExtractNativeLibs = androidExtractNativeLibs;
		}
		@Override
		public ZipEntry transformer(ZipEntry zipEntry, PackagingStream packagingStream) {
			String zipEntryName = zipEntry.getName();

			String[] split = zipEntryName.split("/");
			if (split.length >= 2) {
				//只取so父目录/so文件名
				zipEntryName =  "lib/" + split[split.length - 2] + "/" + split[split.length - 1];
			}

			//以包含过滤
			if (packagingStream.contains(zipEntryName)) {
				return null;
			}


			ZipEntry newZipEntry = new ZipEntry(zipEntryName);
			if (!androidExtractNativeLibs) {
				//android:extractNativeLibs="false"时必须无压缩
				newZipEntry.setMethod(ZipEntry.STORED);
			}
			return newZipEntry;
		}
	}
}
