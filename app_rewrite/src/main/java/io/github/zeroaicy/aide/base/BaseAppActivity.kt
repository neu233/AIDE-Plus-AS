package io.github.zeroaicy.aide.base

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewbinding.ViewBinding
import com.aide.ui.rewrite.R
import com.hjq.language.MultiLanguages
import com.hjq.language.OnLanguageListener
import io.github.zeroaicy.aide.preference.ZeroAicySetting
import io.github.zeroaicy.aide.ui.windowpreferences.WindowPreferencesManager
import io.github.zeroaicy.aide.utils.ActivityBinding
import io.github.zeroaicy.aide.utils.ActivityBindingDelegate
import io.github.zeroaicy.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*

/*
author : 罪慾
date : 2024/12/25 09:59
description : QQ3115093767
*/


abstract class BaseAppActivity<VB : ViewBinding> : AppCompatActivity(),
    ActivityBinding<VB> by ActivityBindingDelegate() {

    protected val mScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (shouldApplyEdgeToEdgePreference()) {
            val windowPreferencesManager = WindowPreferencesManager(this)
            windowPreferencesManager.applyEdgeToEdgePreference(window)
        }

        this.setContentViewWithBinding()
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        super.onConfigurationChanged(configuration)
        initTheme()
    }

    private fun initTheme() {
        // 是否需要重启

        val restart = booleanArrayOf(false)

        if (ZeroAicySetting.enableFollowSystem()) {
            ZeroAicySetting.setLightTheme(ZeroAicySetting.isNightMode(this))
        }
        if (ZeroAicySetting.isLightTheme()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        restart[0] = true

        MultiLanguages.setOnLanguageListener(object : OnLanguageListener {
            override fun onAppLocaleChange(oldLocale: Locale, newLocale: Locale) {
                restart[0] = true
                Log.i(
                    "MultiLanguages",
                    "监听到应用切换了语种，旧语种：$oldLocale，新语种：$newLocale"
                )
            }

            override fun onSystemLocaleChange(oldLocale: Locale, newLocale: Locale) {
                restart[0] = true
                Log.i(
                    "MultiLanguages", "监听到系统切换了语种，旧语种：" + oldLocale + "，新语种：" + newLocale +
                            "，是否跟随系统：" + MultiLanguages.isSystemLanguage(this@BaseAppActivity)
                )
            }
        })

        if (restart[0]) {
            // recreate();
            startActivity(Intent(this, this.javaClass))
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.activity_alpha_in, R.anim.activity_alpha_out)
            finish()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        // 绑定语种
        super.attachBaseContext(MultiLanguages.attach(newBase))
    }

    open fun shouldApplyEdgeToEdgePreference(): Boolean {
        return true
    }

}
