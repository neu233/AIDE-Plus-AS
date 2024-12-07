package io.github.zeroaicy.aide.highlight;
import android.widget.BaseAdapter;
import android.content.Context;
import java.util.List;
import java.util.ArrayList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.content.res.Resources;

public class HighlightAdapter extends BaseAdapter {

    private Context context;

    private List<ColorKind> list=new ArrayList<ColorKind>();

    private boolean isLight;

    public HighlightAdapter(Context context, List<ColorKind> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(List<ColorKind> list) {
        this.list = list;
    }

    public List<ColorKind> getList() {
        return list;
    }

    public void setIsLight(boolean isLight) {
        this.isLight = isLight;
    }

    public boolean isLight() {
        return isLight;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = getColorKindView();
            holder = new ViewHolder(view);
			view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        ColorKind colorKind=  getItem(position);
        holder.title.setText(colorKind.colorName);

        try {
			int colorInt = colorKind.getColor(context, isLight);
			holder.subtitle.setColor(colorInt);
        }
		catch (Throwable e) {
        }

        return view;
    }


    private class ViewHolder {
        public View rootView;
        public TextView title;
		public ColorBackgroundTextView subtitle;

        public ViewHolder(View view) {
            this.rootView = view;
            this.title = view.findViewById(android.R.id.text1);
            this.subtitle = view.findViewById(android.R.id.text2);
        }

    }


    public View getColorKindView() {

        LinearLayout view = new LinearLayout(context);
        view.setOrientation(LinearLayout.VERTICAL);
        
		view.setGravity(Gravity.CENTER_VERTICAL);
		view.setPadding(dp2px(16), dp2px(14), dp2px(16), dp2px(14));

        TextView title = new TextView(context);
        title.setId(android.R.id.text1);
        title.setTextAppearance(context, android.R.style.TextAppearance_Large);
        title.setTextSize(16);
		title.setPadding(0, 0, 0, dp2px(5));

        ColorBackgroundTextView subtitle = new ColorBackgroundTextView(context);
		subtitle.setId(android.R.id.text2);
		subtitle.setTextSize(14);

		view.addView(title);
        view.addView(subtitle, -1, -2);

        return view;
    }

	public static int dp2px(float dpValue) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ColorKind getItem(int p1) {
        return list.get(p1);
    }

    @Override
    public long getItemId(int p1) {
        return p1;
    }


}
