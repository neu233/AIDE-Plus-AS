package io.github.zeroaicy.aide.aaptcompiler.utils;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;
import android.widget.ZoomButton;

import androidx.annotation.NonNull;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import org.apache.bcel.classfile.JavaClass;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StyleUtils {

    private static final Multimap<String, String> sViewStyleMap = ArrayListMultimap.create();
    private static final Map<String, ImmutableSet<String>> sLayoutParamsMap = new HashMap<>();

    static {
        putLayoutParams(ViewGroup.class);
        putLayoutParams(AbsoluteLayout.class);
        putLayoutParams(FrameLayout.class);
        putLayoutParams(RelativeLayout.class);
        putLayoutParams(LinearLayout.class);
        putLayoutParams(GridLayout.class);
        putLayoutParams(TableLayout.class);

        putStyle(View.class);
        putStyle(ViewGroup.class);

        sViewStyleMap.put(ViewGroup.class.getSimpleName(), "ViewGroup_MarginLayout");
        putStyle(LinearLayout.class);
        putStyle(FrameLayout.class);
        putStyle(ListView.class);
        putStyle(RelativeLayout.class);
        putStyle(EditText.class);
        putStyle(TextView.class);
        putStyle(Button.class);
        putStyle(CompoundButton.class);
        putStyle(ImageButton.class);
        putStyle(CheckBox.class);
        putStyle(ProgressBar.class);
        putStyle(SeekBar.class);
        putStyle(TableLayout.class);
        putStyle(GridLayout.class);
        putStyle(CalendarView.class);
        putStyle(DatePicker.class);
        putStyle(ViewFlipper.class);
        putStyle(ViewSwitcher.class);
        putStyle(AbsoluteLayout.class);
        putStyle(ZoomButton.class);
        putStyle(WebView.class);
    }

    public static Set<String> getClasses(String... classNames) {
        Set<String> classes = new HashSet<>();
        for (String className : classNames) {
            Collection<String> strings = sViewStyleMap.get(className);
            if (strings != null) {
                classes.addAll(strings);
                for (String string : strings) {
                    if (ImmutableSet.copyOf(classNames).contains(string)) {
                        continue;
                    }

                    classes.addAll(getClasses(string));
                }
            }
        }
        return classes;
    }

    public static void putStyles(JavaClass javaClass) {
        JavaClass[] superClasses = BytecodeScanner.getSuperClasses(javaClass);
        String viewSimpleName = getSimpleName(javaClass.getClassName());
        for (JavaClass superClass : superClasses) {
            if (Object.class.getName().equals(superClass.getClassName())) {
                continue;
            }
            String simpleName = getSimpleName(superClass.getClassName());
            sViewStyleMap.put(viewSimpleName, simpleName);
        }

        sViewStyleMap.put(viewSimpleName, viewSimpleName);

        if (BytecodeScanner.isViewGroup(javaClass)) {
            putLayoutParams(javaClass);
        }
    }

    public static void putLayoutParams(JavaClass javaClass) {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        JavaClass[] superClasses = BytecodeScanner.getSuperClasses(javaClass);
        Arrays.stream(superClasses)
                .map(JavaClass::getClassName)
                .filter(it -> !Object.class.getName().equals(it))
                .filter(it -> !View.class.getName().equals(it))
                .forEach(it -> builder.add(getSimpleName(it) + "_Layout"));
        sLayoutParamsMap.put(getSimpleName(javaClass.getClassName()) + "_Layout", builder.build());
    }

    public static void putLayoutParams(@NonNull Class<? extends ViewGroup> viewGroup) {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        Class<?> current = viewGroup;
        while (current != null) {
            if (Object.class.getName().equals(current.getName())) {
                break;
            }
            // no layout params for view
            if (View.class.getName().equals(current.getName())) {
                break;
            }

            builder.add(current.getSimpleName() + "_Layout");

            if ("ViewGroup".equals(current.getSimpleName())) {
                builder.add("ViewGroup_MarginLayout");
            }

            current = current.getSuperclass();
        }
        sLayoutParamsMap.put(viewGroup.getSimpleName() + "_Layout", builder.build());
    }

    private static void putStyle(@NonNull Class<? extends View> view) {
        Class<?> current = view;
        while (current != null) {
            if ("java.lang.Object".equals(current.getName())) {
                break;
            }
            sViewStyleMap.put(view.getSimpleName(), current.getSimpleName());
            current = current.getSuperclass();
        }
    }

    public static String getSimpleName(String name) {
        if (name == null) {
            return "";
        }
        if (name.contains(".")) {
            return name.substring(name.lastIndexOf('.') + 1);
        }
        return name;
    }
}
