package io.github.zeroaicy.aide.completion;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.aide.common.AIDEHelpActivityStarter;
import com.aide.common.AppLog;
import com.aide.engine.SourceEntity;
import com.aide.ui.AIDEEditor;
import com.aide.ui.ServiceContainer;
import com.aide.ui.rewrite.R;
import io.github.zeroaicy.aide.ui.services.ThreadPoolService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EditorCompletionAdapter extends ArrayAdapter<SourceEntity> {

	private Map<String, ApiVersionInfo> infoMap = new ConcurrentHashMap<>();

	private AIDEEditor aideEditor;

	private final List<SourceEntity> sourceEntitys;
    public EditorCompletionAdapter(AIDEEditor aideEditor, List<SourceEntity> sourceEntitys) {
        super(aideEditor.getContext(), R.layout.completion_list_entry, sourceEntitys);
		this.aideEditor = aideEditor;
		this.sourceEntitys = sourceEntitys;

		ApiVersionCompletion.preLoad(getContext());
		initAsync();
    }

    private void DW(TextView textView, int start, int end, int color) {
		((Spannable) textView.getText()).setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void j6(TextView textView, int start, int end) {
		((Spannable) textView.getText()).setSpan(new StyleSpan(1), start, end, 33);
    }


	static class ViewHolder {
		View rootView;

		TextView completionEntryName;
		ImageView completionEntryImage;
		ImageView completionHelpButton;
		public ViewHolder(View rootView) {
			this.rootView = rootView;

			completionEntryName = findViewById(R.id.completionEntryName);
			completionEntryImage = findViewById(R.id.completionEntryImage);
			completionHelpButton = findViewById(R.id.completionHelpButton);

		}

		public final <T extends View> T findViewById(int id) {
			return this.rootView.findViewById(id);
		}
	}


	private int clearCount = 0;
	@Override
	public void clear() {
		super.clear();
		clearCount++;
		if (clearCount < 0) {
			// 防止溢出
			clearCount = 0;
		}
		if (clearCount % 2 == 0 
			&& this.infoMap.size() > 0x2000) {
			// 减少缓存clear次数
			this.infoMap.clear();
		}

	}

	@Override
	public void addAll(Collection<? extends SourceEntity> collection) {
		super.addAll(collection);
	}


	// 从 add -> addAll 减少 notifyDataSetChanged调用次数
	@Override
	public void notifyDataSetChanged() {
		// AppLog.println_e("notifyDataSetChanged()");
		List<SourceEntity> sourceEntitys = this.sourceEntitys;
		if (!sourceEntitys.isEmpty()) {
			initAsync();
		}
		super.notifyDataSetChanged();
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View entryView;

		ViewHolder viewholder;
        if (convertView == null) {
			entryView = LayoutInflater.from(getContext()).inflate(R.layout.completion_list_entry, parent, false);
			viewholder = new ViewHolder(entryView);
			entryView.setTag(viewholder);     //将viewholder存储在view中
		} else {
			entryView = convertView;

			viewholder = (ViewHolder) entryView.getTag();
		}

		SourceEntity sourceEntity = getItem(position);

		if (sourceEntity == null) {
			viewholder.completionEntryName.setText("No matches");
			viewholder.completionEntryImage.setImageResource(R.drawable.browser_empty);
			viewholder.completionHelpButton.setVisibility(View.GONE);
			return entryView;
		} else {
			int sourceEntityType = sourceEntityTypes[sourceEntity.j3().ordinal()];

			TextView entryNameView = viewholder.completionEntryName;

			// entityName
			String entityName = sourceEntity.Mr();

			switch (sourceEntityType) {
				case MethodType: 
				case FieldType: 
				case VariableType: {
						// : + typeName
						String typeNameSuffix = sourceEntity.a8();
						if (typeNameSuffix != null) {
							String text = entityName + typeNameSuffix;
							entryNameView.setText(text, TextView.BufferType.SPANNABLE);
							DW(entryNameView, entityName.length(), text.length(), aideEditor.getResources().getColor(R.color.browser_label_gray));
						} else {
							entryNameView.setText(entityName);
						}
					}
					break;
				case ClassType: {
						//
						if (sourceEntity.gW()) {
							// sourceEntity.J8() 包名
							String text = entityName + " - " + sourceEntity.J8();
							entryNameView.setText(text, TextView.BufferType.SPANNABLE);
							DW(entryNameView, entityName.length(), text.length(), aideEditor.getResources().getColor(R.color.browser_label_gray));
						} else {
							entryNameView.setText(entityName);
						}
					}
					break;
				case KeywordType: {
						entryNameView.setText(entityName, TextView.BufferType.SPANNABLE);
						j6(entryNameView, 0, entityName.length());
					}
					break;
				default:
					entryNameView.setText(entityName);
					break;
			}

			// 追加 api 版本信息
			final String docUrl = sourceEntity.Ws();
			ApiVersionInfo info = docUrl == null ? ApiVersionInfo.Empty : infoMap.get(docUrl);
			setTo(entryNameView, info);


			ImageView completionEntryImage = viewholder.completionEntryImage;
			switch (sourceEntityType) {
				case MethodType: {
						if (sourceEntity.er()) {
							completionEntryImage.setImageResource(R.drawable.box_light_red);
						} else {
							completionEntryImage.setImageResource(R.drawable.box_red);
						}
					}
					break;
				case FieldType:
					if (sourceEntity.er()) {
						completionEntryImage.setImageResource(R.drawable.box_light_blue);
					} else {
						completionEntryImage.setImageResource(R.drawable.box_blue);
					}
					break;
				case VariableType:
					completionEntryImage.setImageResource(R.drawable.box_blue);
					break;
				case ClassType:
					if (sourceEntity.er()) {
						completionEntryImage.setImageResource(R.drawable.objects_light);
					} else {
						completionEntryImage.setImageResource(R.drawable.objects);
					}
					break;
				case PackageType:
					completionEntryImage.setImageResource(R.drawable.pakage);					
					break;
				default:
					completionEntryImage.setImageResource(R.drawable.browser_empty);
					break;
			}

			View completionHelpButton = viewholder.completionHelpButton;

			completionHelpButton.setVisibility(docUrl != null ? View.VISIBLE : View.GONE);
			if (docUrl != null) {
				completionHelpButton.setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View v) {
							ServiceContainer.getMainActivity().getAIDEEditorPager().Eq();
							AIDEHelpActivityStarter.DW(ServiceContainer.getMainActivity(), docUrl, com.aide.ui.activities.a.v5().toString());
						}
					});
				// new a(this, Ws));
			}
		}
		return entryView;
    }

	private void initAsync() {
		List<SourceEntity> sourceEntitys = this.sourceEntitys;
		if (sourceEntitys.isEmpty()) {
			return;
		}
		final ArrayList<SourceEntity> sourceEntitysCopy = new ArrayList<SourceEntity>(sourceEntitys);

		ThreadPoolService.getDefaultThreadPoolService()
			.submit(new Runnable(){
				@Override
				public void run() {
					initApiVersionInfo(sourceEntitysCopy, infoMap);
				}
			});
	}

	private static void initApiVersionInfo(List<SourceEntity> sourceEntitys, Map<String, ApiVersionInfo> infoMap) {
		if (infoMap == null) {
			return;
		}
		for (SourceEntity sourceEntity : sourceEntitys) {
			if (sourceEntity == null) {
				continue;
			}
			final String docUrl = sourceEntity.Ws();
			if (docUrl == null) {
				continue;
			}
			if (infoMap.containsKey(docUrl)) {
				continue;
			}
			ApiVersionInfo apiVersionInfo = getApiVersionInfo(sourceEntity);
			// ConcurrentHashMap 不允许 value 为 null
			if (apiVersionInfo == null) {
				apiVersionInfo = ApiVersionInfo.Empty;
			}
			infoMap.put(docUrl, apiVersionInfo);

		}
	}

	private static ApiVersionInfo getApiVersionInfo(SourceEntity sourceEntity) {
		if (sourceEntity == null) {
			return null;
		}
		int sourceEntityType = sourceEntityTypes[sourceEntity.j3().ordinal()];

		if (sourceEntityType != ClassType
			&& sourceEntityType != MethodType
			&& sourceEntityType != FieldType) {
			return null;
		}

		final String docUrl = sourceEntity.Ws();
		if (TextUtils.isEmpty(docUrl)) {
			return null;
		}

		int typeNameEnd = docUrl.indexOf(".html");
		String typeName = docUrl.substring(0, typeNameEnd).replace('.', '$');

		int memberInfoEnd = docUrl.indexOf("#") + 1;
		String memberInfo = memberInfoEnd > 0 ? docUrl.substring(memberInfoEnd) : "";
		switch (sourceEntityType) {
			case ClassType:
				return ApiVersionCompletion.getApiVersionInfo(typeName);
			case FieldType:
				return ApiVersionCompletion.getFieldApiVersionInfo(typeName, memberInfo);
			case MethodType:
				// 没有返回类型信息
				return ApiVersionCompletion.getMethodApiVersionInfo(typeName, memberInfo);
		}
		return null;
	}

	public static void setTo(TextView completionEntryName, ApiVersionInfo result) {
		// 消除 缓存view遗留的Paint.STRIKE_THRU_TEXT_FLAG
		if (result == null || result == ApiVersionInfo.Empty) {
			int flags = completionEntryName.getPaint().getFlags();
			if ((flags & Paint.STRIKE_THRU_TEXT_FLAG) == 0) {
				return;
			}
			flags &= ~Paint.STRIKE_THRU_TEXT_FLAG;
			completionEntryName.getPaint().setFlags(flags);
			completionEntryName.invalidate();
			return;
		} else if (result.isRemoved()) {
			int flags = completionEntryName.getPaint().getFlags();
			flags |= Paint.STRIKE_THRU_TEXT_FLAG;
			completionEntryName.getPaint().setFlags(flags);
			completionEntryName.invalidate();
		}

		CharSequence info = result.getInfo(completionEntryName.getContext());
		if (info.length() > 0) {
			SpannableString text = new SpannableString(info);
			text.setSpan(new ForegroundColorSpan(0xFFAAAAAA), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			text.setSpan(new AbsoluteSizeSpan(12, true), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

			completionEntryName.append("\n");
			completionEntryName.append(text);

			// View parent = (View) completionEntryName.getParent();
			// if (parent.getVisibility() != View.VISIBLE) parent.setVisibility(View.VISIBLE);
		}


	}


	public static final int MethodType = 1;
	public static final int FieldType = 2;

	public static final int VariableType = 3;
	public static final int ClassType = 4;
	public static final int KeywordType = 5;
	public static final int PackageType = 6;


	static final int[] sourceEntityTypes;
	static {
		int[] iArr = new int[SourceEntity.b.values().length];
		sourceEntityTypes = iArr;

		try {
			iArr[SourceEntity.b.Method.ordinal()] = MethodType;
		}
		catch (NoSuchFieldError unused) {
		}
		try {
			sourceEntityTypes[SourceEntity.b.Field.ordinal()] = FieldType;
		}
		catch (NoSuchFieldError unused2) {
		}
		try {
			sourceEntityTypes[SourceEntity.b.Variable.ordinal()] = VariableType;
		}
		catch (NoSuchFieldError unused3) {
		}
		try {
			sourceEntityTypes[SourceEntity.b.Class.ordinal()] = ClassType;
		}
		catch (NoSuchFieldError unused4) {
		}
		try {
			sourceEntityTypes[SourceEntity.b.Keyword.ordinal()] = KeywordType;
		}
		catch (NoSuchFieldError unused5) {
		}
		try {
			sourceEntityTypes[SourceEntity.b.Package.ordinal()] = PackageType;
		}
		catch (NoSuchFieldError unused6) {
		}
	}

}

