package com.bysj.reader.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

public class MyPageWidget extends View{
	private Bitmap mCurBitMap;
	public MyPageWidget(Context context) {
		super(context);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		canvas.drawBitmap(mCurBitMap, 0, 0, null);
		canvas.restore();
	}

    public void setDrawBitMap(Bitmap bitmap ){
		mCurBitMap = bitmap;
	}
}
