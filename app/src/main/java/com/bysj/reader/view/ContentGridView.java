package com.bysj.reader.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.GridView;

import com.bysj.reader.R;

public class ContentGridView extends GridView {
	private Bitmap background;

	public boolean haveScrollbar = false;

	public ContentGridView(Context context) {
		super(context);
	}

	public ContentGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void init() {
	}

	public ContentGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setHaveScrollbar(boolean haveScrollbar) {
		this.haveScrollbar = haveScrollbar;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		int count = getChildCount();
		int top = count > 0 ? getChildAt(0).getTop() : 0;

		int width = getWidth();
		int height = getHeight();

		int totalCount = this.getCount();

		//如果总数为0自然没必要计算高度并画图
		if (totalCount > 0) {
			
			//获取列数
			int numColumns=3;
			//此函数必须要api 11以上才能使用，建议有需要动态列数的可以通过自定义属性来实现。
//			int numColumns=this.getNumColumns();
			// 计算行高，通过行数和控件高度计算
			int rowCount = totalCount / numColumns;
			if (totalCount % numColumns != 0) {
				rowCount++;
			}
			int rowHeight = height / rowCount;


			if (background == null) {
				background = BitmapFactory.decodeResource(getResources(),
						R.mipmap.ic_book_bg);
				background = Bitmap.createScaledBitmap(background, width,
						rowHeight, true);
			}

			int backgroundWidth = background.getWidth();
			int backgroundHeight = background.getHeight();

			for (int y = top; y < height; y += backgroundHeight) {
				for (int x = 0; x < width; x += backgroundWidth) {
					canvas.drawBitmap(background, x, y, null);
				}
			}
		}

		super.dispatchDraw(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (haveScrollbar == false) {
			int expandSpec = MeasureSpec.makeMeasureSpec(
					Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

}
