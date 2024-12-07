package io.github.zeroaicy.aide.completion;


import android.content.Context;
import io.github.zeroaicy.aide.aaptcompiler.interfaces.versions.ClassInfo;
import io.github.zeroaicy.aide.aaptcompiler.interfaces.versions.Info;

public class ApiVersionInfo {

	public static final ApiVersionInfo Empty = new ApiVersionInfo();;
	Info memberInfo;
	ClassInfo classInfo;

	private final boolean empty;
	private ApiVersionInfo() {
		this.empty = true;
	}

	public ApiVersionInfo(Info classInfo) {
		this(classInfo, null);
	}

	public ApiVersionInfo(Info info, ClassInfo classInfo) {
		this.memberInfo = info;
		this.classInfo = classInfo;
		this.empty = false;
	}

	public boolean isRemoved() {
		if (this.empty) {
			return false;
		}

		Info memberInfo = this.memberInfo;

		ClassInfo classInfo = this.classInfo;
		return (memberInfo != null && memberInfo.getRemoved() > 0)
			|| (classInfo != null && classInfo.getRemoved() > 0);
	}

	public CharSequence getInfo(Context context) {
		if (this.empty) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		
		Info memberInfo = this.memberInfo;
		Info classInfo = this.classInfo;

		int since;
		int deprecated;
		int removed;

		if (memberInfo != null) {
			since = memberInfo.getSince();
			deprecated = memberInfo.getDeprecated();
			removed = memberInfo.getRemoved();
        }else{
			since = 0;
			deprecated = 0;
			removed = 0;
		}

		if (classInfo != null) {
			since = since > 0 ? since : classInfo.getSince();
			deprecated = deprecated > 1 ? deprecated : classInfo.getDeprecated();
			removed = removed > 1 ? removed : classInfo.getRemoved();
        }


		if (since > 0) {
			sb.append(String.format("在 API %d 中添加", since));
		}

        if (deprecated > 1) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(String.format("在 API %d 中弃用" , deprecated));
        }
		
		if (removed > 1) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(String.format("在 API %d 中移除", removed));
        }
        return sb.toString();
    }

}
