package com.bysj.reader;

import android.app.Application;

import com.bysj.reader.bean.Config;

/**
 * Created by zhyling.
 */
public class Myapplication extends Application {
    private static Config config = null;

    public Myapplication() {
    }

    /**
     * 获取软件配置对象
     *
     * @return
     */
    public Config getConfig() {
        if (config == null) {
            config = Config.getInstance(getApplicationContext());
        }
        return config;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("---------application start");
    }

}
