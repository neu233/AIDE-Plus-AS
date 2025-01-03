package io.github.zeroaicy.aide.utils;


/*
author : 罪慾
date : 2024/12/26 10:40
description : QQ3115093767
*/

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import io.github.zeroaicy.util.ContextUtil;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.File;

public class HttpUtil {

    @SuppressLint("StaticFieldLeak")
    private static final Context context = ContextUtil.getContext();

    private static OkHttpClient okHttpClient;
    private static Cache okHttpCache;

    public static Cache getOkHttpCache() {
        if (okHttpCache != null) return okHttpCache;
        long size50MiB = 50 * 1024 * 1024;
        okHttpCache = new Cache(new File(context.getCacheDir(), "http_cache"), size50MiB);
        return okHttpCache;
    }

    @NonNull
    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient != null) return okHttpClient;
        var builder = new OkHttpClient.Builder()
                .cache(getOkHttpCache());
        okHttpClient = builder.build();
        return okHttpClient;
    }



}
