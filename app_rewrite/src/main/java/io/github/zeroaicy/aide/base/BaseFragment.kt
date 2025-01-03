package io.github.zeroaicy.aide.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.transition.TransitionInflater
import androidx.viewbinding.ViewBinding
import com.aide.ui.rewrite.R
import io.github.zeroaicy.aide.utils.FragmentBinding
import io.github.zeroaicy.aide.utils.FragmentBindingDelegate
import io.github.zeroaicy.aide.utils.ObserverWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


/*
author : 罪慾
date : 2024/12/25 10:41
description : QQ3115093767
*/



abstract class BaseFragment<VB : ViewBinding> : Fragment(),
    FragmentBinding<VB> by FragmentBindingDelegate() {

    protected val mScope = CoroutineScope(Dispatchers.Default)


    private val navOptions = NavOptions.Builder()
        .setEnterAnim(R.anim.fragment_enter)
        .setExitAnim(R.anim.fragment_exit)
        .setPopEnterAnim(R.anim.fragment_enter_pop)
        .setPopExitAnim(R.anim.fragment_exit_pop)
        .build()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        ObserverWrapper(viewLifecycleOwner) {}.attach()
        return createViewWithBinding(inflater, container)
    }


    fun navigateUp() {
        navController.navigateUp()
    }

    val navController: NavController
        get() {
            return NavHostFragment.findNavController(this)
        }

    fun safeAnimNavigate(
        @IdRes resId: Int,
        args: Bundle? = null,
    ): Boolean {
        try {
            navController.navigate(
                resId,
                args,
                navOptions,
            )
            return true
        } catch (ignored: IllegalArgumentException) {
            return false
        }
    }


    fun safeAnimNavigate(
        direction: NavDirections?,
    ): Boolean {

        try {
            navController.navigate(
                direction!!,
                navOptions,
            )
            return true
        } catch (ignored: IllegalArgumentException) {
            return false
        }
    }

    fun safeNavigate(
        @IdRes resId: Int
    ): Boolean {
        try {
            navController.navigate(resId)
            return true
        } catch (ignored: IllegalArgumentException) {
            return false
        }
    }

    fun safeNavigate(direction: NavDirections?): Boolean {
        try {
            navController.navigate(direction!!)
            return true
        } catch (ignored: IllegalArgumentException) {
            return false
        }
    }

    fun setUpSharedElementEnterTransition() {
        val materialContainerTransform = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.slide_top)
        /*MaterialContainerTransform(requireContext(), true).apply {
        containerColor = MaterialColors.getColor(_binding.root, R.attr.colorSurface)
        fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
    }*/
        sharedElementEnterTransition = materialContainerTransform

    }

}
