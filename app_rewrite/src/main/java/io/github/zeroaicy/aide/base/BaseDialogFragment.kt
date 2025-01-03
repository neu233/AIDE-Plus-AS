package io.github.zeroaicy.aide.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.R
import com.google.android.material.shape.MaterialShapeDrawable
import io.github.zeroaicy.aide.utils.FragmentBinding
import io.github.zeroaicy.aide.utils.FragmentBindingDelegate
import io.github.zeroaicy.aide.utils.ObserverWrapper

/*
author : 罪慾
date : 2024/12/25 10:41
description : QQ3115093767
*/


abstract class BaseDialogFragment<VB : ViewBinding> : DialogFragment(),
    FragmentBinding<VB> by FragmentBindingDelegate() {

    private val edgeToEdgeEnabled = false

    /*

    @SuppressLint("RestrictedApi")
    public void enableEdgeToEdgeIfNeeded(Window window) {
        if (edgeToEdgeEnabled) {
            // Avoid enabling edge-to-edge multiple times.
            return;
        }
        final View headerLayout = requireView().findViewById(R.id.app_bar);
        EdgeToEdgeUtils.applyEdgeToEdge(window, true, ViewUtils.getBackgroundColor(headerLayout), null);
        final int originalPaddingTop = headerLayout.getPaddingTop();
        final int originalPaddingLeft = headerLayout.getPaddingLeft();
        final int originalPaddingRight = headerLayout.getPaddingRight();
        final int originalHeaderHeight = headerLayout.getLayoutParams().height;
        ViewCompat.setOnApplyWindowInsetsListener(
                headerLayout,
                new OnApplyWindowInsetsListener() {
                    @Override
                    public @NotNull WindowInsetsCompat onApplyWindowInsets(@NotNull View v, @NotNull WindowInsetsCompat insets) {
                        Insets inset = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                        if (originalHeaderHeight >= 0) {
                            headerLayout.getLayoutParams().height = originalHeaderHeight + inset.top;
                            headerLayout.setLayoutParams(headerLayout.getLayoutParams());
                        }
                        headerLayout.setPadding(
                                originalPaddingLeft + inset.left,
                                originalPaddingTop + inset.top,
                                originalPaddingRight + inset.right,
                                headerLayout.getPaddingBottom());
                        return insets;
                    }
                });
        edgeToEdgeEnabled = true;
    }
*/
    var materialShapeDrawable: MaterialShapeDrawable? = null
        private set

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        ObserverWrapper(viewLifecycleOwner) {}.attach()
        return createViewWithBinding(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    @SuppressLint("PrivateResource")
    fun initBackground(dialog: Dialog, context: Context) {
        if (materialShapeDrawable == null) {
            materialShapeDrawable =
                MaterialShapeDrawable(
                    context,
                    null,
                    R.attr.materialCalendarStyle,
                    R.style.Widget_MaterialComponents_MaterialCalendar
                )

            val a =
                context.obtainStyledAttributes(
                    null,
                    R.styleable.MaterialCalendar,
                    R.attr.materialCalendarStyle,
                    R.style.Widget_MaterialComponents_MaterialCalendar
                )

            val backgroundColor = a.getColor(R.styleable.MaterialCalendar_backgroundTint, 0)

            a.recycle()

            materialShapeDrawable!!.initializeElevationOverlay(context)
            materialShapeDrawable!!.fillColor = ColorStateList.valueOf(backgroundColor)
            materialShapeDrawable!!.elevation = ViewCompat.getElevation(dialog.window!!.decorView)
        }
    }


    companion object {
        @JvmStatic
        fun resolveOrThrow(context: Context, @AttrRes attributeResId: Int): Int {
            val typedValue = TypedValue()
            if (context.theme.resolveAttribute(attributeResId, typedValue, true)) {
                return typedValue.data
            }
            throw IllegalArgumentException(context.resources.getResourceName(attributeResId))
        }
    }
}
