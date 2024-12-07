package io.github.zeroaicy.aide.services;

import com.aide.common.AppLog;
import io.github.zeroaicy.util.IOUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.BufferedInputStream;
import java.util.zip.CRC32;
import java.io.FileNotFoundException;

public class ZipEntryTransformerService {
	
	public static long getFileCRC32(File file) throws IOException{
		CRC32 crc = new CRC32();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		byte[] data = new byte[4096];
		int count;
		while ( (count = bufferedInputStream.read(data)) > 0 ){
			crc.update(data, 0, count);
		}
		bufferedInputStream.close();
		return crc.getValue();
	}


	public static String getZipEntryName(File file, String relativeRootDirFilePath){
		String filePath = file.getAbsolutePath();
		int index = relativeRootDirFilePath.length();
		int length =filePath.length();
		while ( index < length 
			   && filePath.charAt(index) == '/' ){
			index ++;
		}
		return filePath.substring(index);
	}
	
	public static void packagingDirFile(String relativeRootDirFilePath, File file, ZipEntryTransformer transformer, PackagingStream packagingZipOutput) throws FileNotFoundException, IOException{
		if ( file.isDirectory() ){
			if ( file.isHidden() ){
				return;
			}
			for ( File childFile : file.listFiles() ){
				if ( childFile.isDirectory() && childFile.isHidden() ){
					return;
				}
				packagingDirFile(relativeRootDirFilePath, childFile, transformer, packagingZipOutput);
			}

		}
		else{
			String zipEntryName = getZipEntryName(file, relativeRootDirFilePath);
			ZipEntry zipEntry = new ZipEntry(zipEntryName);
			if ( transformer != null &&
				(zipEntry = transformer.transformer(zipEntry, packagingZipOutput)) == null ){
				//已被转换器过滤
				return;
			}

			// 检查是否无压缩模式
			if ( zipEntry.getMethod() == ZipEntry.STORED ){
				//未压缩时设置未压缩条目数据的CRC-32校验和
				zipEntry.setCrc(getFileCRC32(file));
			}

			zipEntry.setSize(file.length());
			zipEntry.setTime(file.lastModified());
			//添加zip条目
			packagingZipOutput.putNextEntry(zipEntry);

			BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			IOUtils.streamTransfer(inputStream, packagingZipOutput);
			inputStream.close();

			packagingZipOutput.closeEntry();
		}

	}
	/**
	 * 不能添加class文件
	 * 但是 
	 */
	public static void packagingZipFile(String zipFilePath, ZipEntryTransformer transformer, PackagingStream packagingZipOutput, boolean followZipEntryMethod) throws IOException {
		
		if (!new File(zipFilePath).exists()) {
			AppLog.w("Zip file not found: " + zipFilePath);
			return;
		}
		
		ZipInputStream zipFileInput = null;
		try {
			zipFileInput = new ZipInputStream(new FileInputStream(zipFilePath));
			ZipEntry originalZipEntry;
			while ((originalZipEntry = zipFileInput.getNextEntry()) != null) {
				ZipEntry newZipEntry = originalZipEntry;

				if (transformer != null) {
					newZipEntry = transformer.transformer(originalZipEntry, packagingZipOutput);
					//转换器过滤此条目
					if (newZipEntry == null) {
						continue;
					}
				}
				// 转换器未修改
				if (newZipEntry == originalZipEntry) {
					newZipEntry = new ZipEntry(originalZipEntry.getName());
				}

				if (followZipEntryMethod 
					&& originalZipEntry.getMethod() != -1) {
					newZipEntry.setMethod(originalZipEntry.getMethod());
				}

				// 检查 转换后以及跟随压缩方式后是否无压缩
				if (newZipEntry.getMethod() == ZipEntry.STORED) {
					newZipEntry.setCrc(originalZipEntry.getCrc());
					newZipEntry.setSize(originalZipEntry.getSize());
				}


				packagingZipOutput.putNextEntry(newZipEntry);
				IOUtils.streamTransfer(zipFileInput, packagingZipOutput);
				//Entry写入完成
				packagingZipOutput.closeEntry();
			}
		}
		finally {
			IOUtils.close(zipFileInput);
		}
	}
}
