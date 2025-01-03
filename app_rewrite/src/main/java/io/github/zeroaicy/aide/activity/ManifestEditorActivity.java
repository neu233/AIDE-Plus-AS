package io.github.zeroaicy.aide.activity;


/*
author : 罪慾
date : 2024/12/24 22:14
description : QQ3115093767
*/

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aide.ui.ThemedActionbarActivity;
import com.aide.ui.rewrite.R;
import com.aide.ui.rewrite.databinding.ActivityManifestEditorAllItemBinding;
import com.aide.ui.rewrite.databinding.ActivityManifestEditorBinding;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ManifestEditorActivity extends ThemedActionbarActivity {

    private ActivityManifestEditorBinding binding;

    private List<AllTagBean> allTagBeans = new ArrayList<>();

    private MyAdapter myAdapter;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityManifestEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getActionBar().setTitle(R.string.activity_manifest_actionBar_title);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        allTagBeans.add(
                new AllTagBean(
                        getString(R.string.activity_manifest_recycler_activity),
                        getString(R.string.activity_manifest_recycler_activity_description),
                        R.drawable.ic_app_launcher,
                        (v) -> {

                        }
                )
        );

        allTagBeans.add(
                new AllTagBean(
                        getString(R.string.activity_manifest_recycler_uses_permission),
                        getString(R.string.activity_manifest_recycler_uses_permission_description),
                        R.drawable.ic_app_launcher,
                        (v) -> {

                        }
                )
        );

        allTagBeans.add(
                new AllTagBean(
                        getString(R.string.activity_manifest_recycler_permission),
                        getString(R.string.activity_manifest_recycler_permission_description),
                        R.drawable.ic_app_launcher,
                        (v) -> {

                        }
                )
        );


        allTagBeans.add(
                new AllTagBean(
                        getString(R.string.activity_manifest_recycler_application),
                        getString(R.string.activity_manifest_recycler_application_description),
                        R.drawable.ic_app_launcher,
                        (v) -> {

                        }
                )
        );

        myAdapter = new MyAdapter(allTagBeans);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.recyclerView.setAdapter(myAdapter);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }


    static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        public final List<AllTagBean> allTagBeans;

        public MyAdapter(List<AllTagBean> allTagBeans) {
            this.allTagBeans = allTagBeans;
        }

        @NonNull
        @Override
        public @NotNull MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
            ActivityManifestEditorAllItemBinding binding = ActivityManifestEditorAllItemBinding.inflate((LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            return new MyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ManifestEditorActivity.MyViewHolder myViewHolder, int i) {
            AllTagBean allTagBean = allTagBeans.get(i);
            myViewHolder.binding.title.setText(allTagBean.name);
            myViewHolder.binding.subTitle.setText(allTagBean.description);
            myViewHolder.binding.icon.setImageResource(allTagBean.icon);
            myViewHolder.binding.getRoot().setOnClickListener(allTagBean.onClickListener);
        }

        @Override
        public int getItemCount() {
            return allTagBeans.size();
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {


        public final ActivityManifestEditorAllItemBinding binding;

        public MyViewHolder(@NonNull ActivityManifestEditorAllItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    static class AllTagBean implements Serializable {
        public String name;
        public String description;
        public @DrawableRes int icon;
        public View.OnClickListener onClickListener;

        public AllTagBean(String name, String description, @DrawableRes int icon, View.OnClickListener onClickListener) {
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.onClickListener = onClickListener;
        }
    }

}
