package com.bysj.reader.bean;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;

import com.bysj.reader.R;

/**
 * 软件配置信息
 * Created by zhyling on 2016/3/5.
 */
public class Config {
    private static final String CONFIG_FILE_NAME = "config";
    private static final String CONFIG_BG_COLOR = "config_bg_color";
    private static final String CONFIG_LANGUAGE = "config_language";
    private static final String CONFIG_TEXT_SIZE = "config_textsize";
    private static final String CONFIG_TEXT_COLOR = "config_textcolor";
    private static final String CONFIG_BRIGHTNESS = "config_brightness";

    private int textSizeIndex = 0;//字体大小，默认30
    private int textColorIndex = 0;//字体颜色
    private int bgColorIndex = 0;//背景颜色
    private int softLanguageIndex = 0;//软件语言

    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;
    private Context context = null;
    private static Config config = null;

    private Config(Context context) {
        sharedPreferences = context.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static Config getInstance(final Context context) {
        if (config == null) {
            config = new Config(context);
        }
        return config;
    }

    public int getBgColor() {
        bgColorIndex = sharedPreferences.getInt(CONFIG_BG_COLOR, 0);
        return bgColorIndex;
    }

    public void setBgColor(int bgColor) {
        editor.putInt(CONFIG_BG_COLOR, bgColor);
        editor.commit();
        this.bgColorIndex = bgColor;
    }

    public int getSoftLanguage() {
        softLanguageIndex = sharedPreferences.getInt(CONFIG_LANGUAGE, 0);
        return softLanguageIndex;
    }

    public void setSoftLanguage(int softLanguage) {
        editor.putInt(CONFIG_LANGUAGE, softLanguage);
        editor.commit();
        this.softLanguageIndex = softLanguage;
    }

    public int getTextSize() {
        textSizeIndex = sharedPreferences.getInt(CONFIG_TEXT_SIZE, 3);
        return textSizeIndex;
    }

    public void setTextSize(int textSize) {
        editor.putInt(CONFIG_TEXT_SIZE, textSize);
        editor.commit();
        this.textSizeIndex = textSize;
    }

    public int getTextColor() {
        textColorIndex = sharedPreferences.getInt(CONFIG_TEXT_COLOR, 0);
        return textColorIndex;
    }

    public void setTextColor(int textColor) {
        editor.putInt(CONFIG_TEXT_COLOR, textColor);
        editor.commit();
        this.textColorIndex = textColor;
    }

    public float getBrightness() {
        return sharedPreferences.getFloat(CONFIG_BRIGHTNESS, 20);
    }

    public void setBrightness(float brightness) {
        editor.putFloat(CONFIG_BRIGHTNESS, brightness);
        editor.commit();
    }
}
