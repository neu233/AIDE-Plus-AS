@file:Suppress("DEPRECATION")

package io.github.zeroaicy.aide.aaptcompiler

import android.view.View
import android.view.ViewGroup
import android.widget.AbsoluteLayout
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.ViewFlipper
import android.widget.ViewSwitcher
import io.github.zeroaicy.aide.aaptcompiler.utils.BytecodeScanner
import io.github.zeroaicy.aide.aaptcompiler.utils.StyleUtils
import org.apache.bcel.Repository
import org.apache.bcel.classfile.JavaClass
import java.io.File
import java.io.IOException


/**
 * @author: 罪慾
 * @github: https://github.com/neu233/
 * @mail: 3115093767@qq.com
 * @createTime: 2024/11/1
 */


class JavaViewUtils private constructor() {

    init {
        BytecodeScanner.scanBootstrapIfNeeded()
        addFrameworkViews()
        Repository.clearCache()
    }


    companion object {
        @JvmStatic
        private val mJavaViewClasses = mutableMapOf<String, JavaClass>()

        @JvmStatic
        fun getInstance(): JavaViewUtils {
            return JavaViewUtils()
        }

        fun getJavaViewClasses(): MutableMap<String, JavaClass> {
            return mJavaViewClasses
        }


    }

    private fun addFrameworkViews() {
        addFrameworkView(View::class.java)
        addFrameworkView(ViewGroup::class.java)
        addFrameworkView(FrameLayout::class.java)
        addFrameworkView(RelativeLayout::class.java)
        addFrameworkView(LinearLayout::class.java)
        addFrameworkView(AbsoluteLayout::class.java)
        addFrameworkView(ListView::class.java)
        addFrameworkView(EditText::class.java)
        addFrameworkView(Button::class.java)
        addFrameworkView(TextView::class.java)
        addFrameworkView(ImageView::class.java)
        addFrameworkView(ImageButton::class.java)
        addFrameworkView(ImageSwitcher::class.java)
        addFrameworkView(ViewFlipper::class.java)
        addFrameworkView(ViewSwitcher::class.java)
        addFrameworkView(ScrollView::class.java)
        addFrameworkView(HorizontalScrollView::class.java)
        addFrameworkView(CompoundButton::class.java)
        addFrameworkView(ProgressBar::class.java)
        addFrameworkView(CheckBox::class.java)
    }

    private fun addFrameworkView(viewClass: Class<out View?>) {
        val repository: org.apache.bcel.util.Repository = Repository.getRepository()
        try {
            val javaClass = repository.loadClass(viewClass)
            if (javaClass != null) {
                mJavaViewClasses[javaClass.className] = javaClass
            }
        } catch (e: ClassNotFoundException) {
            // ignored
        }
    }


    fun loadJar(jarFile: File) {
        try {
            BytecodeScanner.loadJar(jarFile)
        } catch (ignored: IOException) {
        }
        try {
            val scan: MutableList<JavaClass> = BytecodeScanner.scan(jarFile)
            for (javaClass in scan) {
                StyleUtils.putStyles(javaClass)
                mJavaViewClasses[javaClass.className] = javaClass
            }
            Repository.clearCache()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


}