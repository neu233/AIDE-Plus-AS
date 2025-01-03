package io.github.zeroaicy.aide.activity.fragment

import android.os.Bundle
import android.view.View
import com.aide.ui.rewrite.databinding.FragmentProjectGitCloneBinding
import io.github.zeroaicy.aide.base.BaseFragment
import org.checkerframework.checker.units.qual.N


/*
author : 罪慾
date : 2025/1/3 17:31
description : QQ3115093767
*/

class GitCloneProjectFragment : BaseFragment<FragmentProjectGitCloneBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        _binding.toolbar.setTitle("Git Clone Project")
        _binding.toolbar.setNavigationOnClickListener {
            navigateUp()
        }



    }
}