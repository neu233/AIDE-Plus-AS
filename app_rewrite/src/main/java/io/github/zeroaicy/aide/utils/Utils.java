package io.github.zeroaicy.aide.utils;


/**
 * 功能工具类
 * 未分类的可以优先放这
 */
public class Utils{
	
	public static long nowTime(){
		return System.currentTimeMillis();
	}
	
	
	public static int parseInt(String parseInt, int defaultValue){
		try {
			return Integer.parseInt(parseInt);
		}
		catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}
