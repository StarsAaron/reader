package com.bysj.reader.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ContentScrollView extends ScrollView {

	private OnScrollListener onScrollListener;
 
	private int lastScrollY;

	public ContentScrollView(Context context) {
		super(context);
	}

	public ContentScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ContentScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			int scrollY = ContentScrollView.this.getScrollY();

			if (lastScrollY != scrollY) {
				lastScrollY = scrollY;
				handler.sendMessageDelayed(handler.obtainMessage(), 1);
				int allHeight = ContentScrollView.this.getChildAt(0)
						.getMeasuredHeight();
				int height = ContentScrollView.this.getHeight();
				if (lastScrollY == (allHeight - height)) {
					oL.OnBottomClickListener();
				}
			}
			if (onScrollListener != null) {
				onScrollListener.onScroll(scrollY);
			}

		};

	};

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (onScrollListener != null) {
			onScrollListener.onScroll(lastScrollY = this.getScrollY());
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_UP:
			handler.sendMessageDelayed(handler.obtainMessage(), 1);
			break;
		}
		return super.onTouchEvent(ev);
	}

	public interface OnScrollListener {
        void onScroll(int scrollY);
	}

	private OnBottomClickListener oL;

	public void OnBottomClickListener(OnBottomClickListener l) {
		oL = l;
	}

	public interface OnBottomClickListener {
		void OnBottomClickListener();
	}

}
