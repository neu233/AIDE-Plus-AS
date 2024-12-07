package io.github.zeroaicy.aide.completion;


import android.content.Context;
import com.aide.common.AppLog;
import com.aide.ui.util.FileSystem;
import io.github.zeroaicy.aide.aaptcompiler.ApiVersionsUtils;
import io.github.zeroaicy.aide.aaptcompiler.impl.versions.DefaultClassInfo;
import io.github.zeroaicy.aide.aaptcompiler.interfaces.versions.ApiVersions;
import io.github.zeroaicy.aide.aaptcompiler.interfaces.versions.ClassInfo;
import io.github.zeroaicy.aide.aaptcompiler.interfaces.versions.FieldInfo;
import io.github.zeroaicy.aide.aaptcompiler.interfaces.versions.MethodInfo;
import io.github.zeroaicy.aide.aaptcompiler.utils.jdt.core.Signature;
import io.github.zeroaicy.aide.ui.services.ThreadPoolService;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ApiVersionCompletion {

	// 防止重复加载
	private static AtomicBoolean inited = new AtomicBoolean(false);

	// 主线程只读

	private static ApiVersions apiVersions;
	
	public static synchronized void preLoad(final Context context) {

		if (!inited.compareAndSet(false, true)) {
			return;
		}

		// 异步初始化
		ThreadPoolService.getDefaultThreadPoolService()
			.submit(new Runnable(){
				@Override
				public void run() {
					initAsync(context);
				}
			});
	}


	private static void initAsync(Context context) {
		// data,zip 根目录是 data
		final File platformDir = XmlCompletionUtils.getPlatformDir();
		File androidSdkDataDir = new File(platformDir, "data");
		if (!androidSdkDataDir.exists() 
			|| !androidSdkDataDir.isDirectory()
			|| androidSdkDataDir.list() == null) {
			// data,zip 根目录是 data
			// 解压
			try {
				FileSystem.unZip(context.getAssets().open("data.zip"), platformDir.getAbsolutePath(), true);
			}
			catch (IOException e) {}
		}

		apiVersions = ApiVersionsUtils.getInstance(platformDir).getApiVersion();			

	}

	public static ApiVersionInfo getApiVersionInfo(String typeName) {
		if (apiVersions == null) {
			return null;
		}
		
		ClassInfo classInfo = apiVersions.getClass(typeName);
		if (classInfo == null) {
			return null;
		}
		return new ApiVersionInfo(classInfo);
	}

	public static ApiVersionInfo getFieldApiVersionInfo(String typeName, String fieldName) {
		if (apiVersions == null) {
			return null;
		}
		
		ClassInfo classInfo = apiVersions.getClass(typeName);
		
		if (classInfo == null) {
			AppLog.println_d("getFieldApiVersionInfo classInfo null");
			return null;
		}

		FieldInfo fieldInfo = classInfo.getField(fieldName);
		// 没有按 classInfo处理🐶
		return new ApiVersionInfo(fieldInfo, classInfo);
	}


	public static ApiVersionInfo getMethodApiVersionInfo(String typeName, String methodSignature) {
		if (apiVersions == null) {
			return null;
		}
		ClassInfo classInfo = apiVersions.getClass(typeName);
		if (classInfo == null) {
			return null;
		}
		
		MethodInfo methodInfo = null;
		
		int methodNameEnd = methodSignature.indexOf('(');
		String methodName = 
			methodNameEnd == -1 ?
			methodSignature
			: methodSignature.substring(0, methodNameEnd);
		
		String methodSmaliSignature = methodsParamToSmali(methodSignature);
		if (classInfo instanceof DefaultClassInfo) {
			DefaultClassInfo defaultClassInfo = (DefaultClassInfo)classInfo;
			List<MethodInfo> methods = defaultClassInfo.getMethods$res_parse_release().get(methodName);

			if( methods != null && !methods.isEmpty()){
				for (MethodInfo info : methods) {
					String name = info.getName();
					if( name == null ){
						continue;
					}
					if (name.startsWith(methodSmaliSignature)) {
						methodInfo = info;
						break;
					}
				}
			}
		} else {
			String[] parameterTypes = Signature.getParameterTypes(methodSignature);
			methodInfo = classInfo.getMethod(methodName, parameterTypes);
		}
		
		// 没有按 classInfo处理🐶
		return new ApiVersionInfo(methodInfo, classInfo);

	}


	public static String methodsParamToSmali(String buildParam) {
        String souece = buildParam;
        try {
            buildParam = buildParam.substring(buildParam.indexOf("(") + 1);
            buildParam = buildParam.substring(0, buildParam.lastIndexOf(")"));
        }
		catch (Throwable e) {
        }

        StringBuffer sb = new StringBuffer();
        sb.append(souece.substring(0, souece.indexOf("(")));
        sb.append("(");
        String[] split = buildParam.split(",");
        for (int i = 0; i < split.length; i++) {
            String param = split[i];

            param = param.trim();
            if (param.contains(" ")) param = param.substring(0, param.indexOf(" "));
            if (param.contains("...")) param = param.substring(0, param.indexOf("..."));
            param = param.trim();

            if (param.length() == 0) continue;
            if (param.endsWith("[]")) {
                int size = param.split("\\[").length - 1;
                for (int j = 0; j < size; j++) {
                    sb.append("[");
                }
                sb.append(typeToSmali(param.substring(0, param.indexOf("["))));
            } else {
                sb.append(typeToSmali(param));
            }
        }
        sb.append(")");

        return sb.toString();
    }

    private static String typeToSmali(String type) {
        if ("boolean".equals(type)) {
            return "Z";
        } else if ("byte".equals(type)) {
            return "B";
        } else if ("short".equals(type)) {
            return "S";
        } else if ("char".equals(type)) {
            return "C";
        } else if ("int".equals(type)) {
            return "I";
        } else if ("long".equals(type)) {
            return "J";
        } else if ("float".equals(type)) {
            return "F";
        } else if ("double".equals(type)) {
            return "D";
        }
        if (type != null) {
            String pkg = type;
            if (!type.contains(".")) {
                pkg = "java.lang.Object";
            }
            if (pkg.contains("<")) {
                pkg = pkg.substring(0, pkg.indexOf("<"));
            }
            return "L".concat(pkg.replace(".", "/")).concat(";");
        }
        return type;
    }

}
