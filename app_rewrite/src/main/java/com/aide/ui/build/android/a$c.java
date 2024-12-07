//
// Decompiled by Jadx - 772ms
//
package com.aide.ui.build.android;

import abcd.hy;
import abcd.th;
import com.aide.engine.SyntaxError;
import com.aide.ui.build.android.a;
import com.aide.ui.build.android.a$c;
import com.google.android.gms.internal.ads.iy;
import com.probelytics.annotation.ExceptionEnabled;
import com.probelytics.annotation.FieldMark;
import com.probelytics.annotation.MethodMark;
import com.probelytics.annotation.ParametersEnabled;
import com.probelytics.annotation.TypeMark;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@TypeMark(clazz = -645956312330239873L, container = -645956312330239873L, user = true)
class a$c extends FutureTask<Map<String, List<SyntaxError>>> {

    @ExceptionEnabled
    private static boolean jw;

    @ParametersEnabled
    private static boolean mb;

    @FieldMark(field = 2046316356870119936L)
    @hy
    final a WB;


    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    @MethodMark(method = -7470700694817727821L)
    public a$c(a aVar, a$a aVar2) {
        super(aVar2);
        try {
            this.WB = aVar;
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
			throw new Error(th);
        }
    }

    @Override
    @MethodMark(method = 1931302453843594495L)
    protected void done() {
        try {
            if (isCancelled()) {
                return;
            }
            try {
                Map<String, List<SyntaxError>> map = get();
                if (map == null) {
                    a.j6(this.WB);
                } else {
                    a.DW(this.WB, map);
                }
            } catch (InterruptedException unused) {
                a.FH(this.WB);
            } catch (ExecutionException e) {
                a.Hw(this.WB, e.getCause());
            }
        } catch (Throwable th) {
            
            if( th instanceof Error) throw (Error) th;             throw new Error(th);
        }
    }
}

