/**
 * @Author ZeroAicy
 * @AIDE AIDE+
 */
package io.github.zeroaicy.aide.highlight;
import com.aide.ui.ThemedActionbarActivity;
import android.os.Bundle;
import com.aide.ui.views.CodeEditText;

/**
 * 可以预览的高亮配置编辑器
 * 多主题支持(考虑中)
 */
public class HighlightActivity2 extends ThemedActionbarActivity {

	/**
	 * CodeEditText
	 * ListView
	 */
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);


	}


	public CodeEditText createCodeEditText() {
		return new CodeEditText(this);
	}

}
