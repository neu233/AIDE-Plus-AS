package io.github.zeroaicy.aide.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.window.OnBackInvokedDispatcher
import androidx.activity.enableEdgeToEdge
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.aide.ui.rewrite.R
import com.aide.ui.rewrite.databinding.ActivityHomeBinding
import io.github.zeroaicy.aide.base.BaseAppActivity

/*
author : 罪慾
date : 2024/12/25 11:44
description : QQ3115093767
*/


class HomeActivity : BaseAppActivity<ActivityHomeBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setNavHost()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {}
        }
    }

    private fun setNavHost() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
                ?: return
        setupWithNavController(_binding!!.nav, navHostFragment.navController)
        handleIntent(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(this, R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) {
            return
        }
        if (!TextUtils.isEmpty(intent.dataString)) {
            when (intent.dataString) {
                "home" -> _binding.nav.selectedItemId = R.id.fragment_home
                "projects" -> _binding.nav.selectedItemId = R.id.nav_projects
                "settings" -> _binding.nav.selectedItemId = R.id.nav_settings
            }
        }
    }
}
