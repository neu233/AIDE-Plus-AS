package io.github.zeroaicy.aide.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.aide.ui.rewrite.R
import com.aide.ui.rewrite.databinding.ActivityMainRewriteBinding
import com.blankj.utilcode.util.SPStaticUtils
import io.github.zeroaicy.aide.base.BaseAppActivity

/*
author : 罪慾
date : 2024/12/25 09:58
description : QQ3115093767
*/


const val isDrawerOpened = "isDrawerOpened"


class MainActivityRewrite : BaseAppActivity<ActivityMainRewriteBinding>() {
    private lateinit var mDrawerToggle: ActionBarDrawerToggle


    private val onBackPressedCallback by lazy {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isDrawerOpened()) {
                    closeDrawer()
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()

        setupDrawer()

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)


    }



    @SuppressLint("ClickableViewAccessibility")
    private fun setupDrawer() {
        setSupportActionBar(_binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mDrawerToggle = ActionBarDrawerToggle(
            this,
            _binding.drawerLayout,
            _binding.toolbar,
            R.string.app_string_desc_open,
            R.string.app_string_desc_close
        )

        _binding.drawerLayout.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()

        _binding.toolbar.setNavigationOnClickListener {
            if (_binding.drawerLayout.isOpen) {
                closeDrawer()
            } else {
                openDrawer()
            }
        }

        _binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(view: View, v: Float) {
            }

            override fun onDrawerOpened(view: View) {
                lockDrawer()
            }

            override fun onDrawerClosed(view: View) {
                unlockDrawer()
            }

            override fun onDrawerStateChanged(i: Int) {
                SPStaticUtils.put(isDrawerOpened, isDrawerOpened())
            }
        })

        _binding.drawerLayout.setOnTouchListener { _: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                closeDrawer()
            }
            false
        }

        if (SPStaticUtils.getBoolean(isDrawerOpened, isDrawerOpened())) {
            openDrawer()
        }
    }

    fun openDrawer() {
        _binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    fun closeDrawer() {
        _binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    fun lockDrawer() {
        _binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN)
    }

    fun unlockDrawer() {
        _binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    fun isDrawerLocked(): Boolean =
        _binding.drawerLayout.getDrawerLockMode(GravityCompat.START) == DrawerLayout.LOCK_MODE_LOCKED_OPEN

    fun isDrawerOpened(): Boolean = _binding.drawerLayout.isDrawerOpen(GravityCompat.START)
}
