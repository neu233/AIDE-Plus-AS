package io.github.zeroaicy.aide.utils;


/*
author : 罪慾
date : 2024/12/26 21:16
description : QQ3115093767
*/

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import androidx.annotation.Nullable;
import com.blankj.utilcode.util.SPStaticUtils;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.github.zeroaicy.util.ContextUtil;
import io.github.zeroaicy.util.Log;
import okhttp3.Request;
import okhttp3.ResponseBody;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import static io.github.zeroaicy.aide.App.appGitHubFileUrl;
import static io.github.zeroaicy.aide.utils.HttpUtil.getOkHttpClient;

public class ProjectLoader {

    private static ProjectLoader instance = null;

    private boolean projectLoaded = false;
    private boolean repoLoaded = false;

    @SuppressLint("NewApi")
    private final Path repoFile = Paths.get(ContextUtil.getContext().getFilesDir().getAbsolutePath(), "repo.json");
    private static final String repoUrl = appGitHubFileUrl;

    private Map<String, Project> onlineProjects = new HashMap<>();

    private Map<String, Project> localProjects = new HashMap<>();


    private final Set<ProjectLoader.ProjectListener> listeners = ConcurrentHashMap.newKeySet();


    public boolean isLoaded() {
        return projectLoaded;
    }


    public boolean isRepoLoaded() {
        return repoLoaded;
    }


    public static synchronized ProjectLoader getInstance() {
        if (instance == null) {
            instance = new ProjectLoader();
            Executors.newCachedThreadPool().submit(() -> instance.loadLocalData());
        }
        return instance;
    }

    @SuppressLint("NewApi")
    synchronized public void loadLocalData() {
        projectLoaded = false;
        String projectListPath = SPStaticUtils.getString("Project_List_Path", "");




    }

    @SuppressLint("NewApi")
    synchronized public void loadRepoData(boolean updateRemoteRepo) {
        repoLoaded = false;
        try {
            if (Files.notExists(repoFile)) {
                loadRepoRemoteData();
                updateRemoteRepo = false;
            }
            byte[] encoded = Files.readAllBytes(repoFile);
            String bodyString = new String(encoded, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Map<String, Project> modules = new HashMap<>();
            Project[] repoModules = gson.fromJson(bodyString, Project[].class);
            Arrays.stream(repoModules).forEach(onlineModule -> {
                        modules.put(onlineModule.getName(), onlineModule);
                        for (ProjectListener listener : listeners) {
                            listener.onAddProjectLoaded(onlineModule);
                        }
                    }
            );
            onlineProjects = modules;
        } catch (Throwable t) {
            Log.e("loadLocalData", android.util.Log.getStackTraceString(t));
            for (ProjectListener listener : listeners) {
                listener.onProjectFailed(t);
            }
        } finally {
            repoLoaded = true;
            for (ProjectListener listener : listeners) {
                listener.onProjectLoaded();
            }
            if (updateRemoteRepo) loadRepoRemoteData();
        }
    }

    @SuppressLint("NewApi")
    synchronized public void loadRepoRemoteData() {
        repoLoaded = false;
        try {
            try (var response = getOkHttpClient().newCall(new Request.Builder().url(repoUrl + "modules.json").build()).execute()) {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        try {
                            String bodyString = body.string();
                            Files.write(repoFile, bodyString.getBytes(StandardCharsets.UTF_8));
                            loadRepoData(false);
                        } catch (Throwable t) {
                            Log.e("loadRemoteData", android.util.Log.getStackTraceString(t));
                            for (ProjectListener listener : listeners) {
                                listener.onProjectFailed(t);
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            Log.e("loadRemoteData", "load remote data", e);
            for (ProjectListener listener : listeners) {
                listener.onProjectFailed(e);
            }

        }
    }

    public void addListener(ProjectListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ProjectListener listener) {
        listeners.remove(listener);
    }


    @Nullable
    public Project getRepoProject(String packageName) {
        return repoLoaded && packageName != null ? onlineProjects.get(packageName) : null;
    }


    @Nullable
    public Collection<Project> getRepoProjects() {
        return repoLoaded ? onlineProjects.values() : null;
    }


    @Nullable
    public Project getLocalProject(String path) {
        return repoLoaded && path != null ? onlineProjects.get(path) : null;
    }


    @Nullable
    public Collection<Project> getLocalProjects() {
        return repoLoaded ? onlineProjects.values() : null;
    }


    public interface ProjectListener {

        default void onProjectLoaded() {
        }

        default void onAddProjectLoaded(Project project) {
        }

        default void onProjectFailed(Throwable t) {
            Log.e("onProjectFailed", "load local project failed", t);
        }

    }

    public static class Project implements Serializable {
        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("url")
        @Expose
        private String url;

        private String path;

        private Bitmap icon;

        private boolean isLocal;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Bitmap getIcon() {
            return icon;
        }

        public void setIcon(Bitmap icon) {
            this.icon = icon;
        }

        public boolean isLocal() {
            return isLocal;
        }

        public void setLocal(boolean local) {
            isLocal = local;
        }

    }
}
