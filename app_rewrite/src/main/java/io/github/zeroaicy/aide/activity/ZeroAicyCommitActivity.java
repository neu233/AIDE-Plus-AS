package io.github.zeroaicy.aide.activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.view.MenuItem;
import com.aide.ui.scm.GitStatus;
import com.aide.ui.rewrite.R;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import java.util.BitSet;

public class ZeroAicyCommitActivity extends com.aide.ui.activities.CommitActivity {

	public static void startCommitActivity(Activity activity, GitStatus gitStatus, String gitBranch) {
		Intent intent = new Intent(activity, (Class<?>) ZeroAicyCommitActivity.class);
		intent.putExtra("GITSTATUS", (Parcelable) gitStatus);
		intent.putExtra("GITBRANCH", gitBranch);
		activity.startActivity(intent);
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		int itemId = menuItem.getItemId();

		if (itemId == R.id.commitMenuInverse) {
			ListView listView = getListView();
			ListAdapter adapter = listView.getAdapter();
			
			BitSet checkeds = EQ(this);
			
			// 反选
			checkeds.flip(0, adapter.getCount());
			
			// 刷新
			invalidateViews();
			return true;
		}
		if (itemId == R.id.commitMenuSelectAll) {
			ListView listView = getListView();
			ListAdapter adapter = listView.getAdapter();
			
			BitSet checkeds = EQ(this);
			// checkeds.clear();
			// 全选
			checkeds.set(0, adapter.getCount(), true);
			
			// 刷新
			invalidateViews();
			return true;
		}
		
		return super.onOptionsItemSelected(menuItem);
	}

	private void invalidateViews() {
		ListView listView = getListView();
		ListAdapter adapter = listView.getAdapter();
		if (adapter instanceof ArrayAdapter) {
			ArrayAdapter arrayAdapter = ((ArrayAdapter)adapter);
			arrayAdapter.notifyDataSetInvalidated();
		}else{
			listView.invalidateViews();
		}
		
	}

	private ListView getListView() {
		return findViewById(R.id.modifiedFilesList);
	}

}
