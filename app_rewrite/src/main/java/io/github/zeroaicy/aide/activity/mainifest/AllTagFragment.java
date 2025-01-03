package io.github.zeroaicy.aide.activity.mainifest;


/*
author : 罪慾
date : 2024/12/25 00:12
description : QQ3115093767
*/

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.aide.ui.rewrite.databinding.FragmentManifestEditorAllBinding;
import org.jetbrains.annotations.NotNull;

public class AllTagFragment extends Fragment {

    private FragmentManifestEditorAllBinding binding;

    @Nullable
    @Override
    public @org.jetbrains.annotations.Nullable View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentManifestEditorAllBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
