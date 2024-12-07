package io.github.zeroaicy.aide.ui.services;

import android.app.Activity;
import android.text.TextUtils;
import com.aide.common.AppLog;
import com.aide.ui.ServiceContainer;
import com.aide.ui.services.DownloadService;
import com.aide.ui.services.MavenService;
import com.aide.ui.util.ArtifactNode;
import com.aide.ui.util.BuildGradle;
import com.aide.ui.util.MavenMetadataXml;
import com.aide.ui.util.PomXml;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import io.github.zeroaicy.util.FileUtil;
import android.util.Log;

public class DownloadMavenLibraries implements Callable<Void> {

	private static String TAG = "DownloadMavenLibraries";
	private Runnable downloadCompleteCallback;
    private List<BuildGradle.MavenDependency> deps;
    private final List<BuildGradle.RemoteRepository> remoteRepositorys = new ArrayList<>();
    private final Activity activity;
    protected final DownloadService downloadService;

	private static final BuildGradle.RemoteRepository defaultRemoteRepository = new BuildGradle.RemoteRepository(1, "https://maven.aliyun.com/repository/public");

    public DownloadMavenLibraries(DownloadService downloadService, Activity activity, List<BuildGradle.MavenDependency> deps, List<BuildGradle.RemoteRepository> remoteRepositorys, Runnable completeCallback) {

		this.downloadService = downloadService;
		this.activity = activity;
		this.downloadCompleteCallback = completeCallback;
		this.deps = deps;

		deduplication(remoteRepositorys);
    }

	private void deduplication(List<BuildGradle.RemoteRepository> remoteRepositorys) {
		// 过滤重复仓库
		Set<String> remoteRepositorySet = new HashSet<>();
		//
		remoteRepositorySet.add(defaultRemoteRepository.repositorieURL);
		// 添加默认maven仓库
		this.remoteRepositorys.add(defaultRemoteRepository);


		// 过滤重复maven仓库
		for (BuildGradle.RemoteRepository remoteRepository : remoteRepositorys) {
			if (remoteRepositorySet.contains(remoteRepository.repositorieURL)) {
				// 过滤重复仓库
				continue;
			}

			// 标记
			remoteRepositorySet.add(remoteRepository.repositorieURL);

			this.remoteRepositorys.add(remoteRepository);
		}
	}

	public boolean resolvingMetadataFile(BuildGradle.MavenDependency dep, int count, String mavenMetadataPath, BuildGradle.RemoteRepository remoteRepository) {
		String mavenMetadataUrl = MavenService.getMetadataUrl(remoteRepository, dep);
		try {

			StringBuilder sb = new StringBuilder();

			sb.append("metadata -> ");

			sb.append(dep.groupId);
			sb.append(":");
			sb.append(dep.artifactId);
			sb.append(":");
			sb.append(dep.version);

			String dependencyString = sb.toString();
			// 下载清单文件
			DownloadService.Hw(this.downloadService, dependencyString, (count * 100) / this.deps.size(), 0);
			//已存在 长度不一致时更新
			DownloadService.downloadFile(this.downloadService, mavenMetadataUrl, mavenMetadataPath, false);
		}
		catch (Throwable unused) {
			AppLog.d(TAG, "Maven仓库%s -> %s\n %s", remoteRepository.repositorieURL, mavenMetadataUrl, Log.getStackTraceString(unused));
			return false;
		}
		// 检查文件是否存在
		if (!new File(mavenMetadataPath).exists()) {
			return false;
		}
		MavenMetadataXml metadataXml = new MavenMetadataXml().getConfiguration(mavenMetadataPath);
		//查看maven-metadata.xml是否下载成功
		String version = metadataXml.getVersion(dep.version);

		if (version == null) {
			AppLog.d(TAG, "metadata versions: %s -> dep version %s", String.valueOf(metadataXml.Zo), dep.version);
			return true;
		}

		if (!dep.version.endsWith("+")
			&& !MavenMetadataXml.matchVersion(version, dep.version)) {
			// 不是动态匹配, 必须一致
			AppLog.d(TAG, "非动态匹配, 必须一致 metadata version: %s -> dep version %s", version, dep.version);
			return false;
		}

		// 更新依赖库版本
		dep.version = version;
		return true;
	}

    @Override
    public Void call() {
		//是否有已完成的下载
		boolean downloadComplete = false;
		
		int count = 0;
		for (BuildGradle.MavenDependency dep : this.deps) {
			try {
				//遍历远程仓库
				for (BuildGradle.RemoteRepository remoteRepository : this.remoteRepositorys) {
					try {

						String mavenMetadataPath = MavenService.getMetadataPath(remoteRepository, dep);

						if (!resolvingMetadataFile(dep, count, mavenMetadataPath, remoteRepository)) {
							// 下载失败 仓库有问题[跳过]
							continue;
						}

						// 下载pom文件
						final String version = dep.version;

						// 下载[成功|失败]
						if (!downloadArtifactFile(remoteRepository, dep, version, ".pom", count)) {
							continue;
						}

						String pomPath = MavenService.getArtifactPath(remoteRepository, dep, dep.version, ".pom");

						// 解析pom
						PomXml configuration = PomXml.empty.getConfiguration(pomPath);

						// pom中的 packaging 
						String curPackaging = configuration.getPackaging();

						// 父类依赖认为是 pom
						// 或当前pom 声明是pom
						String classifier = ArtifactNode.getClassifier(dep);

						if (classifier == null 
							&& ("pom".equals(curPackaging)
							|| "pom".equals(dep.packaging)
							|| "bom".equals(curPackaging))) {
							count++;
							// downloadComplete = true;
							break;
						}

						// 更新type
						// 从父依赖解析出来的，最为准确
						dep.packaging = curPackaging;
						// 默认不尝试
						boolean isAttemptn = false;

						// 没有packaging信息，启用尝试模式
						if (classifier != null || TextUtils.isEmpty(dep.packaging)) {
							// 启用尝试 下载aar模式
							isAttemptn = true;
							dep.packaging = "aar";
						}

						String artifactType = "." + dep.packaging;
						//下载
						if (downloadArtifactFile(remoteRepository, dep, version, artifactType, count)) {
							count++;
							downloadComplete = true;
							break;
						}

						// 失败接着尝试
						if (isAttemptn) {
							dep.packaging = "jar";
							artifactType = "." + dep.packaging;
							if (downloadArtifactFile(remoteRepository, dep, version, artifactType, count)) {
								count++;
								downloadComplete = true;
								break;
							}
						}
					}
					catch (Throwable e) {
						AppLog.d("仓库" + remoteRepository.repositorieURL + "错误 mavenMetadataUrl: ", e);
						AppLog.e("Maven Download 仓库 ", remoteRepository.repositorieURL, e);
						continue;
					}
				}
			}
			catch (Throwable e) {
				e.printStackTrace();
				continue;
			}
		}
		final boolean downloadComplete2 = downloadComplete;
		// 回调通知[下载完成]
		ServiceContainer.aj(new Runnable(){
				@Override
				public void run() {
					DownloadService.FH(DownloadMavenLibraries.this.downloadService);
					if (downloadComplete2) {
						DownloadMavenLibraries.this.downloadCompleteCallback.run();
					}
				}
			});
		return null;
	}

	public boolean downloadArtifactFile(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency, String version, String artifactType, int count) {

		String artifactUrl = MavenService.getArtifactUrl(remoteRepository, dependency, version, artifactType);
		
		String artifactPath = MavenService.getArtifactPath(remoteRepository, dependency, version, artifactType);

		File artifactFile = new File(artifactPath);
		if (artifactFile.exists()) {
			return true;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(dependency.groupId);
		sb.append(":");
		sb.append(dependency.artifactId);
		sb.append(":");
		sb.append(version);

		String classifier = ArtifactNode.getClassifier(dependency);
		if (classifier != null) {
			sb.append(":");
			sb.append(classifier);
		}
		if (artifactType != null) {
			sb.append("@");
			sb.append(artifactType);			
		}
		String dependencyString = sb.toString();

		try {
			//通知下载进度
			DownloadService.Hw(this.downloadService, dependencyString, (count * 100) / this.deps.size(), 0);
			//如果文件存在且长度一致则不下载
			DownloadService.downloadFile(this.downloadService, artifactUrl, artifactPath, true);
		}
		catch (Throwable unused) {
			AppLog.e("Maven Download", dependencyString, unused);
			FileUtil.deleteFolder(artifactPath);
			return false;
		}

		return artifactFile.exists();
	}


	/**
	 * 旧的实现
	 */
	public Void call3() {
        String buildMavenMetadataPath = null;
        String version;
        String aarPath;
        String jarUrl;
        String jarPath;
        String jarType = ".jar";
        String aarType = ".aar";
        String pomType = ".pom";
        try {

            ArrayList<BuildGradle.RemoteRepository> remoteRepositorys = new ArrayList<>();

            for (BuildGradle.RemoteRepository remoteRepository : this.remoteRepositorys) {
                if (!remoteRepositorys.contains(remoteRepository)) {
                    remoteRepositorys.add(remoteRepository);
                }
            }

            Iterator<BuildGradle.MavenDependency> it = this.deps.iterator();
            boolean z2 = false;
            int i = 0;
            while (it.hasNext()) {

                if (Thread.interrupted()) {
					throw new InterruptedException();
				}

				BuildGradle.MavenDependency dependency = it.next();
				for (BuildGradle.RemoteRepository remoteRepository : remoteRepositorys) {
					try {
						String buildMavenMetadataUrl = MavenService.getMetadataUrl(remoteRepository, dependency);
						buildMavenMetadataPath = MavenService.getMetadataPath(remoteRepository, dependency);
						//下载
						DownloadService.downloadFile(this.downloadService, buildMavenMetadataUrl, buildMavenMetadataPath, false);
					}
					catch (Exception unused) {
					}
					// aM url
					// XL path
					if (new File(buildMavenMetadataPath).exists() && (version = new MavenMetadataXml().getConfiguration(buildMavenMetadataPath).getVersion(dependency.version)) != null) {
						String pomUrl = MavenService.getArtifactUrl(remoteRepository, dependency, version, pomType);
						String pomPath = MavenService.getArtifactPath(remoteRepository, dependency, version, pomType);

						String aarUrl = MavenService.getArtifactUrl(remoteRepository, dependency, version, aarType);
						aarPath = MavenService.getArtifactPath(remoteRepository, dependency, version, aarType);

						jarUrl = MavenService.getArtifactUrl(remoteRepository, dependency, version, jarType);
						jarPath = MavenService.getArtifactPath(remoteRepository, dependency, version, jarType);


						if ((!new File(jarPath).exists() 
							&& !new File(aarPath).exists()) 

							|| !new File(pomPath).exists()) {

							StringBuilder sb = new StringBuilder();
							sb.append(dependency.groupId);
							sb.append(":");
							sb.append(dependency.artifactId);
							sb.append(":");
							sb.append(version);
							String dependencyString = sb.toString();

							//通知下载进度
							DownloadService.Hw(this.downloadService, dependencyString, (i * 100) / this.deps.size(), 0);
							//下载 复用已有下载[长度一致]
							DownloadService.downloadFile(this.downloadService, pomUrl, pomPath, true);
							DownloadService.downloadFile(this.downloadService, aarUrl, aarPath, true);
							DownloadService.downloadFile(this.downloadService, jarUrl, jarPath, true);


							i++;
							if ((new File(jarPath).exists() 
								|| new File(aarPath).exists()) 
								&& new File(pomPath).exists()) {
								z2 = true;
								break;
							}
						}
					}
				}
            }

			final boolean downloadComplete2 = z2;
            ServiceContainer.aj(new Runnable(){
					@Override
					public void run() {
						DownloadService.FH(downloadService);
						if (downloadComplete2) {
							downloadCompleteCallback.run();
						}
					}
				});
            return null;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
}
