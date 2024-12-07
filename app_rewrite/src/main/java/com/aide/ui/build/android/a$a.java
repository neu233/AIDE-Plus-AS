//
// Decompiled by Jadx - 646ms
//
package com.aide.ui.build.android;

import abcd.hy;
import abcd.th;
import com.aide.engine.SyntaxError;
import com.aide.ui.build.android.a;
import com.aide.ui.build.android.a$a;
import com.google.android.gms.internal.ads.iy;
import com.probelytics.annotation.ExceptionEnabled;
import com.probelytics.annotation.FieldMark;
import com.probelytics.annotation.MethodMark;
import com.probelytics.annotation.ParametersEnabled;
import com.probelytics.annotation.TypeMark;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

class a$a implements Callable<Map<String, List<SyntaxError>>> {

    @FieldMark(field = 3008899555223405220L)
    @hy
    final a DW;

    @FieldMark(field = -22998293770523960L)
    private List<a$b> j6;

	public interface TaskFactory {
		List<a$b> getTasks();
	} 
	
	private TaskFactory taskFactory;
	
    @MethodMark(method = 4957435647640520224L)
    public a$a(a aVar, TaskFactory taskFactory) {
        try {
            this.DW = aVar;
			this.taskFactory = taskFactory;
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @Override
    @MethodMark(method = -2245564065867158435L)
    public Map<String, List<SyntaxError>> call() {
        try {
            this.j6 = this.taskFactory.getTasks();
            for (a$b bVar : this.j6) {
                String gn = bVar.gn();
                if (gn != null) {
                    return a.v5(this.DW, a$b.DW(bVar), gn);
                }
            }
            return null;
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }
}

