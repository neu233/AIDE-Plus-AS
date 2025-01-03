package io.github.zeroaicy.aide.activity.fragment;


/*
author : 罪慾
date : 2024/12/28 14:33
description : QQ3115093767
*/

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.aide.ui.rewrite.R;
import com.aide.ui.rewrite.databinding.FragmentProjectCreateBinding;
import io.github.zeroaicy.aide.base.BaseDialogFragment;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FullScreenFragment extends BaseDialogFragment<FragmentProjectCreateBinding> {

    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public @NotNull Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext(), getThemeResId());
        Context context = dialog.getContext();
        initBackground(dialog, context);
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();


        Window window = requireDialog().getWindow();
        // Dialogs use a background with an InsetDrawable by default, so we have to replace it.
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(getMaterialShapeDrawable());
        //enableEdgeToEdgeIfNeeded(window);
    }

    private int getThemeResId() {
        return resolveOrThrow(Objects.requireNonNull(getContext()), R.attr.materialCalendarFullscreenTheme);
    }
}
