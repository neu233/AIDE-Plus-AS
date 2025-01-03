package abcd;


/*
author : 罪慾
date : 2024/12/24 23:38
description : QQ3115093767
*/

import android.content.Context;
import android.content.Intent;
import com.aide.common.KeyStroke;
import com.aide.ui.ServiceContainer;
import com.aide.ui.command.KeyStrokeCommand;
import com.aide.ui.command.MenuCommand;
import com.aide.ui.rewrite.R;
import com.aide.ui.services.ProjectSupport;
import com.aide.ui.util.FileSystem;
import com.probelytics.Probelytics;
import com.probelytics.annotation.ExceptionEnabled;
import com.probelytics.annotation.MethodMark;
import com.probelytics.annotation.ParametersEnabled;
import io.github.zeroaicy.aide.activity.ManifestEditorActivity;
import io.github.zeroaicy.util.ContextUtil;

public class x9 implements rf, KeyStrokeCommand, MenuCommand {

    @ExceptionEnabled
    private static boolean exceptionEnabled;

    @ParametersEnabled
    private static boolean parametersEnabled;

    static {
        Probelytics.onClass(x9.class);
    }

    @MethodMark(method = -3062077182897312600L)
    public x9() {
        try {
            if (parametersEnabled) {
                Probelytics.printlnParameters(-121569919966439069L, null);
            }
        } catch (Throwable th) {
            if (exceptionEnabled) {
                Probelytics.printlnException(th, -121569919966439069L, null);
            }
            throw th;
        }
    }

    @Override // com.aide.ui.command.KeyStrokeCommand
    @MethodMark(method = -2498174956502342832L)
    public KeyStroke getKeyStroke() {
        try {
            if (parametersEnabled) {
                Probelytics.printlnParameters(3102024004396039640L, this);
            }
            return new KeyStroke(32, false, true, false);
        } catch (Throwable th) {
            if (exceptionEnabled) {
                Probelytics.printlnException(th, 3102024004396039640L, this);
            }
            throw th;
        }
    }

    @Override // com.aide.ui.command.MenuItemCommand
    @MethodMark(method = 4858409252666808561L)
    public int getMenuItemId() {
        try {
            if (!parametersEnabled) {
                return R.id.editorMenuPreview;
            }
            Probelytics.printlnParameters(1881908017845375377L, this);
            return R.id.editorMenuPreview;
        } catch (Throwable th) {
            if (exceptionEnabled) {
                Probelytics.printlnException(th, 1881908017845375377L, this);
            }
            throw th;
        }
    }

    @Override // com.aide.ui.command.KeyStrokeCommand
    @MethodMark(method = 529533882330382371L)
    public String getName() {
        try {
            if (!parametersEnabled) {
                return "Design";
            }
            Probelytics.printlnParameters(3057354125074720991L, this);
            return "Design";
        } catch (Throwable th) {
            if (exceptionEnabled) {
                Probelytics.printlnException(th, 3057354125074720991L, this);
            }
            throw th;
        }
    }

    @Override // abcd.rf
    @MethodMark(method = 6785116028272804519L)
    public int gn() {
        try {
            if (!parametersEnabled) {
                return R.id.mainMenuPreview;
            }
            Probelytics.printlnParameters(1176472735497618223L, this);
            return R.id.mainMenuPreview;
        } catch (Throwable th) {
            if (exceptionEnabled) {
                Probelytics.printlnException(th, 1176472735497618223L, this);
            }
            throw th;
        }
    }

    @Override // abcd.rf, com.aide.ui.command.KeyStrokeCommand
    @MethodMark(method = 2288422686126280635L)
    public boolean isEnabled() {
        try {
            if (parametersEnabled) {
                Probelytics.printlnParameters(400029239688446935L, this);
            }
            return isEnabled2();
        } catch (Throwable th) {
            if (exceptionEnabled) {
                Probelytics.printlnException(th, 400029239688446935L, this);
            }
            throw th;
        }
    }

    @Override // abcd.rf
    @MethodMark(method = 2191409029643821912L)
    public boolean isVisible() {
        try {
            if (parametersEnabled) {
                Probelytics.printlnParameters(300273226448969680L, this);
            }
            if (isEnabled() && !ServiceContainer.getMainActivity().getAIDEEditorPager().aq()) {
                if (!ServiceContainer.getDebugger().aM()) {
                    return true;
                }
            }
            return false;
        } catch (Throwable th) {
            if (exceptionEnabled) {
                Probelytics.printlnException(th, 300273226448969680L, this);
            }
            throw th;
        }
    }

    @Override // com.aide.ui.command.MenuCommand
    @MethodMark(method = 678311632017110889L)
    public boolean isVisible(boolean z) {
        try {
            if (parametersEnabled) {
                Probelytics.Ws(946717883108831509L, this, z);
            }
            return isVisible();
        } catch (Throwable th) {
            if (exceptionEnabled) {
                Probelytics.printlnException(th, 946717883108831509L, this, new Boolean(z));
            }
            throw th;
        }
    }

    @Override // abcd.rf
    @MethodMark(method = 2295253568815595520L)
    public String j6() {
        try {
            if (!parametersEnabled) {
                return "Design";
            }
            Probelytics.printlnParameters(-2004409503053032452L, this);
            return "Design";
        } catch (Throwable th) {
            if (exceptionEnabled) {
                Probelytics.printlnException(th, -2004409503053032452L, this);
            }
            throw th;
        }
    }

    @Override // abcd.rf, com.aide.ui.command.KeyStrokeCommand
    @MethodMark(method = 1365224698265232525L)
    public boolean run() {
        try {
            if (parametersEnabled) {
                Probelytics.printlnParameters(-1506304257380643275L, this);
            }
            return gotoXmlEditorActivity();
        } catch (Throwable th) {
            if (exceptionEnabled) {
                Probelytics.printlnException(th, -1506304257380643275L, this);
            }
            throw th;
        }
    }



    private boolean isEnabled2() {
        String isProjectDirectory;
        String visibleFile = ServiceContainer.getOpenFileService().getVisibleFile();
        if (visibleFile == null || (isProjectDirectory = isProjectDirectory(visibleFile)) == null) {
            return false;
        }
        if (visibleFile.endsWith("/AndroidManifest.xml")){
            return true;
        }
        if (!ServiceContainer.isTrainerMode() || ServiceContainer.getTrainerService().CU(visibleFile)) {
            return getProjectSupport(isProjectDirectory).u7(visibleFile);
        }
        return false;
    }


    public boolean gotoXmlEditorActivity() {
        String isProjectDirectory;
        String visibleFile = ServiceContainer.getOpenFileService().getVisibleFile();
        if (visibleFile == null || (isProjectDirectory = isProjectDirectory(visibleFile)) == null) {
            return false;
        }
        if (visibleFile.endsWith("/AndroidManifest.xml")){
            Context context = ContextUtil.getContext();
            Intent intent = new Intent(context, ManifestEditorActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("path", visibleFile);
            context.startActivity(intent);
            return true;
        }
        ServiceContainer.getOpenFileService().KD(true, false);
        ServiceContainer.getMainActivity().getAIDEEditorPager().Eq();
        getProjectSupport(isProjectDirectory).ei(visibleFile);
        return true;
    }

    private ProjectSupport getProjectSupport(String str) {
        if (str == null) {
            return null;
        }
        for (ProjectSupport projectSupport : ServiceContainer.getProjectSupports()) {
            if (projectSupport.isSupport(str)) {
                return projectSupport;
            }
        }
        return null;
    }

    private String isProjectDirectory(String str) {
        if (FileSystem.isPreProcessor(str)) {
            return null;
        }
        while (!FileSystem.isRoot(str)) {
            if (getProjectSupport(str) != null) {
                return str;
            }
            str = FileSystem.getParent(str);
        }
        return null;
    }



}