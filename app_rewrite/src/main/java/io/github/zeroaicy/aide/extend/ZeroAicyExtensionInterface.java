


package io.github.zeroaicy.aide.extend;


import abcd.q2;
import abcd.x2;
import abcd.y2;
import android.app.Activity;
import android.os.Parcel;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import androidx.annotation.Keep;
import com.aide.codemodel.AIDEModel;
import com.aide.codemodel.ZeroAicyModel;
import com.aide.codemodel.api.BomReaderFactory;
import com.aide.codemodel.api.Entity;
import com.aide.codemodel.api.EntitySpace;
import com.aide.codemodel.api.Member;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.Parser;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.Type;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.abstraction.Syntax;
import com.aide.codemodel.api.callback.APISearcherCallback;
import com.aide.codemodel.api.callback.CodeCompleterCallback;
import com.aide.codemodel.api.callback.CodeMetricsCallback;
import com.aide.codemodel.api.callback.DebugMetadataCallback;
import com.aide.codemodel.api.callback.HighlighterCallback;
import com.aide.codemodel.api.callback.OpenFileCallback;
import com.aide.codemodel.api.callback.RefactoringCallback;
import com.aide.codemodel.api.callback.StopCallback;
import com.aide.codemodel.api.callback.StructureCallback;
import com.aide.codemodel.api.callback.SymbolSearcherCallback;
import com.aide.codemodel.api.callback.TemplateEvaluatorCallback;
import com.aide.codemodel.api.callback.UsageSearcherCallback;
import com.aide.codemodel.api.excpetions.UnknownEntityException;
import com.aide.codemodel.api.util.SyntaxTreeUtils;
import com.aide.codemodel.language.classfile.ClassFilePreProcessor;
import com.aide.codemodel.language.classfile.JavaBinaryLanguage;
import com.aide.codemodel.language.java.JavaCodeAnalyzer;
import com.aide.codemodel.language.java.JavaCodeModel;
import com.aide.codemodel.language.java.JavaCodeModelPro;
import com.aide.codemodel.language.java.JavaParser;
import com.aide.codemodel.language.java.JavaParserPro;
import com.aide.codemodel.language.kotlin.KotlinCodeModel;
import com.aide.codemodel.language.smali.SmaliCodeModel;
import com.aide.codemodel.language.xml.XmlLanguage;
import com.aide.codemodel.language.xml.XmlTools;
import com.aide.common.AppLog;
import com.aide.engine.SourceEntity;
import com.aide.engine.SyntaxStyleType;
import com.aide.engine.service.CodeAnalysisEngineService;
import com.aide.ui.AIDEEditor;
import com.aide.ui.MainActivity;
import com.aide.ui.ServiceContainer;
import com.aide.ui.build.packagingservice.ExternalPackagingService;
import com.aide.ui.project.JavaGradleProjectSupport;
import com.aide.ui.project.JavaProjectSupport;
import com.aide.ui.project.JavaScriptProjectSupport;
import com.aide.ui.project.NativeExecutableProjectSupport;
import com.aide.ui.project.PhonegapProjectSupport;
import com.aide.ui.project.WebsiteProjectSupport;
import com.aide.ui.project.internal.GradleTools;
import com.aide.ui.scm.ExternalGitService;
import com.aide.ui.scm.GitStatus;
import com.aide.ui.services.ProjectService;
import com.aide.ui.services.ProjectSupport;
import com.aide.ui.services.TrainerService;
import com.aide.ui.services.ZeroAicyProjectService;
import com.aide.ui.services.ZeroAicyTrainerService;
import com.aide.ui.util.BuildGradle;
import com.aide.ui.util.FileSystem;
import io.github.zeroaicy.aide.activity.ZeroAicyCommitActivity;
import io.github.zeroaicy.aide.activity.ZeroAicyMainActivity;
import io.github.zeroaicy.aide.codemodel.language.ZeroAicyXmlTools;
import io.github.zeroaicy.aide.completion.EditorCompletionAdapter;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.aide.scm.ZeroAicyExternalGitService;
import io.github.zeroaicy.aide.services.ZeroAicyCodeAnalysisEngineService;
import io.github.zeroaicy.aide.services.ZeroAicyExternalPackagingService;
import io.github.zeroaicy.aide.ui.project.ZeroAicyAndroidProjectSupport;
import io.github.zeroaicy.aide.utils.Utils;
import io.github.zeroaicy.aide.utils.ZeroAicyBuildGradle;
import io.github.zeroaicy.util.ContextUtil;
import io.github.zeroaicy.util.IOUtils;
import io.github.zeroaicy.util.Log;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 1.aapt2
 * 2.class解析器
 * 3.d8[打包流程]
 * 
 */

/**
 * AIDE+底包的修改点都将调用此类
 * 优点是可以随时更换实现
 */
public class ZeroAicyExtensionInterface {
	/**
	 * 编译器编译完成
	 */
	@Keep
	public static void completed( Model model ) {
		if ( model == null ) {
			return;
		}
		for ( CodeModel codeModel : model.getCodeModels() ) {
			try {
				codeModel.getCodeCompiler().completed();
			}
			catch (Throwable e) {				
			}
		}
	}

	// 预扩展 由CodeModelFactory调用 采用[源码覆盖模式]
	public static void createCodeModels( Model model, List<String> codeModelNames, List<CodeModel> codeModels ) {
		// AIDE是根据 codeModelNames来选择是否添加 CodeModel
		// codeModelNames来源之一 ServiceContainer.Hw()
		// 但我不遵守😕😕😕，即表示所有项目都会支持添加的CodeModel

		// codeModelNames不匹配只有词法高亮
		// codeModelNames 应该添加🤔
		codeModels.add(new SmaliCodeModel(model));
		codeModels.add(new KotlinCodeModel(model));

		//* 覆盖JavaCodeModel
		if ( ContextUtil.getPackageName().endsWith("debug")
			|| ZeroAicySetting.isEnableEclipseCompilerForJava() ) {
			// 只在共存版生效
			if (model != null && codeModels.get(0) instanceof JavaCodeModel ) {
				// 替换JavaCodeModel实现
				AppLog.d("启用EclipseCompilerForJava");
				codeModels.set(0, new JavaCodeModelPro(model));
			}
		}
		//*/
	}

	public static AIDEModel createAIDEModel( OpenFileCallback openFileCallback, StopCallback stopCallback, StructureCallback structureCallback, HighlighterCallback highlighterCallback, SymbolSearcherCallback symbolSearcherCallback, CodeCompleterCallback codeCompleterCallback, RefactoringCallback refactoringCallback, UsageSearcherCallback usageSearcherCallback, CodeMetricsCallback codeMetricsCallback, APISearcherCallback aPISearcherCallback, DebugMetadataCallback debugMetadataCallback, TemplateEvaluatorCallback templateEvaluatorCallback, y2 y2Var, q2 q2Var, x2 x2Var, BomReaderFactory bomReaderFactory ) {
        return new ZeroAicyModel(openFileCallback, stopCallback, structureCallback, highlighterCallback, symbolSearcherCallback, codeCompleterCallback, refactoringCallback, usageSearcherCallback, codeMetricsCallback, aPISearcherCallback, debugMetadataCallback, templateEvaluatorCallback, y2Var, q2Var, x2Var, bomReaderFactory);
	}
	
	/**
	 * AIDE AIDL容易崩 所以需要压缩
	 */
	/*
	 * parcelable 需要被序列化的Parcel
	 * 仅有Parcelable这个类数据
	 * dest 目标Parcel writeToParcel中的参数
	 */

	/*
	 #	将原来的 writeToParcel -> writeToParcelOriginal
	 #	并替换writeToParcelOriginal类名
	 .method public writeToParcel(Landroid/os/Parcel;I)V
	 .registers 4
	 .annotation system Ldalvik/annotation/Signature;
	 value = {
	 "(",
	 "Landroid/os/Parcel;",
	 "I)V"
	 }
	 .end annotation

	 .annotation runtime Ljava/lang/Override;
	 .end annotation

	 .line 329
	 invoke-static {}, Landroid/os/Parcel;->obtain()Landroid/os/Parcel;

	 move-result-object v0

	 .line 330
	 invoke-virtual {p0, v0, p2}, Lcom/aide/engine/EngineSolutionProject;->writeToParcelOriginal(Landroid/os/Parcel;I)V

	 .line 331
	 invoke-static {v0, p1}, Lio/github/zeroaicy/aide/extend/ZeroAicyExtensionInterface;->compressionParcel(Landroid/os/Parcel;Landroid/os/Parcel;)V

	 return-void
	 .end method
	 */
	public static void compressionParcel( Parcel parcelableParcel, Parcel dest ) {
		// 判断是否压缩, 数据大的强制压缩吧

		if ( ZeroAicySetting.getDefaultSp() == null ) {
			ZeroAicySetting.init(ContextUtil.getContext());
		}

		// 是否启用压缩
		final boolean data_compression_enable = ZeroAicySetting.getDefaultSp().getBoolean("data_compression_enable", true);

		// 压缩阈值 2k
		final int data_compression_threshold = Utils.parseInt(ZeroAicySetting.getDefaultSpString("data_compression_threshold", "2"), 2);

		// 压缩等级
		int data_compression_level = Utils.parseInt(ZeroAicySetting.getDefaultSpString("data_compression_level", "9"), Deflater.DEFLATED);
		// 规范压缩等级
		if ( data_compression_level < 0 || data_compression_level > 9 ) {
			data_compression_level = Deflater.DEFLATED;
		}

		// 左移 10 为 1KB
		boolean compress = data_compression_enable && parcelableParcel.dataSize() > data_compression_threshold << 10;

		//是否压缩标识
		dest.writeInt(compress ? 1 : 0);

		if ( !compress ) {
			// dest 只有数据没有 压缩标识
			// 添加dest
			dest.appendFrom(parcelableParcel, 0, parcelableParcel.dataSize());
			parcelableParcel.recycle();
			return;
		}

		try {
			// 压缩
			// 获得序列化后数据

			GZIPOutputStream gzipOutput = null;
			try {
				final int compression_level = data_compression_level;
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				gzipOutput = new GZIPOutputStream(output){
					{
						// 重写GZIPOutputStream，使得可以设置压缩等级
						this.def.setLevel(compression_level);
					}
				};

				byte[] marshall = parcelableParcel.marshall();				
				//写入序列化数据并压缩
				gzipOutput.write(marshall);
				// 强制缓存区写入
				gzipOutput.flush();
				gzipOutput.close();
				output.close();

				//压缩数据数据
				byte[] compressData = output.toByteArray();
				// 向目标 parcel写入压缩后的数组
				dest.writeByteArray(compressData);

				// 没用了
				parcelableParcel.recycle();

			}
			finally {
				IOUtils.close(gzipOutput);
			}
		}
		catch (Throwable e) {
			AppLog.e("EngineSolutionProject", "压缩", e);
			// throw new Error(e);
		}
	}

	/**
	 * smali替换 Parcelable.Creator createFromParcel 实现
	 * 因为需要返回 Parcel
	 * 需要 调用recycleParcelableParcel
	 #	invoke-static {p1}, Lio/github/zeroaicy/aide/extend/ZeroAicyExtensionInterface;->decompressionParcel(Landroid/os/Parcel;)Landroid/os/Parcel;
	 #	move-result-object v0
	 #	xxxxx
	 #	
	 *
	 */
	public static Parcel decompressionParcel( Parcel dest ) {
		// dest 不仅一个Parcelable数据
		// 所以不能修改
		// compression标志
		boolean compression = dest.readInt() == 1;

		if ( !compression ) {
			// 如果压缩时用appendFrom
			// 啥都不用处理
			return dest;
		}

		GZIPInputStream gzipInputStream = null;
		try {
			//读取数据
			byte[] buf = dest.createByteArray();

			gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(buf));
			// 解压数据
			byte[] data = IOUtils.readAllBytes(gzipInputStream);
			// 填充
			Parcel obtain = Parcel.obtain();
			obtain.unmarshall(data, 0, data.length);
			obtain.setDataPosition(0);

			return obtain;
		}
		catch (Throwable e) {
			AppLog.e("EngineSolutionProject", "unZipParcel", e);
			throw new Error(e);
		}
		finally {
			IOUtils.close(gzipInputStream);
		}
	}
	/*
	 * 释放Parcel
	 *	在createFromParcel调用
	 *	p1 source v0 decompressionParcel返回的
	 #	invoke-static {p1, v0}, Lio/github/zeroaicy/aide/extend/ZeroAicyExtensionInterface;->recycleParcelableParcel(Landroid/os/Parcel;Landroid/os/Parcel;)V
	 *
	 */
	public static void recycleParcelableParcel( Parcel source, Parcel parcelableParcel ) {
		if ( source != parcelableParcel ) {
			parcelableParcel.recycle();
		}
	}
	/**
	 * 测试 仅在共存版会被SyntaxTree::declareAttrType()调用
	 */
	@Keep
	public static void declareAttrType( SyntaxTree syntaxTree, int node, Type type ) {

		if ( type == null ) return;
		// 禁用
		if ( type != null ) return;
		SyntaxTreeUtils.printlnNodeAttr(syntaxTree, "declareAttrType node ", node);
		SyntaxTreeUtils.printNode(syntaxTree, node);
		System.out.println(type);

		Log.printlnStack(2, 5);

		AppLog.println_d("************************");
		AppLog.println_d();


	}

	/**
	 * 过滤JavaCodeAnalyzer$a::Od(varNode)的返回值为
	 * 防止ParameterizedType被踢出泛型
	 */
	@Keep
	public static Type getVarNodeAttrType( SyntaxTree syntaxTree, int varParentNode ) {
		return SyntaxTreeUtils.getVarNodeAttrType(syntaxTree, varParentNode);
	}



	/**
	 * 尝试支持 var [Java]
	 */
	@Keep
	public static Entity getVarAttrType( JavaCodeAnalyzer.a JavaCodeAnalyzer$a, int varNode ) throws UnknownEntityException {
		return SyntaxTreeUtils.getVarAttrType(JavaCodeAnalyzer$a, varNode);
	}


	/**
	 * 
	 */
	@Keep
	public static boolean parserLambdaExpression( JavaParser javaParser ) throws Parser.a {
		if ( ! ( javaParser instanceof JavaParserPro ) ) {
			return false;
		}
		JavaParserPro javaParserPro = (JavaParserPro)javaParser;
		return javaParserPro.parserLambdaExpression();
	}

	/**
	 * 修正接口方法是否自动附加abstract
	 * 除default | static 方法除外
	 * JavaParser::oY()I
	 */
	@Keep
	public static int getModifiers( SyntaxTree syntaxTree, int nodeIndex, int flags ) {
		// 显示声明 abstract 或 static，添加 public 就行
		if ( ( flags & 0x4000 ) != 0 
			|| ( flags & 0x40 ) != 0 ) {
			return flags |= 0x1;
		}
		// 处理源码的接口默认方法
		Language language = syntaxTree.getLanguage();
		// 非class 且 非default或非static时才添加抽象标志
		if ( ! ( language instanceof JavaBinaryLanguage )
			&& !SyntaxTreeUtils.isNoInterfaceAbstractMethod(syntaxTree, syntaxTree.getChildNode(nodeIndex, 0)) ) {
			// 非 default || 非static 才 |= 0x4000
			return flags |= 0x4001;
		}

		return flags |= 0x1;
	}

	/**
	 * is abstract method 是不是接口不重要
	 * 代替 y1.j6 [Modifiers::isAbstract(int)boolean]
	 * Modifiers::isAbstract(int)传入的参数是遍历树节点得到的
	 * 没有考虑接口类缺省abstract的方法
	 * 此时Member已填充完成，Member:isAbstract()可用
	 */
	@Keep
	public static boolean isInterfaceAbstractMethod( EntitySpace entitySpace, SyntaxTree syntaxTree, int nodeIndex ) {
		// code 分析器 
		Member method = entitySpace.getMember(syntaxTree.getFile(), syntaxTree.getLanguage(), syntaxTree.getDeclarationNumber(nodeIndex));
		// 此方法没有 abstract
		return method != null && method.isAbstract();
	}

	//扩展接口
	/**
	 * 重定义Apk构建路径
	 *
	 */
	public static String getApkBuildPath( String projectPath ) {
		if ( ZeroAicySetting.isEnableAdjustApkBuildPath() ) {
			String currentAppHome = ServiceContainer.getProjectService().getCurrentAppHome();
			if ( currentAppHome != null ) {
				return GradleTools.getBinPath(currentAppHome) + "/" + FileSystem.getName(projectPath) + ".apk";
			}
		}
		return FileSystem.getSafeCacheDirPath() + "/apk/" + FileSystem.getName(projectPath) + ".apk";
	}
	/**
	 * 返回入口Activity类
	 * 主要是替换点击通知后的启动
	 */
	public static Class<? extends MainActivity> getLaunchActivityClass( ) {
		return ZeroAicyMainActivity.class;
	}
	/**
	 * 替换CodeAnalysisEngineService实现
	 */
	public static Class<? extends CodeAnalysisEngineService> getCodeAnalysisEngineServiceClass( ) {
		return ZeroAicyCodeAnalysisEngineService.class;
	}

	//打包服务替换
	public static Class<?extends ExternalPackagingService> getExternalPackagingServiceClass( ) {
		return ZeroAicyExternalPackagingService.class;
	}

	/**
	 * 替换 ExternalGitService服务
	 */
	public static Class<?extends ExternalGitService> getExternalGitService( ) {
		return ZeroAicyExternalGitService.class;
	}

	//替换ClassFilePreProcessor实现
	@Keep
	public static ClassFilePreProcessor getClassFilePreProcessor( ) {
		return ZeroAicyClassFilePreProcessor.getSingleton();
	}
	//拦截类默认接口方法
	@Deprecated
	@Keep
	public static boolean isDefaultMethod( String methodSignature ) {
		return false; //ZeroAicyClassFilePreProcessor.isDefaultMethod(methodSignature);
	}

	//替换默认安装，true则拦截，false则不拦截
	@Keep
	public static boolean instalApp( final String apkFilePath ) {
		return DistributeEvents.instalApp(apkFilePath);
	}

	//在Java项目中解除android.jar限制
	@Keep
	public static boolean isEnableAndroidApi( ) {
		return ZeroAicySetting.isEnableAndroidApi();
	}

	@Keep
	public static boolean isEnableADRT( ) {
		return ZeroAicySetting.enableADRT();
	}
	/*
	 * 控制台是否启用分屏
	 */
	@Keep
	public static boolean isEnableSplitScreenConsole( ) {
		return false;
	}
	/**
	 * 修改maven默认下载路径
	 */
	@Keep
	public static String getUserM2Repositories( ) {
		return ZeroAicySetting.getDefaultSpString("user_m2repositories", null);
	}

	/**
	 * 替换BuildGradle解析实现
	 */
	@Keep
	public static BuildGradle getBuildGradle( ) {
		return ZeroAicyBuildGradle.getSingleton();
	}

	@Keep
	public static ProjectService getProjectService( ) {
		return ZeroAicyProjectService.getSingleton();
	}

	/**
	 * 项目服务运行的线程服务
	 */
	@Keep
	public static ExecutorService getProjectExecutorService( ) {
		return ZeroAicyProjectService.getProjectServiceExecutorService();
	}

	/**
	 * 优化冷启动
	 */
	@Keep
	public static TrainerService getTrainerService( ) {
		return ZeroAicyTrainerService.getSingleton();
	}

	/**
	 * 实现渠道包添加额外依赖
	 * configuration.dependencies -> getFlavorDependencies
	 */
	public static List<BuildGradle.Dependency> getFlavorDependencies( BuildGradle buildGradle ) {
		if ( buildGradle == null ) {
			return Collections.emptyList();
		}

		List<BuildGradle.Dependency> defaultDependencies = buildGradle.dependencies;
		if ( !( buildGradle instanceof ZeroAicyBuildGradle ) ) {
			return defaultDependencies;
		}

		// 渠道包
		String buildVariant = ServiceContainer.getProjectService().getFlavor();

		//Log.d("getFlavorDependencies", "buildVariant", buildVariant);
		if ( TextUtils.isEmpty(buildVariant) ) {
			return defaultDependencies;
		}

		List<BuildGradle.Dependency> flavorDependencies = ( (ZeroAicyBuildGradle)buildGradle ).getFlavorDependencies(buildVariant);
		if ( flavorDependencies.isEmpty() ) {
			return defaultDependencies;			
		}
		// 合并 flavorDependencies与defaultDependencies
		List<BuildGradle.Dependency> dependencies = new ArrayList<BuildGradle.Dependency>();
		dependencies.addAll(flavorDependencies);
		dependencies.addAll(defaultDependencies);

		return dependencies;
	}

	// 用于修复MessageBox::gW()可能在子线程运行的情况
	public static void showDialogMessageBox( final Activity activity, final int id ) {
		if ( activity == null ) return;
		//保证在主线程调用
		activity.runOnUiThread(new Runnable(){
				@Override
				public void run( ) {
					if ( !activity.isFinishing() ) {
						activity.showDialog(id);						
					}
				}
			});
	}
	/**
	 * 修复更新弹窗
	 */
	public static String repairWhatsNewDialog( String appId ) {
		if ( ServiceContainer.appId.equals(appId) ) {
			return ContextUtil.getPackageName();
		}
		return appId;
	}
	
	
	public static SyntaxStyleType getSyntaxStyleType(int type){
		return SyntaxStyleType.getStyleTypeOfHighlighterType(type);
	}
	/**
	 * Lcom/aide/engine/Engine$c;->Ws
	 */
	@Keep
	@Deprecated
	public static SyntaxStyleType getSyntaxStyleType( Syntax syntax, int syntaxTag ) {
        try {
			/*
			 if ( syntax.isOperator(syntaxTag) ){
			 return SyntaxStyleType.OPERATOR;
			 }
			 if ( syntax.isSeparator(syntaxTag) ){
			 return SyntaxStyleType.SEPARATOR;
			 }
			 if ( syntax.isTypeIdentifier(syntaxTag) ){
			 return SyntaxStyleType.TYPE_IDENTIFIER;
			 }
			 if ( syntax.isBooleanLiteral(syntaxTag) ){
			 return SyntaxStyleType.LITERAL;
			 }
			 if ( syntax.isToken(syntaxTag) ){
			 return SyntaxStyleType.KEYWORD;
			 }
			 if ( syntax.isDocComment(syntaxTag) ){
			 return SyntaxStyleType.DOC_COMMENT;
			 }
			 if ( syntax.isComment(syntaxTag) ){
			 return SyntaxStyleType.COMMENT;
			 }
			 // 扩展
			 if ( syntax.isParameters(syntaxTag) ){
			 //return SyntaxStyleType.PARAMETER;
			 }
			 if ( syntax.isIdentifier(syntaxTag) ){
			 // 测试一下斜体
			 return SyntaxStyleType.IDENTIFIER;

			 }
			 /*if( syntax instanceof JavaSyntax){
			 AppLog.d("getSyntaxStyleType: ", syntax.getString(syntaxTag), "\n");
			 }*/
            return null;
        }
		catch (Throwable th) {
			return null;
        }
    }

	@Keep
	public static ProjectSupport[] getProjectSupports( ) {
		return 
			new ProjectSupport[]{
			new JavaGradleProjectSupport(),
			new ZeroAicyAndroidProjectSupport(), 
			new WebsiteProjectSupport(), 
			new PhonegapProjectSupport(), 
			new JavaProjectSupport(), 
			new NativeExecutableProjectSupport(),
			new JavaScriptProjectSupport()};
	}

	/**
	 *  Tools必然重构 符号更换底包是麻烦
	 */
	@Keep
	public static XmlTools getXmlTools( Model model, XmlLanguage xmlLanguage, boolean p ) {
		return new ZeroAicyXmlTools(model, xmlLanguage, p);
	}

	/**
	 * 必须返回 ArrayAdapter
	 */
	@Keep
	public static ArrayAdapter<SourceEntity> getEditorCompletionAdapter( AIDEEditor aideEditor, List<SourceEntity> sourceEntitys ) {
		return new EditorCompletionAdapter(aideEditor, sourceEntitys);
	}

	/**
	 * 必须返回 替换 CommitActivity实现
	 */
	@Keep
	public static void startCommitActivity( Activity activity, GitStatus gitStatus, String gitBranch ) {
		ZeroAicyCommitActivity.startCommitActivity(activity, gitStatus, gitBranch);
	}
}
