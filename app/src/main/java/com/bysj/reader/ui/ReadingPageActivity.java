package com.bysj.reader.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bysj.reader.Myapplication;
import com.bysj.reader.R;
import com.bysj.reader.bean.BookInfo;
import com.bysj.reader.bean.BookmarkBean;
import com.bysj.reader.bean.Config;
import com.bysj.reader.db.BookInfoDao;
import com.bysj.reader.util.MyBookPageFactory;
import com.bysj.reader.util.ToastUtil;
import com.bysj.reader.view.MyPageWidget;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 读书界面
 */
public class ReadingPageActivity extends Activity implements View.OnClickListener {
    private Myapplication myapplication = null;
    private RelativeLayout rl_container = null;
    private RelativeLayout rl_title_bar = null;
    private LinearLayout ll_foot_bar = null;
    private ImageView iv_reading_back;
    private TextView tv_reading_title;
    private ImageView iv_bar_screenorient, iv_bar_scrollstart, iv_bar_light, iv_bar_menu, iv_bar_bookmark, iv_bar_night;

    private Config config = null;//配置文件对象
    private MyPageWidget mPageWidget;
    private Bitmap mCurrentPageBitmap, mNextPageBitmap;
    private Canvas mCurrentPageCanvas, mNextPageCanvas;
    private DisplayMetrics dm;
    private MyBookPageFactory pagefactory;
    private BookInfo bookInfo = null;//选择的书本信息
    private int[] position = null;//读书位置（页字符开始，结束位置）
    private int fontsize = 30; //字体大小
    private String fontColor = "";//字体颜色
    private String bgColor = "";//背景颜色
    private boolean isAutoNextPage = false;//是否自动滚屏
    private Timer timer = null;
    private Timer timer2 = null;
    private float brightness = 255;//亮度
    private int tempBrightness = 0;//系统亮度
    private int dayAndNight = 0;//当前日夜模式，默认白天
    private boolean isFromBookmark = false;//是否从书签跳转过来
    private String positionStr = "";//书签位置
    private PopupWindow popupWindow = null;
    private int[] location = null;//脚栏坐标位置

    private BookInfoDao bookInfoDao = null;

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x111:
                    actionBar();
                    break;
                case 0x222:
                    if (isAutoNextPage) {
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!pagefactory.isTheEnd()) {
                                    pagefactory.nextPage();
                                    position = pagefactory.getPosition();
                                    pagefactory.onDrow(mCurrentPageCanvas);
                                    mPageWidget.setDrawBitMap(mCurrentPageBitmap);
                                    mPageWidget.invalidate();//刷新界面
                                    myHandler.sendEmptyMessage(0x222);
                                } else {
                                    ToastUtil.show(ReadingPageActivity.this, "到文件尾了！");
                                }
                            }
                        }, 3000);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        myapplication = (Myapplication) getApplication();
        config = myapplication.getConfig();//获取配置对象
        bookInfoDao = new BookInfoDao(ReadingPageActivity.this);

        //获取传递过来的书本信息
        Bundle bundle = this.getIntent().getExtras();
        if (this.getIntent().getFlags() == 0x132) {//从书架或首页跳转过来
            isFromBookmark = false;
            bookInfo = (BookInfo) bundle.getSerializable("slect_book");
        } else if (this.getIntent().getFlags() == 0x133) {//从书签跳转过来
            isFromBookmark = true;
            int tt = bundle.getInt("slect_book_id");
            bookInfo = bookInfoDao.getBookFromId(tt);
            positionStr = bundle.getString("slect_book_position");
        }

        if (bookInfo == null) {
            ToastUtil.show(ReadingPageActivity.this, "获取书籍信息出错！");
        } else {
            //获取刷新activity之前的配置
            if (savedInstanceState != null) {
                fontColor = savedInstanceState.getString("fontColor");
                bgColor = savedInstanceState.getString("bgColor");
                position = savedInstanceState.getIntArray("position");
            }
            registView();
            initView();//初始化
        }
    }

    private void registView() {
        rl_container = (RelativeLayout) findViewById(R.id.rl_container);
        rl_title_bar = (RelativeLayout) findViewById(R.id.rl_title_bar);
        ll_foot_bar = (LinearLayout) findViewById(R.id.ll_foot_bar);
        iv_reading_back = (ImageView) findViewById(R.id.iv_reading_back);
        tv_reading_title = (TextView) findViewById(R.id.tv_reading_title);

        iv_bar_screenorient = (ImageView) findViewById(R.id.iv_bar_screenorient);
        iv_bar_scrollstart = (ImageView) findViewById(R.id.iv_bar_scrollstart);
        iv_bar_light = (ImageView) findViewById(R.id.iv_bar_light);
        iv_bar_menu = (ImageView) findViewById(R.id.iv_bar_menu);
        iv_bar_bookmark = (ImageView) findViewById(R.id.iv_bar_bookmark);
        iv_bar_night = (ImageView) findViewById(R.id.iv_bar_night);

        iv_bar_screenorient.setOnClickListener(this);
        iv_bar_scrollstart.setOnClickListener(this);
        iv_bar_light.setOnClickListener(this);
        iv_bar_menu.setOnClickListener(this);
        iv_bar_bookmark.setOnClickListener(this);
        iv_bar_night.setOnClickListener(this);
    }

    //初始化
    private void initView() {
        //设置亮度
        saveSysBrightness();
        brightness = config.getBrightness();
        setBrightness(brightness);
        //设置标题
        String title = bookInfo.getBook_name();
        tv_reading_title.setText(title.substring(0, title.lastIndexOf(".")));

        iv_reading_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        //页面内容
        mPageWidget = new MyPageWidget(this);
        rl_container.addView(mPageWidget);

        String[] textSizedArray = getResources().getStringArray(R.array.textsize);
        String[] textColorArray = getResources().getStringArray(R.array.textcolor);
        String[] bgColorArray = getResources().getStringArray(R.array.bgcolor);
        //DisplayMetrics获取显示宽高信息
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mCurrentPageBitmap = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Bitmap.Config.ARGB_8888);
        mCurrentPageCanvas = new Canvas(mCurrentPageBitmap);

        if (TextUtils.isEmpty(fontColor) || TextUtils.isEmpty(bgColor) || position == null) {
            position = new int[2];
            if (isFromBookmark) { //位置来自书签
                String[] pp = positionStr.split(",");
                position[0] = Integer.parseInt(pp[0]);
                position[1] = Integer.parseInt(pp[1]);
            } else { //位置来自书
                //获取读书位置
                String[] record = bookInfo.getRecordPosition().split(",");
                position[0] = Integer.parseInt(record[0]);
                position[1] = Integer.parseInt(record[1]);
            }
            //字体大小
            fontsize = Integer.parseInt(textSizedArray[config.getTextSize()]);
            bgColor = bgColorArray[config.getBgColor()];
            //字体颜色
            fontColor = textColorArray[config.getTextColor()];
            setDayAndNight(fontColor, bgColor, position);
        } else {
            setDayAndNight(fontColor, bgColor, position);
        }
        //设置点击控件
        mPageWidget.setDrawBitMap(mCurrentPageBitmap);
        mPageWidget.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v == mPageWidget) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (event.getY() > dm.heightPixels * 2 / 3) {
                            if (event.getX() > dm.widthPixels / 2) {
                                pagefactory.nextPage();
                                position = pagefactory.getPosition();
                                pagefactory.onDrow(mCurrentPageCanvas);
                                mPageWidget.setDrawBitMap(mCurrentPageBitmap);
                            } else {
                                pagefactory.prePage();
                                position = pagefactory.getPosition();
                                pagefactory.onDrow(mCurrentPageCanvas);
                                mPageWidget.setDrawBitMap(mCurrentPageBitmap);
                            }
                            mPageWidget.invalidate();//刷新界面
                        } else {
                            actionBar();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        mPageWidget.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                //长按操作
                return true;
            }
        });

        timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                myHandler.sendEmptyMessage(0x111);//显示标题3秒
            }
        }, 3000);
    }

    /**
     * 标题栏和脚栏
     */
    private void actionBar() {
        if (timer2 != null) { //取消第一次显示标题的计时器
            timer2.cancel();
            timer2.purge();
            timer2 = null;
        }
        if (rl_title_bar.getVisibility() == View.VISIBLE) {
            if (timer != null) {
                timer.cancel();
                timer.purge();
            }
            AlphaAnimation bb = new AlphaAnimation(1.0f, 0.0f);
            bb.setDuration(700);
            rl_title_bar.startAnimation(bb);
            ll_foot_bar.startAnimation(bb);
            rl_title_bar.setVisibility(View.GONE);
            ll_foot_bar.setVisibility(View.GONE);
        } else {
            AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
            aa.setDuration(700);
            rl_title_bar.startAnimation(aa);
            ll_foot_bar.startAnimation(aa);
            rl_title_bar.setVisibility(View.VISIBLE);
            ll_foot_bar.setVisibility(View.VISIBLE);
            //定时器标题栏消失
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    myHandler.sendEmptyMessage(0x111);
                }
            }, 3000);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //保存旋屏时的阅读参数
        outState.putString("fontColor", fontColor);
        outState.putString("bgColor", bgColor);
        outState.putIntArray("position", position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_bar_screenorient://屏幕旋转
                //取消滚屏
                isAutoNextPage = false;
                myHandler.removeMessages(0x222);
                //旋转屏幕
                if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Log.i("info", "landscape");
                    //竖屏设置
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    Log.i("info", "portrait");
                    //横屏设置
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                break;
            case R.id.iv_bar_scrollstart://滚动
                if (isAutoNextPage) {
                    isAutoNextPage = false;
                    ToastUtil.show(ReadingPageActivity.this, "结束自动翻页！");
                } else {
                    isAutoNextPage = true;
                    ToastUtil.show(ReadingPageActivity.this, "开启自动翻页！");
                    myHandler.sendEmptyMessage(0x222);
                }
                break;
            case R.id.iv_bar_light://亮度
                if (brightness == 40) {
                    brightness = 127;
                    setBrightness(brightness);
                } else {
                    if (brightness == 127) {
                        brightness = 255;
                        setBrightness(brightness);
                    } else {
                        brightness = 40;
                        setBrightness(brightness);
                    }
                }
                break;
            case R.id.iv_bar_menu://目录
                if(popupWindow!= null&&popupWindow.isShowing()){
                    popupWindow.dismiss();
                    popupWindow = null;
                }
                View view = getLayoutInflater().inflate(R.layout.progressbar, null);
                popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
                popupWindow.setOutsideTouchable(true);
                SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
                TextView process = (TextView) view.findViewById(R.id.tv_precent);
                seekBar.setMax(100);
                int pr = (int) (pagefactory.getProgress());
                seekBar.setProgress(pr);
                process.setText(String.valueOf(pr) + "%");
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (seekBar.getProgress() > 95) {

                        } else {
                            int pp = (int) (seekBar.getProgress() * pagefactory.getMaxSize() / 100);
                            int[] position = new int[2];
                            position[0] = pp;
                            position[1] = pp;
                            setDayAndNight(fontColor, bgColor, position);
                        }
                        popupWindow.dismiss();
                        popupWindow = null;
                    }
                });
                popupWindow.setAnimationStyle(R.style.contextMenuAnim);
                location = new int[2];
                ll_foot_bar.getLocationOnScreen(location);
                popupWindow.showAtLocation(ll_foot_bar, Gravity.NO_GRAVITY, location[0], location[1] - 58);
                break;
            case R.id.iv_bar_night://日夜模式
                if (dayAndNight == 0) { //白天模式
                    dayAndNight = 1;
                    setDayAndNight("#414141", "#668564", pagefactory.getPosition());
                } else { //夜晚模式
                    dayAndNight = 0;
                    setDayAndNight("#414141", "#fdf7d5", pagefactory.getPosition());
                }
                break;
            case R.id.iv_bar_bookmark://书签
                //插入书签
                int[] position = pagefactory.getPosition();
                BookmarkBean bookmarkBean = new BookmarkBean();
                bookmarkBean.setBookmark_bookname(bookInfo.getBook_name());
                bookmarkBean.setBook_id(bookInfo.getBook_id());
                if (position.length >= 2) {
                    bookmarkBean.setBook_position(position[0] + "," + position[1]);
                }
                bookmarkBean.setBook_page_deac(pagefactory.getPresentDesc());
                if (bookInfoDao.addBookmark(bookmarkBean)) {
                    ToastUtil.show(ReadingPageActivity.this, "添加书签成功！");
                } else {
                    ToastUtil.show(ReadingPageActivity.this, "添加书签失败！");
                }
                break;
        }
    }

    /**
     * 设置白天或黑夜模式
     *
     * @param fc   字体颜色
     * @param bgc  背景颜色
     * @param psit 阅读位置
     */
    private void setDayAndNight(String fc, String bgc, int[] psit) {
        fontColor = fc;
        bgColor = bgc;
        pagefactory = new MyBookPageFactory(dm.widthPixels, dm.heightPixels, fontsize, fc);
        //设置背景图片
        pagefactory.setBgColor(bgc);
        try {
            pagefactory.openBook(bookInfo.getBook_path(), psit);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pagefactory.onDrow(mCurrentPageCanvas);
    }

    /**
     * 保存系统亮度配置
     */
    private void saveSysBrightness() {
        if (isAutoBrightness(getContentResolver())) {
            tempBrightness = -1;
        } else {
            tempBrightness = getScreenBrightness();
        }
    }

    /**
     * 恢复系统亮度配置
     */
    private void restoreSysBrightness() {
        if (tempBrightness == -1) {
            startAutoBrightness();
        } else {
            setBrightness(tempBrightness);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        restoreSysBrightness();//恢复系统亮度设置
        //保存当前书籍记录
        if (bookInfo != null) {
            int[] dd = pagefactory.getPosition();
            StringBuffer stringBuffer = new StringBuffer();
            if (dd.length >= 2) {
                stringBuffer.append(dd[0] + "," + dd[1]);
                bookInfo.setRecordPosition(stringBuffer.toString());
                bookInfo.setLastReadTime(new Date(System.currentTimeMillis()).getTime());
                bookInfoDao.chgBookInfo(bookInfo);//更新数据库
            }
        }
    }

    /**
     * 设置亮度图标
     *
     * @param dd
     */
    private void setBrightnessIcon(float dd) {
        if (dd == 255) {
            iv_bar_light.setBackground(getResources().getDrawable(R.mipmap.bar_light3));
        } else if (dd < 255 && dd >= 127) {
            iv_bar_light.setBackground(getResources().getDrawable(R.mipmap.bar_light2));
        } else {
            iv_bar_light.setBackground(getResources().getDrawable(R.mipmap.bar_light));
        }
    }

    /**
     * 设置亮度
     *
     * @param bh
     */
    private void setBrightness(float bh) {
        if (isAutoBrightness(getContentResolver())) {
            stopAutoBrightness();
        }
        //设置当前activity的屏幕亮度
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        //0到1,调整亮度暗到全亮
        lp.screenBrightness = Float.valueOf(bh / 255f);
        this.getWindow().setAttributes(lp);

//        //保存为系统亮度方法1
//        android.provider.Settings.System.putInt(getContentResolver(),
//                android.provider.Settings.System.SCREEN_BRIGHTNESS,
//                (int)bh);

        //保存为系统亮度方法2
        Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");
        android.provider.Settings.System.putInt(getContentResolver(), "screen_brightness", (int) bh);
        // resolver.registerContentObserver(uri, true, myContentObserver);
        getContentResolver().notifyChange(uri, null);
        config.setBrightness(bh);
        setBrightnessIcon(bh);
    }

    /**
     * 检查是否自动调节亮度
     */
    public boolean isAutoBrightness(ContentResolver aContentResolver) {
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(aContentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }

    /**
     * 获取当前屏幕亮度
     */
    public int getScreenBrightness() {
        int nowBrightnessValue = 0;
        ContentResolver resolver = getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

//    /**
//     * 保存到系统亮度
//     **/
//    private void setBrightness() {
//        try {
//            IPowerManager power = IPowerManager.Stub.asInterface(
//                    ServiceManager.getService("power"));
//            if (power != null) {
//                power.setBacklightBrightness(changeLight);
//            }
//        } catch (RemoteException doe) {
//
//
//        }
//    }

    /**
     * 设置停止自动亮度
     */
    public void stopAutoBrightness() {
        Settings.System.putInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * 开启自动亮度
     */
    public void startAutoBrightness() {
        Settings.System.putInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

}
