package io.github.zeroaicy.aide.activity;


/*
author : 罪慾
date : 2024/12/24 22:20
description : QQ3115093767
*/

import android.os.Bundle;
import com.aide.ui.ThemedActionbarActivity;
import com.aide.ui.rewrite.databinding.ActivityIconsAddBinding;
import io.github.zeroaicy.aide.base.BaseActivity;

public class IconsActivity extends ThemedActionbarActivity {

    // https://github.com/google/material-design-icons/archive/master.zip


    private ActivityIconsAddBinding binding;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityIconsAddBinding.inflate(getLayoutInflater());


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}
