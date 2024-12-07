//
// Decompiled by Jadx - 2935ms
//
package com.aide.ui.build.android;

import abcd.th;
import abcd.wf;
import abcd.xf;
import android.os.Build;
import com.aide.common.AppLog;
import com.aide.common.StreamUtilities;
import com.aide.ui.build.android.a$b;
import com.aide.ui.util.FileSystem;
import com.google.android.gms.internal.ads.iy;
import com.probelytics.annotation.ExceptionEnabled;
import com.probelytics.annotation.FieldMark;
import com.probelytics.annotation.MethodMark;
import com.probelytics.annotation.ParametersEnabled;
import com.probelytics.annotation.TypeMark;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@TypeMark(clazz = -1010823170957869711L, container = -1010823170957869711L, user = true)
class a$b {

    @ParametersEnabled
    private static boolean VH;

    @ExceptionEnabled
    private static boolean gn;

    @FieldMark(field = -1571365023117879528L)
    private final String DW;

    @FieldMark(field = 6993245178019010064L)
    private final String FH;

    @FieldMark(field = 6883242355877066451L)
    private final Map<String, List<String>> Hw;

    @FieldMark(field = 3367034270560319580L)
    private final boolean Zo;

    @FieldMark(field = -3295612150393503995L)
    private final String j6;

    @FieldMark(field = -590303393023326637L)
    private final List<String> v5;
	
	
	@MethodMark(method = -1082841311919215507L)
    static String DW(a$b a_b) {
        return a_b.j6;
    }

    @MethodMark(method = -7172434805806024095L)
    public a$b(a aVar, String str, String str2, String str3, Map<String, List<String>> map, List<String> list, boolean z) {
        try {
            this.j6 = str2;
            this.DW = str;
            this.FH = str3;
            this.Hw = map;
            this.v5 = list;
            this.Zo = z;
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @MethodMark(method = 227909245881707665L)
    private boolean FH(String str, String str2, File file) {
        try {
            if (this.Zo) {
                return true;
            }
            File file2 = new File(v5(file.getPath(), str, str2));
            if (file2.isFile()) {
                return file2.lastModified() < file.lastModified();
            }
            return true;
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @MethodMark(method = 3007469703513941775L)
    private String Hw(byte[] bArr, int i) {
        try {
            String str = "";
            try {
                str = StreamUtilities.readTextReader(new InputStreamReader(new ByteArrayInputStream(bArr)));
            } catch (Exception unused) {
            }
            String trim = str.trim();
            if (trim.length() != 0) {
                return trim;
            }
            return "aidl exited with code " + i;
        } catch (Throwable th) {
            
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @MethodMark(method = 6318680938885244092L)
    private String VH(String str, List<String> list, String str2, File file) {
        String u7;
        try {
            File[] listFiles = file.listFiles();
            if (listFiles == null) {
                return null;
            }
            for (File file2 : listFiles) {
                if (file2.isDirectory()) {
                    String VH2 = VH(str, list, str2, file2);
                    if (VH2 != null) {
                        return VH2;
                    }
                } else if (file2.isFile()) {
                    String path = file2.getPath();
                    if (path.toLowerCase().endsWith(".aidl") && FH(str, str2, file2) && (u7 = u7(path, list, str, str2)) != null) {
                        return u7;
                    }
                } else {
                    continue;
                }
            }
            return null;
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @MethodMark(method = -2659660403972098180L)
    private void Zo(List<String> list) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Running aidl ");
            for (int i = 1; i < list.size(); i++) {
                sb.append('\"');
                sb.append(list.get(i));
                sb.append('\"');
                if (i != list.size() - 1) {
                    sb.append(" ");
                }
            }
            AppLog.d(sb.toString());
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @MethodMark(method = -653427425690642280L)
    private void j6() {
        wf j6 = null;
        try {
            if (!Thread.interrupted()) {
                if (Build.VERSION.SDK_INT < 29 && !a.Zo()) {
                    try {
                        j6 = xf.j6(Arrays.asList("/system/bin/chmod", "777", this.DW), null, null, true, (OutputStream) null, (byte[]) null);
                    } catch (Exception unused) {
                        wf j62 = xf.j6(Arrays.asList("/system/xbin/chmod", "777", this.DW), (String) null, null, true, (OutputStream) null, (byte[]) null);
                        if (j62.DW() != 0) {
                            throw new IOException("Could not make " + this.DW + " executable - exit code " + j62.DW());
                        }
                    }
                    if (j6.DW() != 0) {
                        throw new IOException("Could not make " + this.DW + " executable - exit code " + j6.DW());
                    }
                    a.VH(true);
                    return;
                }
                return;
            }
            throw new InterruptedException();
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @MethodMark(method = -1068951146983634135L)
    private String u7(String str, List<String> list, String str2, String str3) {
        try {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.DW);
            arrayList.add("-p" + this.FH);
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                arrayList.add("-I" + it.next());
            }
            arrayList.add("-I" + str3);
            arrayList.add(str);
            String v5 = v5(str, str2, str3);
            File file = new File(FileSystem.getParent(v5));
            if (!file.exists() && !file.mkdirs()) {
                return "Could not create directory " + FileSystem.getParent(v5);
            }
            arrayList.add(v5);
            Zo(arrayList);
            wf j6 = xf.j6(arrayList, (String) null, (Map) null, true, (OutputStream) null, (byte[]) null);
            if (j6.DW() != 0) {
                if (j6.DW() == 11) {
                    AppLog.d("aidl exited with code 11");
                } else {
                    return Hw(j6.j6(), j6.DW());
                }
            }
            if (!new File(v5).exists()) {
                try {
                    new FileOutputStream(v5).close();
                    return null;
                } catch (IOException e) {
                    return "Could not create " + v5 + ": " + e.getMessage();
                }
            }
            if (!FH(str2, str3, new File(str))) {
                return null;
            }
            FileSystem.ensureUpdatedFileLastModified(v5);
            return null;
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @MethodMark(method = 4750742892298729216L)
    private String v5(String str, String str2, String str3) {
        try {
            if (!str2.endsWith(File.separator)) {
                str2 = str2 + File.separator;
            }
            if (str.startsWith(str2)) {
                return new File(str3, str.substring(str2.length(), str.length() - 5) + ".java").getPath();
            }
            throw new IllegalArgumentException();
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @MethodMark(method = 1969741082957069825L)
    public String gn() {
        try {
            for (String str : this.Hw.keySet()) {
                if (!new File(str).exists()) {
                    new File(str).mkdirs();
                }
            }
            j6();
            for (String str2 : this.Hw.keySet()) {
                if (!new File(str2).exists()) {
                    new File(str2).mkdirs();
                }
            }
            if (this.v5 != null) {
                for (String str3 : this.v5) {
                    for (Map.Entry<String, List<String>> entry : this.Hw.entrySet()) {
                        String key = entry.getKey();
                        List<String> value = entry.getValue();
                        for (String str4 : value) {
                            if (str3.startsWith(str4) && FH(str4, key, new File(str3))) {
                                String u7 = u7(str3, value, str4, key);
                                if (u7 != null) {
                                    return u7;
                                }
                            }
                        }
                    }
                    return "No source dir found for " + str3;
                }
                return null;
            }
            for (Map.Entry<String, List<String>> entry2 : this.Hw.entrySet()) {
                String key2 = entry2.getKey();
                List<String> value2 = entry2.getValue();
                for (String str5 : value2) {
                    String VH2 = VH(str5, value2, key2, new File(str5));
                    if (VH2 != null) {
                        return VH2;
                    }
                }
            }
            return null;
        } catch (Throwable th) {
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }
}

