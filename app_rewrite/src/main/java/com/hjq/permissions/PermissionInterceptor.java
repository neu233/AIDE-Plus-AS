package com.hjq.permissions;

import android.app.Activity;
import java.util.List;
import java.util.ArrayList;

public class PermissionInterceptor implements IPermissionInterceptor {
	/**
     * 发起权限申请（可在此处先弹 Dialog 再申请权限，如果用户已经授予权限，则不会触发此回调）
     *
     * @param allPermissions            申请的权限
     * @param callback                  权限申请回调
     */
    public void launchPermissionRequest(Activity activity, List<String> allPermissions,
										OnPermissionCallback callback) {
		PermissionFragment.launch(activity, new ArrayList<String>(allPermissions), this, callback);
	}

    /**
     * 用户授予了权限（注意需要在此处回调 {@link OnPermissionCallback#onGranted(List, boolean)}）
     *
     * @param allPermissions             申请的权限
     * @param grantedPermissions         已授予的权限
     * @param allGranted                 是否全部授予
     * @param callback                   权限申请回调
     */
    public void grantedPermissionRequest(Activity activity, List<String> allPermissions,
										 List<String> grantedPermissions, boolean allGranted,
										 OnPermissionCallback callback) {
		if (callback == null) {
			return;
		}
		callback.onGranted(grantedPermissions, allGranted);
	}

    /**
     * 用户拒绝了权限（注意需要在此处回调 {@link OnPermissionCallback#onDenied(List, boolean)}）
     *
     * @param allPermissions            申请的权限
     * @param deniedPermissions         已拒绝的权限
     * @param doNotAskAgain             是否勾选了不再询问选项
     * @param callback                  权限申请回调
     */
    public void deniedPermissionRequest(Activity activity, List<String> allPermissions,
										List<String> deniedPermissions, boolean doNotAskAgain,
										OnPermissionCallback callback) {
		if (callback == null) {
			return;
		}
		callback.onDenied(deniedPermissions, doNotAskAgain);
	}


    /**
     * 权限请求完成
     *
     * @param allPermissions            申请的权限
     * @param skipRequest               是否跳过了申请过程
     * @param callback                  权限申请回调
     */
    public void finishPermissionRequest(Activity activity, List<String> allPermissions,
										boolean skipRequest, OnPermissionCallback callback) {
		
	}
}
