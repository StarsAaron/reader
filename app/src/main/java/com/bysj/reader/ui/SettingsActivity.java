package com.bysj.reader.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bysj.reader.Myapplication;
import com.bysj.reader.R;
import com.bysj.reader.adapter.SpinnerImgAdapter;
import com.bysj.reader.adapter.SpinnerTextAdapter;
import com.bysj.reader.bean.Config;
import com.bysj.reader.util.ToolUtil;
import com.bysj.reader.view.AboutDialog;


/**
 * 软件设置
 */
public class SettingsActivity extends BaseActivity {
    private Spinner sp_settings_text_size,sp_settings_text_color,sp_settings_bg_color,sp_settings_language;
    private TextView tv_settings_about;
    private AboutDialog aboutDialog = null;
    private Config config = null;
    private Myapplication myapplication = null;

//    private static final String[] textSizedArray = {"16", "18", "20", "30"};
//    private static final String[] textColorArray = {"#414141", "#949393", "#FFFFFF"};
//    private static final String[] bgColorArray = {"#fdf7d5", "#668564", "#997c56"};
//    private static final String[] softLanguageArray = {"中文", "English"};

    private static String[] textSizedArray = null;
    private static String[] textColorArray = null;
    private static String[] bgColorArray = null;
    private static String[] softLanguageArray = null;

    private int textSizeIndex = 0;
    private int textColorIndex = 0;
    private int bgColorIndex = 0;
    private int languageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        myapplication = (Myapplication)getApplication();
        config = myapplication.getConfig();

        textSizedArray = getResources().getStringArray(R.array.textsize);
        textColorArray = getResources().getStringArray(R.array.textcolor);
        bgColorArray = getResources().getStringArray(R.array.bgcolor);
        softLanguageArray = getResources().getStringArray(R.array.language);

        registView();
        initView();
    }

    private void registView() {
        sp_settings_text_size = (Spinner)findViewById(R.id.sp_settings_text_size);
        sp_settings_text_color = (Spinner)findViewById(R.id.sp_settings_text_color);
        sp_settings_bg_color = (Spinner)findViewById(R.id.sp_settings_bg_color);
        sp_settings_language = (Spinner)findViewById(R.id.sp_settings_language);
        tv_settings_about = (TextView)findViewById(R.id.tv_settings_about);
    }

    private void initView() {
        //初始化标题
        setTitle(getResources().getString(R.string.setting_title));
        showLeftButton(true);
        setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        tv_settings_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutDialog aboutDialog = new AboutDialog(SettingsActivity.this);
                aboutDialog.show();
                aboutDialog.setAboutTv("作者：赵汝健\n时间：2016.3\n版本：V" + ToolUtil.getVersionName(SettingsActivity.this));
            }
        });

        //初始化显示设置
        textSizeIndex = config.getTextSize();
        textColorIndex = config.getTextColor();
        bgColorIndex = config.getBgColor();
        languageIndex = config.getSoftLanguage();

        //创建适配器----字体大小
        SpinnerTextAdapter spinnerTextAdapter = new SpinnerTextAdapter(getApplicationContext(),textSizedArray);
        sp_settings_text_size.setAdapter(spinnerTextAdapter);
        sp_settings_text_size.setSelection(textSizeIndex); //设置默认选项
        sp_settings_text_size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                config.setTextSize(position);//注意这里的position是从1开始的，我们保存的是数组的下标
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //创建适配器----字体颜色
        SpinnerImgAdapter spinnerImgAdapter = new SpinnerImgAdapter(getApplicationContext(),textColorArray);
        sp_settings_text_color.setAdapter(spinnerImgAdapter);
        sp_settings_text_color.setSelection(textColorIndex); //设置默认选项
        sp_settings_text_color.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                config.setTextColor(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //创建适配器----背景颜色
        SpinnerImgAdapter spinnerImgAdapter2 = new SpinnerImgAdapter(getApplicationContext(),bgColorArray);
        sp_settings_bg_color.setAdapter(spinnerImgAdapter2);
        sp_settings_bg_color.setSelection(bgColorIndex); //设置默认选项
        sp_settings_bg_color.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                config.setBgColor(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //创建适配器----语言
        SpinnerTextAdapter spinnerTextAdapter2 = new SpinnerTextAdapter(getApplicationContext(),softLanguageArray);
        sp_settings_language.setAdapter(spinnerTextAdapter2);
        sp_settings_language.setSelection(languageIndex); //设置默认选项
        sp_settings_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                config.setSoftLanguage(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

}
