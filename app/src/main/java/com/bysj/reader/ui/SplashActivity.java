package com.bysj.reader.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.bysj.reader.R;

/**
 * 过度页面
 */
public class SplashActivity extends Activity {
    private ImageView iv_splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        iv_splash = (ImageView)findViewById(R.id.iv_splash);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f,1.0f);
        alphaAnimation.setDuration(1500);
        iv_splash.startAnimation(alphaAnimation);
        new Thread(){
            public void run() {
                SystemClock.sleep(1500); //1.5S后跳转到首页
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }.start();
    }
}
