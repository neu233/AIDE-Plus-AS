package com.aide.ui.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.model.Exclusion;
import java.util.ArrayList;
import com.aide.common.AppLog;
import android.text.TextUtils;

public class ArtifactNode extends BuildGradle.MavenDependency{

	public static String getClassifier(BuildGradle.MavenDependency dependency){
		if ( dependency instanceof ArtifactNode ){
			return ((ArtifactNode)dependency).classifier;
		}
		return null;
	}

	// 装箱
	public static ArtifactNode pack(BuildGradle.MavenDependency dep){

		if ( dep instanceof ArtifactNode ){
			return (ArtifactNode)dep;
		}

		String version = dep.version;
		if ( version != null || version.length() == 0 ){
			version = "+";
		}
		return new ArtifactNode(dep, version);
	}


	/*******************************************************************************/

	// 依赖排除
	private List<Exclusion> exclusions;
	public String classifier;

	public ArtifactNode(BuildGradle.MavenDependency mavenDependency, String version){
		super(mavenDependency, version);

		if ( mavenDependency instanceof ArtifactNode ){
			// 直接用字段，getExclusions可能返回emptyList
			ArtifactNode artifactNode = (ArtifactNode)mavenDependency;
			this.classifier = artifactNode.classifier;
			this.setExclusions(artifactNode.exclusions);
		}
		this.setVersion(this.version);

		// 保留 packaging
		this.packaging = mavenDependency.packaging;

	}

	public ArtifactNode(String groupId, String artifactId, String version){
		this(1, groupId, artifactId, version);
	}
	public ArtifactNode(int line, String groupId, String artifactId, String version){
		super(line);
		this.groupId = groupId;
		this.artifactId = artifactId;

		// version不能为空
		this.setVersion(version == null ? "+" : version);

		if ( this.groupId == null ){
			this.groupId = "";
		}
	}

	public ArtifactNode(PomXml pom){
		super(1);

		this.groupId = pom.group;
		this.artifactId = pom.artifact;
		String curVersion = pom.curVersion;

		// 确保 version不为null
		this.setVersion(curVersion == null ? "+" : curVersion);

		// 从pom解析出来的
		this.packaging = pom.getPackaging();

		if ( this.groupId == null ){
			this.groupId = "";
		}
		if ( this.artifactId == null ){
			this.artifactId = "";
		}

	}
	/*******************************************************************************/

	public void setVersion(String version){
		if ( TextUtils.isEmpty(version) ){
			version = "+";
		}
		this.version = version;
	}

	public String getVersion(){
		return this.version;
	}
	@Override
	public String getGroupIdArtifactId(){
		String groupIdArtifactId = super.getGroupIdArtifactId();
		if ( this.classifier != null ){
			groupIdArtifactId = groupIdArtifactId + ":" + this.classifier;
		}
		return groupIdArtifactId;
	}
	@Override
	public String toString(){

		StringBuilder sb = new StringBuilder(super.getGroupIdArtifactId())
			.append(":")
			.append(this.version);
		if ( this.classifier != null ){
			sb.append(":")
				.append(this.classifier);
		}
		if ( this.packaging != null && this.packaging.length() != 0 ){
			sb.append("@")
				.append(this.packaging);
		}
		
		return sb.toString();
	}

	@Override
	public int hashCode(){
		int hashCode = super.hashCode();
		if ( this.version != null ){
			hashCode += 17 * this.version.hashCode();
		}
		if ( this.classifier != null ){
			hashCode += 17 * this.classifier.hashCode();
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object object){
		if ( object instanceof BuildGradle.MavenDependency ){
			BuildGradle.MavenDependency mavenDependency = (BuildGradle.MavenDependency)object;
			return getGroupIdArtifactId().equals(mavenDependency.getGroupIdArtifactId());
		}else{
			return super.equals(object);
		}
	}
	public void syncExclusions(ArtifactNode artifactNode){
		if ( artifactNode.exclusions == null ){
			// 没有同步啥
			return;
		}
		if ( this.exclusions == null ){
			// 无脑用 
			this.exclusions = artifactNode.exclusions;
			return;
		}
		this.exclusions = new ArrayList<Exclusion>(this.exclusions);
		this.exclusions.addAll(artifactNode.exclusions);
	}
	public void setExclusions(List<Exclusion> exclusions){
		this.exclusions = exclusions;
	}
	private List<Exclusion> getExclusions(){
		if ( this.exclusions == null ){
			return Collections.emptyList();
		}
		return this.exclusions;
	}

	public Set<String> getExclusionSet(){
		Set<String> exclusionSet = new HashSet<>();
		for ( Exclusion exclusion : getExclusions() ){
			exclusionSet.add(exclusion.getGroupId() + ":" + exclusion.getArtifactId());
		}
		return exclusionSet;
	}

}
