package io.github.zeroaicy.aide.highlight;


import android.content.Context;
import android.content.SharedPreferences;
import io.github.zeroaicy.util.ContextUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class CodeTheme {
	public static final int NORMAL = 0;      //普通样式
	public static final int BOLD = 1;        //字体加粗
	public static final int ITALIC = 2;       //斜体
	public static final int BOLD_ITALIC = 3; //字体加粗+斜体

	public static final String CodeThemeName = "CodeThemePreferences";

	static SharedPreferences codeThemePreferences;

	public static void save( Context context, ColorKind colorKind ) {
		String key = colorKind.key;
		SharedPreferences.Editor edit = codeThemePreferences.edit();
		if ( colorKind.hasTypefaceStyle() ) {
			edit.putInt(key + "_typefaceStyle", colorKind.getTypefaceStyle());			
		}
		long value = combineInt2Long(colorKind.getColor(context, true), colorKind.getColor(context, true));
		// edit.putLong(key, value).apply();
		edit.putString(key, Long.toHexString(value).toUpperCase()).apply();

	}

	public static void restore( boolean isLight ) {
		for ( ColorKind colorKind : colorKindMap.values() ) {
			colorKind.restoreDefault(isLight);
		}
		codeThemePreferences.edit().clear().commit();
	}

	public static void init( Context context ) {

		if ( !ContextUtil.isMainProcess() ) {
			return;
		}

		if ( codeThemePreferences != null ) {
			return;
		}
		codeThemePreferences = context.getSharedPreferences(CodeThemeName, Context. MODE_PRIVATE);


		// 加载自定义高亮
		Map<String, Object> customColorMap = new HashMap<String, Object>(codeThemePreferences.getAll());
		Set<String> keySet = new HashSet<String>(customColorMap.keySet());
		for ( String key : keySet ) {
			Object value = customColorMap.get(key);
			// 处理旧版
			if ( value instanceof Long ) {
				Long colors = (Long) value;
				SharedPreferences.Editor edit = codeThemePreferences.edit();
				String colorHexString = Long.toHexString(colors).toUpperCase();
				edit.remove(key).putString(key, colorHexString).apply();

				customColorMap.put(key, colorHexString);
				continue;
			}

			if ( !( value instanceof String ) ) {
				customColorMap.remove(key);
			}

		}

		// 填充自定义高亮
		for ( Map.Entry<String, ?> entry : customColorMap.entrySet() ) {
			// 无符号16进制
			long colorValue = Long.parseUnsignedLong((String) entry.getValue(), 16);
			int[] values = separateLong2int(colorValue);
			int lightColor = values[0];
			int darkColor = values[1];

			String key = entry.getKey();
			ColorKind colorKind = getColorKind(key);
			// 亮主题
			colorKind.setCustomColor(lightColor, true);
			// 暗主题
			colorKind.setCustomColor(darkColor, false);
			// 字体风格
			if ( colorKind.hasTypefaceStyle() ) {
				int typefaceStyle = codeThemePreferences.getInt(key + "_typefaceStyle", colorKind.getTypefaceStyle());
				colorKind.setTypefaceStyle(typefaceStyle);				
			}
		}

	}


	static Map<String, ColorKind> colorKindMap = new HashMap<>();
	static{
		init();
	}

	private static void init( ) {
		if ( ContextUtil.isMainProcess() ) {
			// 方便查询
			for ( ColorKind colorKind : ColorKind.values() ) {
				colorKindMap.put(colorKind.key, colorKind);
			}			
		}		
	}

	public static ColorKind getColorKind( String key ) {
		return CodeTheme.colorKindMap.get(key);
	}
	public static int getColor( String colorKindKey, Context context, boolean isLight ) {
		return CodeTheme.colorKindMap.get(colorKindKey).getColor(context, isLight);
	}
	public static int getTypefaceStyle( String colorKindKey ) {
		return CodeTheme.colorKindMap.get(colorKindKey).getTypefaceStyle();
	}



	public static long combineInt2Long( int low, int high ) {
		return ( (long)low & 0xFFFFFFFFl ) | ( ( (long)high << 32 ) & 0xFFFFFFFF00000000l );
	}
	public static int[] separateLong2int( Long val ) {
		int[] ret = new int[2];
		ret[0] = (int) ( 0xFFFFFFFFl & val );
		ret[1] = (int) ( ( 0xFFFFFFFF00000000l & val ) >> 32 );
		return ret;
	}

}
