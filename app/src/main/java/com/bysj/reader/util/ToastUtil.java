package com.bysj.reader.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具
 * 
 * @author zhyling
 *
 */
public class ToastUtil {
	public static void show(Context context, int stringId) {
		Toast.makeText(context, context.getResources().getString(stringId), Toast.LENGTH_SHORT).show();
	}

	public static void show(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
}
