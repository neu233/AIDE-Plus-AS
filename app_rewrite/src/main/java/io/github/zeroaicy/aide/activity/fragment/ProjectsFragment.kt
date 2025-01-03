package io.github.zeroaicy.aide.activity.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.aide.ui.rewrite.R
import com.aide.ui.rewrite.databinding.FragmentProjectsBinding
import com.aide.ui.rewrite.databinding.FragmentProjectsTabRecyclerItemBinding
import com.aide.ui.rewrite.databinding.Material3EditorLayoutBinding
import com.blankj.utilcode.util.SPStaticUtils
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.google.android.material.search.SearchView
import io.github.zeroaicy.aide.base.BaseFragment
import io.github.zeroaicy.aide.bean.ProjectChipBean
import io.github.zeroaicy.aide.ext.getProjectChipBean
import io.github.zeroaicy.aide.utils.ProjectLoader
import io.github.zeroaicy.aide.utils.ProjectLoader.ProjectListener
import org.lsposed.manager.ui.dialog.BlurBehindDialogBuilder

/*
author : 罪慾
date : 2024/12/25 12:04
description : QQ3115093767
*/


class ProjectsFragment : BaseFragment<FragmentProjectsBinding>(), ProjectListener {
    private val projectLoader: ProjectLoader = ProjectLoader.getInstance()


    private val contextualToolbarOnBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback( /* enabled= */false) {
            override fun handleOnBackPressed() {
                hideContextualToolbarAndClearSelection()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectLoader.addListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAllUI(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        projectLoader.removeListener(this)

        //adapter.unregisterAdapterDataObserver(observer);
    }

    private fun showSearchSettingsDialog() {
        val editorLayoutBinding = Material3EditorLayoutBinding.inflate(
            layoutInflater
        )
        editorLayoutBinding.textInputLayout.helperText =
            getString(R.string.fragment_searchBar_textInputLayout_helperText)
        editorLayoutBinding.textInputEditText.setText(SPStaticUtils.getString("Project_List_Path", ""))
        BlurBehindDialogBuilder(requireActivity())
            .setTitle(R.string.command_settings)
            .setView(editorLayoutBinding.root)
            .setPositiveButton(
                android.R.string.ok
            ) { _: DialogInterface?, _: Int ->
                SPStaticUtils.put(
                    "Project_List_Path",
                    editorLayoutBinding.textInputEditText.text.toString()
                )
            }
            .setNeutralButton(android.R.string.cancel, null)
            .setCancelable(true)
            .create()
            .show()
    }

    private fun setUpAllUI(savedInstanceState: Bundle?) {
        // tabLayout和recyclerView联动

        _binding.tabLayout.linear(RecyclerView.HORIZONTAL).setup {
            addType<ProjectChipBean>(R.layout.fragment_projects_tab_recycler_item)

            onBind {
                val binding = getBinding<FragmentProjectsTabRecyclerItemBinding>()
                val model = getModel<ProjectChipBean>()
                binding.chip.text = model.title
                binding.chip.setOnClickListener {
                    model.onClick.invoke(it)
                }
            }
        }.models = getProjectChipBean()

        _binding.recyclerView.linear().setup {

        }


        // menu设置
        _binding.openSearchBar.inflateMenu(R.menu.projects_searchbar_menu)
        _binding.openSearchBar.setOnMenuItemClickListener { menuItem: MenuItem ->
            // TODO
            if (menuItem.itemId == R.id.commitMenuSettings) showSearchSettingsDialog()
            true
        }

        _binding.contextualToolbar.setNavigationOnClickListener { hideContextualToolbarAndClearSelection() }
        _binding.contextualToolbar.inflateMenu(R.menu.projects_searchbar_contextual_toolbar_menu)
        _binding.contextualToolbar.setOnMenuItemClickListener { menuItem: MenuItem ->
            val menuId = menuItem.itemId
            when (menuId) {
                R.id.commitMenuSelectAll -> {
                    //setItemsSelected(true);
                    // TODO("全选")
                    _binding.contextualToolbar.setTitle(R.string.command_select)
                    return@setOnMenuItemClickListener true
                }

                R.id.commitMenuInverse -> {
                    /**setItemsInverse(); */
                    /**setItemsInverse(); */
                    /**setItemsInverse(); */
                    // TODO("反选")
                }

                R.id.commitMenuClear -> {
                    hideContextualToolbarAndClearSelection()
                    return@setOnMenuItemClickListener true
                }

                R.id.commitMenuDelete -> {
                    // TODO
                }
            }
            false
        }


        // openSearchView联动
        _binding.openSearchView
            .editText
            .setOnEditorActionListener { _: TextView?, _: Int, _: KeyEvent? -> false }
        val onBackPressedCallback: OnBackPressedCallback =
            object : OnBackPressedCallback( /* enabled= */false) {
                override fun handleOnBackPressed() {
                    _binding.openSearchView.hide()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        _binding.openSearchView.addTransitionListener { _: SearchView?, _: SearchView.TransitionState?, newState: SearchView.TransitionState ->
            onBackPressedCallback.isEnabled =
                newState == SearchView.TransitionState.SHOWN
        }


        // Don't start animation on rotation. Only needed in demo because minIntervalSeconds is 0.
        if (savedInstanceState == null) {
            _binding.openSearchBar.startOnLoadAnimation()
        }

        ViewCompat.setOnApplyWindowInsetsListener(_binding.contextualToolbarContainer) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, contextualToolbarOnBackPressedCallback)




    }

    private fun hideContextualToolbarAndClearSelection() {
        //Adapter.selectionModeEnabled = false
        if (collapseContextualToolbar()) {
            //setItemsSelected(false);
            // TODO
        }
    }

    private fun expandContextualToolbar() {
        contextualToolbarOnBackPressedCallback.isEnabled = true
        _binding.openSearchBar.expand(_binding.contextualToolbarContainer, _binding.appBarLayout)
    }

    private fun collapseContextualToolbar(): Boolean {
        contextualToolbarOnBackPressedCallback.isEnabled = false
        return _binding.openSearchBar.collapse(_binding.contextualToolbarContainer, _binding.appBarLayout)
    }


}
