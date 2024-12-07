package com.aide.ui.project;

import abcd.c0;
import abcd.fe;
import com.aide.common.ValueRunnable;
import com.aide.engine.EngineSolution;
import com.aide.ui.ServiceContainer;
import com.aide.ui.build.BuildServiceCollect;
import com.aide.ui.project.internal.MakeJavaEngineSolution;
import com.aide.ui.rewrite.R;
import com.aide.ui.services.ProjectSupport;
import com.aide.ui.services.TemplateService;
import com.aide.ui.trainer.Course;
import com.aide.ui.trainer.Course.File;
import com.aide.ui.util.ClassPath;
import com.aide.ui.util.FileSystem;
import com.probelytics.Probelytics;
import com.probelytics.annotation.ExceptionEnabled;
import com.probelytics.annotation.MethodMark;
import com.probelytics.annotation.ParametersEnabled;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class JavaProjectSupport2 implements ProjectSupport {

	@ExceptionEnabled
    private static boolean exceptionEnabled;

    @ParametersEnabled
    private static boolean parametersEnabled;


    public JavaProjectSupport2( ) {}

    public static String I( String str, String str2 ) {
        try {
            for ( String sourcedDirectory : getProjectSourceDirs(str) ) {
                if ( FileSystem.isPrefix(sourcedDirectory, str2) ) {
                    return FileSystem.getRelativePath(sourcedDirectory, str2).replace('/', '.');
                }
            }
            return null;
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    public static String Mz( String str, boolean z ) {
        try {
            return getClassFileCacheFile(str, z) + "/dex/jars";
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

	// 允许在非高级版中保存一个Java文件
    private boolean Qq( ) {
        try {
            int i = 0;
            for ( String str : getProjectSourceDirs(ServiceContainer.getProjectService().getCurrentAppHome()) ) {
                i += FileSystem.J8(str, x9(), new String[]{".java"});
                if ( i >= x9() ) {
                    return true;
                }
            }
            return false;
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }
	// getJavaProjectOutputFile
    public static String Sf( String str, boolean z ) {
        try {
            if ( parametersEnabled ) {
                Probelytics.printlnParameters(6940714751171552300L, (Object) null, str, new Boolean(z));
            }
            return getClassFileCacheFile(str, z) + "/dex/classes.dex.zip";
        }
		catch (Throwable th) {
            if ( exceptionEnabled ) {
                Probelytics.printlnException(th, 6940714751171552300L, (Object) null, str, new Boolean(z));
            }
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    public static String ca( Map<String, List<String>> map, String str ) {
        try {
            for ( String str2 : getProjectSourceDirs(map) ) {
                if ( FileSystem.isPrefix(str2, str) ) {
                    return FileSystem.getRelativePath(str2, str).replace('/', '.');
                }
            }
            return null;
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = -3518927663961977092L)
    public static String[] cb( String str ) {
        try {
            ArrayList<String> arrayList = new ArrayList<>();
            for ( ClassPath.Entry entry : singleton.getConfiguration(getClassPathFile(str)).Zo ) {
                if ( entry.isLibKind() ) {
                    arrayList.add(entry.resolveFilePath(str));
                }
            }
            return arrayList.toArray(new String[arrayList.size()]);
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = -1949508141307515464L)
    public static String ef( String str, boolean z ) {
        try {
            return getClassFileCacheFile(str, z) + "/dex";
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

	// g3 -> getClassPathFile
    public static String getClassPathFile( String str ) {
        try {
            return str + "/.classpath";
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    public static final ClassPath singleton = new ClassPath();
	// dx -> getProjectSourceDirs
    public static String[] getProjectSourceDirs( String projectDirectory ) {
        try {
            List<String> projectSourceDirs = new ArrayList<>();
            for ( ClassPath.Entry entry : singleton.getConfiguration(getClassPathFile(projectDirectory)).Zo ) {
                if ( entry.isSrcKind() ) {
                    projectSourceDirs.add(entry.resolveFilePath(projectDirectory));
                }
            }
            return projectSourceDirs.toArray(new String[projectSourceDirs.size()]);
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    // sG -> getProjectSourceDirs
    public static String[] getProjectSourceDirs( Map<String, List<String>> map ) {
        try {
            List<String> projectSourceDirs = new ArrayList<>();
            for ( String str : map.keySet() ) {
				for ( ClassPath.Entry entry : singleton.getConfiguration(getClassPathFile(str)).Zo ) {
                    if ( entry.isSrcKind() ) {
                        projectSourceDirs.add(entry.resolveFilePath(str));
                    }
                }
            }
            return projectSourceDirs.toArray(new String[projectSourceDirs.size()]);
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

	// getClassFileCacheFile
    public static String getClassFileCacheFile( String str, boolean isDebugFormAide ) {
        try {
            for ( ClassPath.Entry entry : singleton.getConfiguration(getClassPathFile(str)).Zo ) {
                if ( entry.isOutputIKind() ) {
                    String resolveFilePath = entry.resolveFilePath(str);
                    if ( isDebugFormAide ) {
                        return resolveFilePath + "/debug";
                    }
                    return resolveFilePath + "/release";
                }
            }
            if ( isDebugFormAide ) {
                return str + "/bin/debug";
            }
            return str + "/bin/release";
        }
		catch (Throwable th) {
			if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    /**
	 * 允许在非高级版中保存一个Java文件
	 */
	private int x9( ) {
        try {
            return 2;
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = -6443555734859431011L)
    public boolean J8( ) {
		return false;
    }

    @MethodMark(method = -440288102385704928L)
    public void Mr( ) {
        try {
            ServiceContainer.getLicenseService().cn(ServiceContainer.getMainActivity(), 0x7f0d0611, "large_project");
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    public void P8( String str, String str2 ) {
    }

    public void SI( String str, ValueRunnable<String> valueRunnable ) {
        try {
            fe.DW(str, valueRunnable);
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    public boolean Zo( String str ) {
        try {
            return fe.j6(str);
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    public boolean a8( String str ) {
        try {
            return Arrays.asList(cb(ServiceContainer.getProjectService().getCurrentAppHome())).contains(str);
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = 823499853340058705L)
    public void addJarLib( String str ) {
        try {
            if ( parametersEnabled ) {
                Probelytics.printlnParameters(1981618901340942695L, this, str);
            }
            String currentAppHome = ServiceContainer.getProjectService().getCurrentAppHome();
            List list = singleton.getConfiguration(getClassPathFile(currentAppHome)).Zo;
            Iterator it = list.iterator();
            while ( true ) {
                if ( !it.hasNext() ) {
                    break;
                }
                ClassPath.Entry entry = (ClassPath.Entry) it.next();
                if ( entry.isLibKind() && entry.resolveFilePath(currentAppHome).equals(str) ) {
                    list.remove(entry);
                    break;
                }
            }
            ClassPath.Hw(getClassPathFile(currentAppHome), list);
        }
		catch (Throwable th) {
            if ( exceptionEnabled ) {
                Probelytics.printlnException(th, 1981618901340942695L, this, str);
            }
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = 2609982001950720552L)
    public void buildProject( boolean z ) {
        try {
            // build
            BuildServiceCollect.javaProjectBuildService.XL(z, ServiceContainer.getProjectService().er());
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    public void cn( List<String> list, boolean z ) {

    }

    @MethodMark(method = 889606723063076857L)
    public boolean containJarLib( String str ) {
        try {
            if ( str.toLowerCase().endsWith(".jar") ) {
                if ( !Arrays.asList(cb(ServiceContainer.getProjectService().getCurrentAppHome())).contains(str) ) {
                    return true;
                }
            }
            return false;
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

	public void ei( String str ) {

    }

    public boolean gW( ) {
		return false;
    }

    public List<String> getAddToProjectAdvise( String str ) {
        return null;
    }

    public int getOpenProjectNameStringId( String str ) {
        return /*0x7f0d002e*/ R.string.command_files_open_java_project;
    }

    public List<String> getProductFlavors( String str ) {
        return null;
    }

    public String getProjectAttributeHtmlString( ) {
        try {
            String str = ( "<b>Java Project:</b><br/><br/>" + ServiceContainer.getProjectService().getCurrentAppHome() + "<br/><br/>" ) + "<i>Library JARs:</i><br/><br/>";
            String[] cb = cb(ServiceContainer.getProjectService().getCurrentAppHome());
            if ( cb.length == 0 ) {
                return str + "&lt;none&gt;<br/><br/>";
            }
            for ( String str2 : cb ) {
                if ( !FileSystem.exists(str2) ) {
                    str = str + "(NOT FOUND) ";
                }
                str = str + str2 + "<br/><br/>";
            }
            return str;
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = -18331867418089656L)
    public String getProjectPackageName( ) {
		return ServiceContainer.getContext().getPackageName();
    }
	
    public TemplateService.TemplateGroup[] getTemplateGroups( ) {
        boolean z;
        try {
            if ( parametersEnabled ) {
                Probelytics.printlnParameters(1131067569338544863L, this);
            }
            if ( !ServiceContainer.isAggregateVersion() && !ServiceContainer.appId.equals("com.aide.ui") ) {
                z = false;
                return new TemplateService.TemplateGroup[]{new TemplateService.TemplateGroup("Java Application", new TemplateService.Template(this, 3, "Java Application", "Java", "MyJavaConsoleApp", false, false, "com.aide.ui", "JAVA", "course_java", z), 0x7f07007b, "JavaConsole.zip", new String[]{"Main.java"}, (String) null)};
            }

            z = true;
            return new TemplateService.TemplateGroup[]{new TemplateService.TemplateGroup("Java Application", new TemplateService.Template(this, 3, "Java Application", "Java", "MyJavaConsoleApp", false, false, "com.aide.ui", "JAVA", "course_java", z), 0x7f07007b, "JavaConsole.zip", new String[]{"Main.java"}, (String) null)};
        }
		catch (Throwable th) {
            if ( exceptionEnabled ) {
                Probelytics.printlnException(th, 1131067569338544863L, this);
            }
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = 2950406777596539456L)
    public List<Course.File> getTrainerCourses( ) {
        try {
            File file = new Course.File("course_java", 1, new String[]{"com.aide.ui", "com.aide.trainer.java"});
			return Collections.<Course.File>singletonList(file);
        }
		catch (Throwable th) {
            if ( exceptionEnabled ) {
                Probelytics.printlnException(th, -1751453124824682264L, this);
            }
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = -417465410313150680L)
    public void gn( ) {
        try {
            if ( parametersEnabled ) {
                Probelytics.printlnParameters(-3585970868152192L, this);
            }
        }
		catch (Throwable th) {
            if ( exceptionEnabled ) {
                Probelytics.printlnException(th, -3585970868152192L, this);
            }
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    public void init( String projectDir, Map<String, List<String>> map, List<String> projectDirs ) {
        try {
            map.put(projectDir, new ArrayList<String>());
            projectDirs.add(projectDir);
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    public boolean isInCurrentProjectDirectory( String str ) {
        try {
            if ( parametersEnabled ) {
                Probelytics.printlnParameters(-1502946952403260497L, this, str);
            }
            Iterator it = ServiceContainer.getProjectService().getLibraryMapping().keySet().iterator();
            while ( it.hasNext() ) {
                if ( FileSystem.isPrefix((String) it.next(), str) ) {
                    return true;
                }
            }
            for ( String str2 : getProjectSourceDirs(ServiceContainer.getProjectService().getCurrentAppHome()) ) {
                if ( FileSystem.isPrefix(str2, str) ) {
                    return true;
                }
            }
            return false;
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }
	
    public boolean isPremium( ) {
        try {
            if ( ServiceContainer.isAggregateVersion() || ServiceContainer.getLicenseService().Ws() ) {
                return false;
            }
            if ( c0.Ws(ServiceContainer.getMainActivity(), "AllowSavingOneJavaFileInNonPremium", new double[]{1.0d, 0.0d}) ) {
                // 允许在非高级版中保存一个Java文件
				return Qq();
            }
            return true;
        }
		catch (Throwable th) {
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = 7129499176044610200L)
    public boolean isSupport( String str ) {
        try {
            if ( FileSystem.exists(getClassPathFile(str)) ) {
                if ( !FileSystem.exists(str + "/AndroidManifest.xml") ) {
                    return true;
                }
            }
            return false;
        }
		catch (Throwable th) {
            if ( exceptionEnabled ) {
                Probelytics.printlnException(th, 1013175581687134208L, this, str);
            }
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = 5050030786987684784L)
    public boolean isVersionSupport( String str ) {
        try {
            if ( parametersEnabled ) {
                Probelytics.printlnParameters(-4833072011717200944L, this, str);
            }
            if ( !ServiceContainer.isAggregateVersion() && !ServiceContainer.appId.equals("com.aide.ui") ) {
                if ( !ServiceContainer.appId.equals("com.aide.trainer.java") ) {
                    return false;
                }
            }
            return true;
        }
		catch (Throwable th) {
            if ( exceptionEnabled ) {
                Probelytics.printlnException(th, -4833072011717200944L, this, str);
            }
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = 6506233914607290985L)
    public void j6( ) {
        try {
            if ( parametersEnabled ) {
                Probelytics.printlnParameters(1581057273621888879L, this);
            }
        }
		catch (Throwable th) {
            if ( exceptionEnabled ) {
                Probelytics.printlnException(th, 1581057273621888879L, this);
            }
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = -4696716385342169281L)
    public boolean lg( ) {
        try {
            if ( !parametersEnabled ) {
                return true;
            }
            Probelytics.printlnParameters(4081838800878329337L, this);
            return true;
        }
		catch (Throwable th) {
            if ( exceptionEnabled ) {
                Probelytics.printlnException(th, 4081838800878329337L, this);
            }
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = -1468446800406435000L)
    public EngineSolution makeEngineSolution( ) {
        try {
            if ( parametersEnabled ) {
                Probelytics.printlnParameters(-3872239307417140704L, this);
            }
            return MakeJavaEngineSolution.DW(ServiceContainer.getProjectService().getCurrentAppHome(), ServiceContainer.getProjectService().getAndroidJarPath(), (String) null);
        }
		catch (Throwable th) {
            if ( exceptionEnabled ) {
                Probelytics.printlnException(th, -3872239307417140704L, this);
            }
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = -3641730026299000867L)
    public void nw( String str ) {
        try {
            if ( parametersEnabled ) {
                Probelytics.printlnParameters(-3427879366614745133L, this, str);
            }
            String currentAppHome = ServiceContainer.getProjectService().getCurrentAppHome();
            List list = singleton.getConfiguration(getClassPathFile(currentAppHome)).Zo;
            list.add(new ClassPath.Entry("lib", FileSystem.removePrefix(currentAppHome, str), false));
            ClassPath.Hw(getClassPathFile(currentAppHome), list);
        }
		catch (Throwable th) {
            if ( exceptionEnabled ) {
                Probelytics.printlnException(th, -3427879366614745133L, this, str);
            }
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = -359481658356851144L)
    public int rN( String str ) {
        try {
            if ( parametersEnabled ) {
                Probelytics.printlnParameters(-100285065325905132L, this, str);
            }
            return fe.Hw(str);
        }
		catch (Throwable th) {
            if ( exceptionEnabled ) {
                Probelytics.printlnException(th, -100285065325905132L, this, str);
            }
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = -82167400608941088L)
    public String sh( String str ) {
        try {
            if ( parametersEnabled ) {
                Probelytics.printlnParameters(-7013994048444584208L, this, str);
            }
            String[] sG = getProjectSourceDirs(ServiceContainer.getProjectService().getLibraryMapping());
            if ( !str.startsWith("/") ) {
                str = "/" + str;
            }
            for ( String str2 : sG ) {
                String str3 = str2 + str;
                if ( FileSystem.exists(str3) ) {
                    return str3;
                }
            }
            return null;
        }
		catch (Throwable th) {
            if ( exceptionEnabled ) {
                Probelytics.printlnException(th, -7013994048444584208L, this, str);
            }
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = 642355415680964120L)
    public String tp( String str ) {
        try {
            if ( parametersEnabled ) {
                Probelytics.printlnParameters(-1557429322153859008L, this, str);
            }
            if ( isVersionSupport(str) ) {
                return null;
            }
            return "com.aide.ui";
        }
		catch (Throwable th) {
            if ( exceptionEnabled ) {
                Probelytics.printlnException(th, -1557429322153859008L, this, str);
            }
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = 4688557158630911056L)
    public boolean u7( String str ) {
        try {
            if ( !parametersEnabled ) {
                return false;
            }
            Probelytics.printlnParameters(3903443620038632736L, this, str);
            return false;
        }
		catch (Throwable th) {
            if ( exceptionEnabled ) {
                Probelytics.printlnException(th, 3903443620038632736L, this, str);
            }
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = -1781888940704881800L)
    public String v5( String str ) {
        try {
            if ( parametersEnabled ) {
                Probelytics.printlnParameters(-4328879974228677312L, this, str);
            }
            String ca = ca(ServiceContainer.getProjectService().getLibraryMapping(), FileSystem.getParent(str));
            if ( ca == null ) {
                return str;
            }
            return ca.replace('.', '/') + "/" + FileSystem.getName(str);
        }
		catch (Throwable th) {
            if ( exceptionEnabled ) {
                Probelytics.printlnException(th, -4328879974228677312L, this, str);
            }
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = -142575311183543395L)
    public boolean verifyResourcesDownload( ) {
        try {
            if ( !parametersEnabled ) {
                return false;
            }
            Probelytics.printlnParameters(-7089856552801447365L, this);
            return false;
        }
		catch (Throwable th) {
            if ( exceptionEnabled ) {
                Probelytics.printlnException(th, -7089856552801447365L, this);
            }
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = 1003945709493297000L)
    public boolean vy( String str ) {
        try {
            if ( !parametersEnabled ) {
                return false;
            }
            Probelytics.printlnParameters(1738458739672370240L, this, str);
            return false;
        }
		catch (Throwable th) {
            if ( exceptionEnabled ) {
                Probelytics.printlnException(th, 1738458739672370240L, this, str);
            }
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }

    @MethodMark(method = -1775220633938098065L)
    public int we( String str ) {
        try {
            if ( parametersEnabled ) {
                Probelytics.printlnParameters(1676999377948974401L, this, str);
            }
            return fe.FH(str);
        }
		catch (Throwable th) {
            if ( exceptionEnabled ) {
                Probelytics.printlnException(th, 1676999377948974401L, this, str);
            }
            if ( th instanceof Error ) throw (Error)th;
			throw new Error(th);
        }
    }
}
