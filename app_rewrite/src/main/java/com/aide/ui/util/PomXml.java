//
// Decompiled by Jadx - 1059ms
//
package com.aide.ui.util;

import com.aide.common.AppLog;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import android.text.TextUtils;

/**
 * 同名覆盖底包中的类
 * 已保持
 */
public class PomXml extends Configuration<PomXml> {


    public static PomXml empty = new PomXml(true);
	private boolean isEmpty;
	public boolean isEmpty() {
		return this.isEmpty;
	}

	String group = "";
	String artifact = "";
	String curVersion = "";

	String packaging = null;

	public PomXml(boolean isEmpty) {
		this.isEmpty = isEmpty;
		this.deps = new ArrayList<>();
		this.depManages = new ArrayList<>();

	}

	public void setPackaging(String packaging) {
		if (!".jar".equals(packaging)
			&& !"aar".equals(packaging)
			&& !"pom".equals(packaging)
			&& !"bom".equals(packaging)
			) {
			packaging = "jar";
		}

		if ("bundle".equals(packaging)
			|| "takari-jar".equals(packaging)) {
			this.packaging = "jar";
		} else {
			this.packaging = packaging;
		}

	}
	public String getPackaging() {
		return this.packaging;
	}

    public PomXml makeConfiguration(String pomPath) {
        try {
			File file = new File(pomPath);
			if (!file.exists()) {
				return empty;
			}
            return new PomXml(pomPath);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	public String getGroupIdArtifactId() {
		return this.group + ":" + this.artifact;		
	}
	@Override
	public String toString() {
		return this.group + ":" + this.artifact + ":" + this.curVersion;
	}

	//子依赖
	//将一分为二
	public final List<ArtifactNode> deps;

	public final List<ArtifactNode> depManages;


	/**
	 * 新版解析器
	 */
	private static final MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();

    private PomXml(String filePath) {

		this.deps = new ArrayList<>();
		this.depManages = new ArrayList<>();
		this.configurationPath = filePath;

		try {
			File file = new File(filePath);
			if (!file.exists()) {
				// 未解析
				this.isEmpty = true;
				return;
			}
			FileInputStream inputStream = new FileInputStream(file);
			// pom文件模型
			Model model = mavenXpp3Reader.read(inputStream);
			inputStream.close();

			this.group = model.getGroupId();

			this.artifact = model.getArtifactId();
			this.curVersion = model.getVersion();

			if (this.curVersion ==  null) {
				this.curVersion = "+";
			}

			this.setPackaging(model.getPackaging());

			Parent parent = model.getParent();
			if (this.group == null) {
				if (parent != null) {
					this.group = parent.getGroupId();
				}
			}
			init(model);
			this.isEmpty = false;
		}
		catch (Throwable e) {
			AppLog.e(e.getMessage(), e);
			this.isEmpty = true;
			return;
		}
	}

	private void init(Model model) {

		DependencyManagement depManagement = model.getDependencyManagement();
		if (depManagement != null) {
			// 版本统一
			for (Dependency dep : depManagement.getDependencies()) {
				String scope = dep.getScope();

				ArtifactNode artifactNode = make(model, dep);

				if (artifactNode != null) {
					//除了bom，在dependencyManagement都只压入缓存用于控制版本
					if ("pom".equals(artifactNode.packaging) 
						|| "import".equals(scope)) {
						this.deps.add(artifactNode);
					} else {
						this.depManages.add(artifactNode);
					}
				}
			}
		}

		for (Dependency dep : model.getDependencies()) {
			ArtifactNode dependency = make(model, dep);
			if (dependency == null) {
				continue;
			}
			deps.add(dependency);
		}
		/*
		 AppLog.d( toString(), "depManages", this.depManages);
		 AppLog.d( toString(), "deps", this.deps);
		 System.out.println();
		 */


	}

	public ArtifactNode make(Model model, Dependency dep) {
		// 只能先添加，因为自己还未解析
		String scope = dep.getScope();
		//依赖类型为test不依赖
		// provided
		if ("test".equals(scope)
			|| "system".equals(scope)) {
			return null;
		}

		// 先添加，等待maven服务解析
		String groupId = dep.getGroupId();
		String artifactId = dep.getArtifactId();
		String version = dep.getVersion();
		String type = dep.getType();

		if (groupId == null) {
			groupId = "";
		}
		if (artifactId == null) {
			artifactId = "";
		}
		if (version == null) {
			version = "";
		}


		// 解析变量
		if (groupId.startsWith("${")) {
			//${project.groupId}
			if ("${project.groupId}".equals(groupId)) {
				groupId = model.getGroupId();
			} else {
				//自定义变量
				groupId = model.getProperties().getProperty(groupId.substring(2, groupId.length()  - 1));	
			}
		}

		if (groupId == null) {
			// groupId仍然为null是不对的
			System.out.println(configurationPath);
			return null;
		}

		if (artifactId.startsWith("${")) {
			//${project.artifactId}
			if ("${project.artifactId}".equals(artifactId)) {
				artifactId = model.getArtifactId();
			} else {
				artifactId = model.getProperties().getProperty(artifactId.substring(2, artifactId.length()  - 1));
			}
		}
		if (artifactId == null) {
			// artifactId仍然为null是不对的
			return null;
		}

		if (version == null) {
			String curGroupIdArtifactId = groupId + ":" + artifactId;
			for (ArtifactNode depManage : depManages) {
				if (curGroupIdArtifactId.equals(depManage.getGroupIdArtifactId())) {
					version = depManage.getVersion();
				}
			}
		}

		if (TextUtils.isEmpty(version)) {
			version = "+";
		}

		if (version.startsWith("${")) {
			//${project.version}
			if ("${project.version}".equals(version)) {
				version = model.getVersion();
			} else {
				version = model.getProperties().getProperty(version.substring(2, version.length()  - 1));	
			}
		}

		if (TextUtils.isEmpty(version)) {
			version = "+";
		}

		//
		if (version.startsWith("[")
			|| version.endsWith("]")
			|| version.startsWith("(")
			|| version.endsWith(")")
			|| version.contains(",")) {

			String newVersion = version.substring(1, version.length() - 1);

			String[] versions = newVersion.split(",");

			int index = versions.length - 1;
			if (version.endsWith("]")) {
				version = versions[index];
			} else if (version.endsWith(")")) {
				if (index > 0) {
					index--;
				}
				version = versions[index];
			}
		}

		ArtifactNode artifactNode = new ArtifactNode(groupId, artifactId, version);
		// 本质是 type
		artifactNode.packaging = type;


		List<Exclusion> exclusions = dep.getExclusions();
		if (exclusions != null) {
			// 添加排除依赖项
			artifactNode.setExclusions(exclusions);
		}
		return artifactNode;
	}
}

