//
// Decompiled by Jadx - 1315ms
//
package com.aide.ui.build.android;

import android.os.Build;
import androidx.annotation.Keep;
import com.aide.common.AppLog;
import com.aide.engine.SyntaxError;
import com.aide.ui.ServiceContainer;
import com.aide.ui.project.AndroidProjectSupport;
import com.aide.ui.services.AssetInstallationService;
import com.aide.ui.services.ProjectService;
import com.aide.ui.util.FileSystem;
import com.probelytics.annotation.FieldMark;
import com.probelytics.annotation.MethodMark;
import io.github.zeroaicy.aide.extend.ZeroAicyExtensionInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * 被AndroidProjectBuildService引用
 * AidlService
 */
 @Keep
public class a {

    @FieldMark(field = 239202569234476859L)
    private static boolean Hw;

    @FieldMark(field = -1439319343464242515L)
    private a$c lastTask;

    @FieldMark(field = 2429444071915123500L)
    private f FH;

    @FieldMark(field = 2646723890928786381L)
    private final ExecutorService executorService;

    @MethodMark(method = 2143289394818954145L)
    public a() {
        this.executorService = ZeroAicyExtensionInterface.getProjectExecutorService();
    }

    @MethodMark(method = -7598314424750600748L)
    static void DW(a aVar, Map<String, List<SyntaxError>> map) {
        aVar.Ws(map);
    }

    @MethodMark(method = -7836100668456117327L)
    private Map<String, List<SyntaxError>> EQ(String str, String str2) {
        int i;
        try {
            HashMap<String, List<SyntaxError>> syntaxErrorMap = new HashMap<>();
            for (String str3 : str2.split("\n")) {
                String trim = str3.trim();
                if (trim.length() > 0) {
                    try {
                        int indexOf = trim.indexOf(58);
                        if (indexOf > 0) {
                            String substring = trim.substring(0, indexOf);
                            if (FileSystem.KD(substring)) {
                                int i2 = indexOf + 1;
                                int indexOf2 = trim.indexOf(58, i2);
                                if (indexOf2 < 0) {
                                    indexOf2 = trim.indexOf(32, i2);
                                }
                                if (indexOf2 > 0) {
                                    try {
                                        i = Integer.parseInt(trim.substring(i2, indexOf2));
                                    } catch (NumberFormatException unused) {
                                        i = 1;
                                    }
                                    String trim2 = trim.substring(indexOf2 + 1, trim.length()).trim();
                                    while (trim2.toLowerCase().startsWith("error:")) {
                                        trim2 = trim2.substring(6, trim2.length()).trim();
                                    }
                                    SyntaxError syntaxError = gn("aapt", i, trim2);
                                    if (!syntaxErrorMap.containsKey(substring)) {
                                        syntaxErrorMap.put(substring, new ArrayList<SyntaxError>());
                                    }
									syntaxErrorMap.get(substring).add(syntaxError);
                                }
                            }
                        }
                    } catch (Exception e) {
                        AppLog.e(e);
                    }
                    if (!syntaxErrorMap.containsKey(str)) {
                        syntaxErrorMap.put(str, new ArrayList<SyntaxError>());
                    }
					syntaxErrorMap.get(str).add(gn("aapt", 1, trim));
                }
            }
            return syntaxErrorMap;
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @MethodMark(method = 637887118825791720L)
    static void FH(a aVar) {
        aVar.J8();
    }

    @MethodMark(method = 304434648875156499L)
    static void Hw(a aVar, Throwable th) {
        aVar.J0(th);
    }

    @MethodMark(method = -1818287024386762077L)
    private void J0(Throwable th) {
        try {
            AppLog.e(th);
            if (this.FH != null) {
                this.FH.g3();
            }
        } catch (Throwable th2) {
            if( th2 instanceof Error) throw (Error) th2;
            throw new Error(th2);
        }
    }

    @MethodMark(method = -2893096954932841107L)
    private void J8() {
        try {
            if (this.FH != null) {
                this.FH.J0();
            }
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @MethodMark(method = -1613577850507724712L)
    static boolean VH(boolean z) {
        Hw = z;
        return z;
    }

    @MethodMark(method = 2291577116539024620L)
    private void Ws(Map<String, List<SyntaxError>> map) {
        try {
            if (this.FH != null) {
                this.FH.Mz(map);
            }
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @MethodMark(method = 3364127788162686923L)
    static boolean Zo() {
        return Hw;
    }

    @MethodMark(method = 609663216955893907L)
    private SyntaxError gn(String str, int i, String str2) {
        try {
            SyntaxError syntaxError = new SyntaxError();
            syntaxError.jw = i;
            syntaxError.fY = 1;
            syntaxError.qp = i;
            syntaxError.k2 = 1000;
            syntaxError.zh = str + ": " + str2;
            return syntaxError;
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @MethodMark(method = 473248448452984248L)
    static void j6(a aVar) {
        aVar.we();
    }

    @MethodMark(method = 4174856810662674400L)
	// getAIDL路径
    private String tp() {
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                return ServiceContainer.getContext().getApplicationInfo().nativeLibraryDir + "/libaidl.so";
            }
            return AssetInstallationService.DW("aidl", false);
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @MethodMark(method = -2638105283236111335L)
    private a$b getTaskInfo(String str, List<String> list, boolean z, String str2) {
        try {
            return new a$b(this, str2, str, ServiceContainer.getAssetInstallationService().getFrameworkAidlPath(), AndroidProjectSupport.hz(ServiceContainer.getProjectService().vy(str), ServiceContainer.getProjectService().getFlavor()), list, z);
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @MethodMark(method = 4158781679847656875L)
    static Map<String, List<SyntaxError>> v5(a aVar, String str, String str2) {
        return aVar.EQ(str, str2);
    }

    @MethodMark(method = -3535590781531786879L)
    private void we() {
        try {
            if (this.FH != null) {
                this.FH.j6();
            }
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    // AndroidProjectBuildService::DW() call
	@Keep
    public void QX(f fVar) {
        try {
            this.FH = fVar;
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    // AndroidProjectBuildService::sy() call
	@Keep
	public void XL(final String str, final boolean z, final boolean z2) {
        try {
            final String tp = tp();
            if (this.lastTask != null) {
                this.lastTask.cancel(true);
                this.lastTask = null;
            }
			a$a.TaskFactory taskFactory = new a$a.TaskFactory(){
				@Override
				public List<a$b> getTasks() {
					ArrayList<a$b> arrayList = new ArrayList<>();
					if (z2) {
						for (String str2 : ServiceContainer.getProjectService().getMainAppWearApps()) {
							if (!str.equals(str2)) {
								arrayList.add(getTaskInfo(str2, null, false, tp));
							}
						}
					}
					arrayList.add(getTaskInfo(str, null, z, tp));
					return arrayList;
				}
			};
            
            a$c task = new a$c(this, new a$a(this, taskFactory));
            this.lastTask = task;
            this.executorService.execute(task);
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }
	
	// AndroidProjectBuildService$c$c::run() 调用
	// AndroidProjectBuildService::yO()
    @Keep
    public void aM(final List<String> list) {
        try {
            final String tp = tp();
            if (this.lastTask != null) {
                this.lastTask.cancel(true);
                this.lastTask = null;
            }
			// 异步，如果在主线程则非常耗时
			a$a.TaskFactory taskFactory = new a$a.TaskFactory(){
				@Override
				public List<a$b> getTasks() {
					// 比较耗时，当在线程池运行
					ArrayList<a$b> arrayList = new ArrayList<>();
					ProjectService projectService = ServiceContainer.getProjectService();
					List<String> mainAppWearApps = projectService.getMainAppWearApps();
					for (String next : mainAppWearApps) {
						arrayList.add(getTaskInfo(next, list, false, tp));
					}
					return arrayList;
				}
			};
            a$c task = new a$c(this, new a$a(this, taskFactory));
            this.lastTask = task;
            this.executorService.execute(task);
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }
}

