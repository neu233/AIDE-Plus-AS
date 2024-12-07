package io.github.zeroaicy.aide.extend;

import androidx.annotation.Keep;
import com.aide.codemodel.language.classfile.ClassFilePreProcessor;
import com.aide.common.AppLog;
import io.github.zeroaicy.aide.ClassReader;
import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * ZeroAicy实现的ClassFilePreProcessor
 */
@Keep
public class ZeroAicyClassFilePreProcessor extends ClassFilePreProcessor {

	public ZeroAicyClassFilePreProcessor() {}

	private static ZeroAicyClassFilePreProcessor singleton;

	@Deprecated
	public static boolean isDefaultMethod(String methodSignature) {
		return false;//ClassReader.hasDefaultMethod(methodSignature);
	}

	public static ClassFilePreProcessor getSingleton() {
		if (singleton == null) {
			singleton = new ZeroAicyClassFilePreProcessor();
		}
		return singleton;
	}

	// -> readZipEntry
	@Override
	public Reader readZipEntry(String zipFilePath, String className, String str3) {
		if (className.endsWith(".class")) {
			Reader readClassFile = ClassReader.Dc_ReadClassFile(zipFilePath, className);
			if (readClassFile != null) {
				return readClassFile;
			}
			return new StringReader(String.format( "//类解析器错误 -> %s/%s\n", zipFilePath, className));
		}
		return super.readZipEntry(zipFilePath, className, str3);
	}

	//复用 ZipFile
	// -> getZipFile
	@Override
	public ZipFile getZipFile(String string) {
		return super.getZipFile(string);
	}
	
	
	// -> listZipEntry
	@Override
	public List<String> listZipEntry(String zipFilePath, String listZipEntryName) {

		ZipFile zipFile = null;
		try {
			// str2 相对于Zip内部的路径
			Set<String> listZipNames = new HashSet<>();

			zipFile = getZipFile(zipFilePath);
			//new ZipFile(zipFilePath);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
				String zipEntryName = zipEntry.getName();

				if (zipEntryName.startsWith("src/") 
					&& zipEntryName.endsWith(".java")) {
					zipEntryName = zipEntryName.substring(4);
				}

				if (zipEntryName.endsWith("/")) {
					//去除路径末尾 /
					zipEntryName = zipEntryName.substring(0, zipEntryName.length() - 1);
				}

				if (zipEntryName.equals(listZipEntryName)
					|| !zipEntryName.startsWith(listZipEntryName)) {
					continue;
				}
				if (listZipEntryName.length() > 0 
					&& zipEntryName.charAt(listZipEntryName.length()) != '/') {
					//除了根目录 list ZipEntry子目录应该从 / 开始
					continue;
				}

				// 过滤自己的子文件夹
				int indexOf = zipEntryName.indexOf('/', listZipEntryName.length() + 1);
				if (indexOf > 0) {
					listZipNames.add(zipFilePath + '/' + zipEntryName.substring(0, indexOf));
					continue;
				}

				String lowerEntryName = zipEntryName.toLowerCase();

				if (zipEntry.isDirectory()) {
					listZipNames.add(zipFilePath + '/' + zipEntryName);
					continue;

				} 
				if ((lowerEntryName.endsWith(".class")
					&& lowerEntryName.lastIndexOf('/') >= lowerEntryName.lastIndexOf('$'))) {

					listZipNames.add(zipFilePath + '/' + zipEntryName);
					continue;

				} 
				if (lowerEntryName.endsWith(".java")) {
					if (lowerEntryName.startsWith("src/")) {
						zipEntryName = zipEntryName.substring(4);
					}
					listZipNames.add(zipFilePath + '/' + zipEntryName);
					continue;
				}

			}
			return Arrays.asList(listZipNames.toArray(new String[listZipNames.size()]));
		}
		catch (Exception zipException) {
			AppLog.e("ZeroAicyClassFilePreProcessor", "zip文件错误: " + zipFilePath);
			return Collections.emptyList();
		}
		catch (Throwable th) {
			throw new Error(th);
		}
	}

	// -> collectClassAndJavaFiles
	@Override
	public void collectClassAndJavaFiles(String directoryPath, String str2, Vector<String> vector) {
        try {
            if (str2.length() > 0) {
                str2 = str2 + File.separator;
            }
            String[] childNames = new File(directoryPath + File.separatorChar).list();
			if (childNames == null) {
				return;
			}
			for (String childName : childNames) {
				String childPath = directoryPath + File.separatorChar + childName;
				if (new File(childPath).isDirectory()) {
					collectClassAndJavaFiles(childPath, str2 + childName, vector);
				}
				else if (childName.lastIndexOf(36) == -1 && childName.endsWith(".class")) {
					vector.add(str2 + childName);
				}
				else if (childName.endsWith(".java")) {
					vector.add(str2 + childName);
				}
			}
		}
        
		catch (Throwable th) {

        }
    }

	// -> collectClassAndJavaFiles
	@Override
	public String[] collectClassAndJavaFiles(String str) {
        try {
            Vector<String> vector = new Vector<>();
            try {
                if (str.toUpperCase().endsWith(".CLASS")) {
                    String substring = str.lastIndexOf(File.separator) == -1 ? str : str.substring(str.lastIndexOf(File.separator) + 1, str.length());
                    if (substring.indexOf("$") == -1) {
                        vector.add(substring);
                    }
                }
				else if (new File(str).isDirectory()) {
                    collectClassAndJavaFiles(str, "", vector);
                }
				else {
                    Enumeration<? extends ZipEntry> entries = getZipFile(str).entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry nextElement = entries.nextElement();
                        String name = nextElement.getName();
                        if (!nextElement.isDirectory() 
							&& name.lastIndexOf('/') >= name.lastIndexOf('$')
							&& name.endsWith(".class")) {
                            vector.add(name);
							continue;
                        }
						if (!nextElement.isDirectory() && name.endsWith(".java")) {
                            if (name.startsWith("src/") || name.startsWith("src\\")) {
                                name = name.substring(4, name.length());
                            }
                            vector.add(name);
                        }
                    }
                }
            }
			catch (Exception unused) {
				
            }
            String[] strArr = new String[vector.size()];
            vector.copyInto(strArr);
            return strArr;
        }
		catch (Throwable th) {
			return null;
        }
    }
}
