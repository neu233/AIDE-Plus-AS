package io.github.zeroaicy.aide.activity.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.aide.ui.rewrite.R
import com.aide.ui.rewrite.databinding.FragmentProjectCreateBinding
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.google.android.material.tabs.TabLayout
import io.github.zeroaicy.aide.base.BaseFragment
import kotlinx.coroutines.launch

/*
author : 罪慾
date : 2024/12/27 21:46
description : QQ3115093767
*/

class CreateProjectFragment : BaseFragment<FragmentProjectCreateBinding>() {

    private lateinit var adapter: BindingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingData()
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()

    }

    private fun setUpUI() {
        _binding.toolbar.setTitle(R.string.fragment_create_project_toolbar_title)
        _binding.toolbar.setNavigationOnClickListener {
            navigateUp()
        }

        _binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        adapter = _binding.recycler.linear().setup {
            //addType<>()
        }
    }

    private fun loadingData() {
        mScope.launch {

        }
    }


}






