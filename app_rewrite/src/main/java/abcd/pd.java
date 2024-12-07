//
// Decompiled by Jadx - 990ms
//
package abcd;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.ScrollView;
import android.widget.TextView;
import com.aide.common.MessageBox;
import com.aide.ui.ServiceContainer;
import com.aide.ui.util.FileSystem;
import com.probelytics.Probelytics;
import com.probelytics.annotation.ExceptionEnabled;
import com.probelytics.annotation.FieldMark;
import com.probelytics.annotation.MethodMark;
import com.probelytics.annotation.ParametersEnabled;
import com.probelytics.annotation.TypeMark;
import com.aide.ui.services.ProjectService;
import android.text.SpannableString;
import android.view.View;
import android.text.style.ClickableSpan;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.text.Spannable;
import java.io.File;
import com.aide.ui.services.FileBrowserService;
import com.aide.ui.MainActivity;

public class pd extends MessageBox implements DialogInterface.OnClickListener {
    // no keep
	// private AlertDialog k2;
	//this.k2 = create;


    public pd() {}

    public Dialog onCreateDialog(Activity activity) {
		ProjectService projectService = ServiceContainer.getProjectService();

		Spanned oldSpanned = Html.fromHtml(projectService.getProjectAttribute());

		TextView textView = new TextView(activity);
		textView.setPadding((int) (activity.getResources().getDisplayMetrics().density * 10.0f), (int) (activity.getResources().getDisplayMetrics().density * 10.0f), (int) (activity.getResources().getDisplayMetrics().density * 10.0f), (int) (activity.getResources().getDisplayMetrics().density * 10.0f));

		// 美化
		Spannable projectInfoSpannable = beautifyProjectInfoDialog(textView, oldSpanned);
		textView.setText(projectInfoSpannable);


		ScrollView scrollView = new ScrollView(activity);
		scrollView.addView(textView);

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setView(scrollView)
			.setCancelable(true)
			.setPositiveButton(0x7f0d05d7, this)
			.setNeutralButton(0x7f0d05da, this);

		String currentAppHome = projectService.getCurrentAppHome();
		builder.setTitle(activity.getResources().getString(0x7f0d05db, "'" + FileSystem.getName(currentAppHome) + "'"));


		return builder.create();

    }

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			dialog.dismiss();
		} else if (which == DialogInterface.BUTTON_NEUTRAL) {
			dialog.dismiss();
            ProjectService projectService = ServiceContainer.getProjectService();
			projectService.showSwitchVariantDialog();
		}
	}

	// 也可以不覆盖重写pd类
	// 也可在 textView.setText前 调用 beautifyProjectInfoDialog
	public static Spannable beautifyProjectInfoDialog(TextView textView, Spanned oldSpanned) {
		// 美化
		textView.setTextIsSelectable(true);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		textView.setTextSize(14);

		SpannableString fromHtml = new SpannableString(oldSpanned);
		String fromHtmlString = fromHtml.toString();


		Pattern pattern = Pattern.compile("(?:^|\\s)(/\\S+(?:\\s\\S+)*)(?=\\s|$)"); // 匹配以空格或开头开头，以空格或结尾结尾的路径

		Matcher matcher = pattern.matcher(fromHtmlString);

		// int color = Utils.getThemeAttrColor(activity, android.R.attr.textColorTertiary);
		while (matcher.find()) {
			final String filePath = matcher.group().trim();

			if (!FileSystem.exists(filePath)) {
				continue;
			}
			int start = matcher.start();

			{
				label: for (int i = start; i < fromHtmlString.length(); i++) {
					if (fromHtmlString.charAt(i) != ' '
						&& fromHtmlString.charAt(i) != '\n') {
						start = i;
						break label;
					}
				}
			}

			fromHtml.setSpan(new GotoFileSpan(filePath), start, matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			//fromHtml.setSpan(new ForegroundColorSpan(color), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			//fromHtml.setSpan(new UnderlineSpan(), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return fromHtml;
	}

	public static class GotoFileSpan extends ClickableSpan {
		String path;

		public GotoFileSpan(String path) {
			this.path = path;
		}

		@Override
		public void onClick(View widget) {
			if (path.endsWith(".aar")) {
				gotoFile(new File(this.path).getParent());

				return;
			}
			gotoFile(this.path);
		}

		private static void gotoFile(String filePath) {
			if (filePath == null) {
				return;
			}
			FileBrowserService fileBrowserService = ServiceContainer.getFileBrowserService();
			if (fileBrowserService != null) {
				fileBrowserService.Hw(filePath);
			}
            MainActivity mainActivity = ServiceContainer.getMainActivity();
			if (mainActivity != null) {
				mainActivity.k2();
			}
		}
	}
}
