package io.github.zeroaicy.aide.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import com.aide.ui.views.SplitView;
import com.aide.ui.views.SplitView.OnSplitChangeListener;

public class ZeroAicySplitView extends SplitView {
	OnSplitInterceptListener onSplitInterceptListener;

	private OnSplitChangeListener listener;
	public void setOnSplitInterceptListener(OnSplitInterceptListener onSplitInterceptListener) {
		this.onSplitInterceptListener = onSplitInterceptListener;
	}

	public OnSplitInterceptListener getOnSplitInterceptListener() {
		return onSplitInterceptListener;
	}

	public interface OnSplitInterceptListener {
		public boolean openSplit(boolean isHorizontal, boolean animator);
		public boolean closeSplit(boolean animator, Runnable animatorListenerAdapterRunable);
	}
	
	
	
	public ZeroAicySplitView(Context context) {
		this(context, null);
	}

	public ZeroAicySplitView(Context context, AttributeSet attributeSet) {
		this(context, attributeSet, 0);
	}

	public ZeroAicySplitView(Context context, AttributeSet attributeSet, int defStyleAttr) {
		super(context, attributeSet, defStyleAttr);
	}

	@Override
	public void setOnSplitChangeListener(OnSplitChangeListener onSplitChangeListener) {
		super.setOnSplitChangeListener(onSplitChangeListener);
		this.listener = onSplitChangeListener;
		
	}
	
	@Override
	public void openSplit(boolean isHorizontal, boolean animator) {
		if (this.onSplitInterceptListener == null 
			|| !this.onSplitInterceptListener.openSplit(isHorizontal, animator)) {
			// 返回true消耗事件
			super.openSplit(isHorizontal, animator);			
		}else{
			// OnSplitChangeListener
			if (this.listener != null) {
				this.listener.j6(this.isSplit());
			}
		}
	}

	@Override
	public void closeSplit(boolean animator, Runnable animatorListenerAdapterRunable) {
		if (this.onSplitInterceptListener == null 
			|| !this.onSplitInterceptListener.closeSplit(animator, animatorListenerAdapterRunable)) {
			// 返回true消耗事件
			super.closeSplit(animator, animatorListenerAdapterRunable);			
		}else{
			
			// OnSplitChangeListener
			if (this.listener != null) {
				this.listener.j6(this.isSplit());
			}	
		}
		
	}
	
	
}
