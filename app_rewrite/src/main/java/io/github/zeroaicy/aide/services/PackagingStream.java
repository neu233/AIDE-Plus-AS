package io.github.zeroaicy.aide.services;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

public class PackagingStream extends ZipOutputStream {

	private Set<String> zipEntryNameSet = new HashSet<>();

	public PackagingStream(OutputStream out) {
		this(out, StandardCharsets.UTF_8);
	}
	public PackagingStream(OutputStream out, Charset charset) {
		super(out, charset);
	}

	//查询是否已添加
	public boolean contains(String str) {
		return this.zipEntryNameSet.contains(str);
	}
	public int getZipEntryCount(){
		return this.zipEntryNameSet.size();
	}

	@Override
	public void putNextEntry ( ZipEntry zipEntry ) throws IOException {
		if (contains(zipEntry.getName())) {
			throw new ZipException("Entry already exists: " + zipEntry.getName());
		}
		this.zipEntryNameSet.add(zipEntry.getName());
		super.putNextEntry(zipEntry);
	}
}
