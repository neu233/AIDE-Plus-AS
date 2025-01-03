@file:Suppress("UNCHECKED_CAST", "unused")


package io.github.zeroaicy.aide.utils

/**
 * @author: 罪慾
 * @github: https://github.com/neu233/
 * @mail: 3115093767@qq.com
 * @createTime: 2024/9/10
 */

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.aide.ui.rewrite.R
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType

object ViewBindingUtil {

    @JvmStatic
    fun <VB : ViewBinding> inflateWithGeneric(genericOwner: Any, layoutInflater: LayoutInflater) =
        withGenericBindingClass(genericOwner) { clazz ->
            clazz.getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as VB
        }.withLifecycleOwner(genericOwner)

    @JvmStatic
    fun <VB : ViewBinding> inflateWithGeneric(genericOwner: Any, parent: ViewGroup): VB =
        inflateWithGeneric(genericOwner, LayoutInflater.from(parent.context), parent, false)

    @JvmStatic
    fun <VB : ViewBinding> inflateWithGeneric(
        genericOwner: Any,
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) =
        withGenericBindingClass(genericOwner) { clazz ->
            clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
                .invoke(null, layoutInflater, parent, attachToParent) as VB
        }.withLifecycleOwner(genericOwner)

    @JvmStatic
    @Suppress("DEPRECATION")
    fun <VB : ViewBinding> getBindingWithGeneric(genericOwner: Any, view: View): VB =
        view.getTag(R.id.tag_view_binding) as? VB ?: bindWithGeneric<VB>(genericOwner, view)
            .also { view.setTag(R.id.tag_view_binding, it) }

    @JvmStatic
    @Deprecated(
        "Use ViewBindingUtil.getBindingWithGeneric<VB>(genericOwner, view) instead.",
        ReplaceWith("ViewBindingUtil.getBindingWithGeneric<VB>(genericOwner, view)")
    )
    fun <VB : ViewBinding> bindWithGeneric(genericOwner: Any, view: View) =
        withGenericBindingClass(genericOwner) { clazz ->
            clazz.getMethod("bind", View::class.java).invoke(null, view) as VB
        }.withLifecycleOwner(genericOwner)

    private fun <VB : ViewBinding> VB.withLifecycleOwner(genericOwner: Any) = apply {
        if (this is ViewDataBinding && genericOwner is ComponentActivity) {
            lifecycleOwner = genericOwner
        } else if (this is ViewDataBinding && genericOwner is Fragment) {
            lifecycleOwner = genericOwner.viewLifecycleOwner
        }
    }

    private fun <VB : ViewBinding> withGenericBindingClass(genericOwner: Any, block: (Class<VB>) -> VB): VB {
        var genericSuperclass = genericOwner.javaClass.genericSuperclass
        var superclass = genericOwner.javaClass.superclass
        while (superclass != null) {
            if (genericSuperclass is ParameterizedType) {
                genericSuperclass.actualTypeArguments.forEach {
                    try {
                        return block.invoke(it as Class<VB>)
                    } catch (_: NoSuchMethodException) {
                    } catch (_: ClassCastException) {
                    } catch (e: InvocationTargetException) {
                        var tagException: Throwable? = e
                        while (tagException is InvocationTargetException) {
                            tagException = e.cause
                        }
                        throw tagException
                            ?: IllegalArgumentException("ViewBinding generic was found, but creation failed.")
                    }
                }
            }
            genericSuperclass = superclass.genericSuperclass
            superclass = superclass.superclass
        }
        throw IllegalArgumentException("There is no generic of ViewBinding.")
    }
}


interface ActivityBinding<VB : ViewBinding> {
    val _binding: VB
    fun Activity.setContentViewWithBinding()
}

class ActivityBindingDelegate<VB : ViewBinding> : ActivityBinding<VB> {
    private lateinit var binding: VB

    override val _binding: VB get() = binding

    override fun Activity.setContentViewWithBinding() {
        binding = ViewBindingUtil.inflateWithGeneric(this, layoutInflater)
        setContentView(binding.root)
    }

}


interface FragmentBinding<VB : ViewBinding> {
    val _binding: VB
    fun Fragment.createViewWithBinding(inflater: LayoutInflater, container: ViewGroup?): View
}

class FragmentBindingDelegate<VB : ViewBinding> : FragmentBinding<VB> {
    private var binding: VB? = null
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    override val _binding: VB
        get() = requireNotNull(this.binding) { "The property of binding has been destroyed." }

    override fun Fragment.createViewWithBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        if (this@FragmentBindingDelegate.binding == null) {
            this@FragmentBindingDelegate.binding = ViewBindingUtil.inflateWithGeneric(this, inflater, container, false)
            viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    handler.post { this@FragmentBindingDelegate.binding = null }
                }
            })
        }
        return this@FragmentBindingDelegate._binding.root
    }
}
