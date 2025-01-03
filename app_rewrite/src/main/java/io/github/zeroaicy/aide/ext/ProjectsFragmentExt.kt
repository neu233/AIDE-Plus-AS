package io.github.zeroaicy.aide.ext

import com.aide.ui.rewrite.R
import io.github.zeroaicy.aide.activity.fragment.ProjectsFragment
import io.github.zeroaicy.aide.bean.ProjectChipBean


/*
author : 罪慾
date : 2025/1/1 23:26
description : QQ3115093767
*/

fun ProjectsFragment.getProjectChipBean(): List<ProjectChipBean> {
    val list = mutableListOf<ProjectChipBean>()


    list.add(
        ProjectChipBean(
            title = getString(R.string.fragment_projects_tab_create),
            onClick = {
                /*it.transitionName = "CreateProject"
                _binding.root.transitionName = "CreateProject"*/
                //setUpSharedElementEnterTransition()
                safeAnimNavigate(R.id.fragment_create_project)
            }
        ))
    list.add(
        ProjectChipBean(
            title = getString(R.string.fragment_projects_tab_open),
            onClick = {

            }
        ))
    list.add(
        ProjectChipBean(
            title = getString(R.string.fragment_projects_tab_share),
            onClick = {

            }
        ))
    list.add(
        ProjectChipBean(
            title = getString(R.string.fragment_projects_tab_git),
            onClick = {
                safeAnimNavigate(R.id.fragment_git_clone)
            }
        ))



    return list
}