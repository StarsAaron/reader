package com.bysj.reader.util;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class ToolUtil {

	public static boolean isMobile(String mobiles) { // 验证手机号码
		Pattern p = Pattern.compile("^[1]\\d{10}$");
		// Pattern p = Pattern
		// .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Pattern p1 = Pattern.compile("^((\\+86)|(86))?(13)\\d{9}$");

		Matcher m = p.matcher(mobiles);
		if (m.matches())
			return true;
		else {
			m = p1.matcher(mobiles);
			return m.matches();
		}
	}

	/**
	 * 电话号码验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isPhone(String str) {
		Pattern p1 = null, p2 = null;
		Matcher m = null;
		boolean b = false;
		p1 = Pattern.compile("^[0][1-9]{2,3}-?[0-9]{5,10}$"); // 验证带区号的
		p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$"); // 验证没有区号的
		if (str.length() > 9) {
			m = p1.matcher(str);
			b = m.matches();
		} else {
			m = p2.matcher(str);
			b = m.matches();
		}
		return b;
	}

	public static boolean isEmail(String email) { // 验证邮箱
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	public static String intToIp(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();

		return (ipAddress & 0xFF) + "." +

		((ipAddress >> 8) & 0xFF) + "." +

		((ipAddress >> 16) & 0xFF) + "." +

		(ipAddress >> 24 & 0xFF);
	}

	public static String getVersionName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			String version = packInfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getVersionCode(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 关闭输入法
	 * 
	 * @param context
	 */
	public static void closeInput(Activity context) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			if (context != null) {
				try {
					IBinder iBinder = context.getCurrentFocus().getWindowToken();
					if (iBinder != null)
						imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				} catch (Exception e) {
					Log.e("xing", "closeInput exception");
				}
			}
		}
	}

	/**
	 * 获取缓存目录
	 * 
	 * @param context
	 * @return
	 */
	public static File getDiskCacheDir(Context context) {
		File cachePath = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir();
		} else {
			cachePath = context.getCacheDir();
		}

		if (cachePath == null) {
			cachePath = context.getFilesDir();
			if (cachePath == null && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
					|| !Environment.isExternalStorageRemovable()) {
				cachePath = Environment.getExternalStorageDirectory();
			}
		}
		return cachePath;
	}

	public static SimpleDateFormat getFormat(String partten) {
		return new SimpleDateFormat(partten, Locale.getDefault());
	}

	public static String formatTime(String time) {
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(sdf.parse(time));
			return c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);

		return str;
	}

	public static String getDateDay() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);

		return str;
	}

	public static String getDateDayTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		return str;
	}

	public static String getFormatString(CharSequence text, Object... args) {
		return String.format(text.toString(), args);
	}

	public static boolean compareTime(String stime, String etime) {
		DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		try {
			Date d1 = dateformat.parse(stime);
			Date d2 = dateformat.parse(etime);

			return d1.after(d2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	final static float scale = Resources.getSystem().getDisplayMetrics().density;

	/**
	 * Converts DIP to pixels.
	 * 
	 * @param dip
	 *            the value of the dip.
	 * @return the value of the pixels.
	 */
	public static int dipToPixel(int dip) {
		return (int) (dip * scale + 0.5f);
	}

	public static int pixelToDip(int px) {
		return (int) ((px - 0.5f) / scale);
	}

	public static int getDensity(Activity context) {
		// l：m：h：xh：xxh=3：4：6：8：12 所有L是120
		float density = context.getResources().getDisplayMetrics().density; // 屏幕密度(0.75-1.0-1.5)

		int densityDpi = context.getResources().getDisplayMetrics().densityDpi; // 屏幕密度DPI(120-160-240)
		System.out.println("density=" + density + "- densityDpi=" + densityDpi);
		return densityDpi;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		BitmapDrawable bd = (BitmapDrawable) drawable;
		Bitmap bitmap = bd.getBitmap();
		return bitmap;
	}

	public static Drawable BitmapToDrawable(Context c, final Bitmap bitmap) {
		BitmapDrawable bd = new BitmapDrawable(c.getResources(), bitmap);
		return bd;
	}

	/**
	 * 检测Sdcard是否存在
	 * 
	 * @return
	 */
	public static boolean isExitsSdcard() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	public static ComponentName getTopAppInfo(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		RunningTaskInfo currentRun = activityManager.getRunningTasks(1).get(0);
		ComponentName nowApp = currentRun.topActivity;
		return nowApp;
	}

	// 清楚缓存
	public static void clearCookies(Context context) {
		CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		cookieSyncMngr.sync();
		cookieSyncMngr.startSync();
	}

	// clear the cache before time numDays
	public static int clearCacheFolder(File dir, long numDays) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, numDays);
					}
					if (child.lastModified() < numDays) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}

	/**
	 * 同步一下cookie
	 */
	public static void synCookies(Context context, String url, String cookies) {
		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		cookieManager.removeSessionCookie();// 移除
		cookieManager.setCookie(url, cookies);// 指定要修改的cookies
		CookieSyncManager.getInstance().sync();
	}

	public static boolean isOpenNetwork(Context c) {
		ConnectivityManager connManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}

		return false;
	}

	/**
	 * 获取机器ID
	 * 
	 * @param context
	 * @return
	 */
	public static String getDevId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String devid = telephonyManager.getDeviceId();
		if (!TextUtils.isEmpty(devid))
			return devid;

		devid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		if (!TextUtils.isEmpty(devid))
			return devid;
		return null;
	}

	/**
	 * 将byte转换为一个长度为8的byte数组，数组每个值代表bit
	 */
	public static byte[] getBitArray(byte b) {
		byte[] array = new byte[8];
		for (int i = 0; i < 8; i++) {
			array[i] = (byte) (b & 1);
			b = (byte) (b >> 1);
		}
		return array;
	}
}
