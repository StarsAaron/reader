package com.bysj.reader.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bysj.reader.R;

/**
 * 基类
 */
public class BaseActivity extends Activity {
    private RelativeLayout rl_title_bar,rl_container;
    private ImageView iv_headbar_back,iv_headbar_other,iv_headbar_search;
    private TextView tv_headbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        rl_container = (RelativeLayout)findViewById(R.id.rl_container);
        rl_title_bar = (RelativeLayout)findViewById(R.id.rl_title_bar);
        iv_headbar_back = (ImageView) findViewById(R.id.iv_headbar_back);
        iv_headbar_other = (ImageView)findViewById(R.id.iv_headbar_other);
        iv_headbar_search = (ImageView)findViewById(R.id.iv_headbar_search);
        tv_headbar_title = (TextView) findViewById(R.id.tv_headbar_title);
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, view.getLayoutParams());
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if(params == null){
            rl_container.addView(view);
        }else{
            rl_container.addView(view, params);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        View v = getLayoutInflater().inflate(layoutResID,rl_container,false);
        setContentView(v);
    }

    /**
     * 设置标题
     * @param mTitle
     */
    public void setTitle(String mTitle){
        tv_headbar_title.setText(mTitle);
    }

    /**
     * 设置标题是否显示
     * @param isShow
     */
    public void showTitleBar(boolean isShow){
        if(isShow){
            if(rl_title_bar.getVisibility() == View.GONE){
                AlphaAnimation in = new AlphaAnimation(0.5f,1.0f);
                in.setDuration(200);
                rl_title_bar.startAnimation(in);
                rl_title_bar.setVisibility(View.VISIBLE);
            }
        }else{
            if(rl_title_bar.getVisibility() == View.VISIBLE){
                AlphaAnimation out = new AlphaAnimation(1.0f,0.0f);
                out.setDuration(200);
                rl_title_bar.startAnimation(out);
                rl_title_bar.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 显示左按钮
     * @param mShow
     */
    public void showLeftButton(boolean mShow){
        if(mShow){
            iv_headbar_back.setVisibility(View.VISIBLE);
        }else{
            iv_headbar_back.setVisibility(View.GONE);
        }
    }

    /**
     * 设置左按钮图标
     * @param drawableId
     */
    public void showLeftButtonIcon(int drawableId){
        iv_headbar_back.setBackground(getResources().getDrawable(drawableId));
    }

    /**
     * 设置左按钮点击事件
     * @param listener
     */
    public void setLeftButtonOnClickListener(View.OnClickListener listener){
        iv_headbar_back.setOnClickListener(listener);
    }

    /**
     * 显示右按钮
     * @param mShow
     */
    public void showRightMenuButton(boolean mShow){
        if(mShow){
            iv_headbar_other.setVisibility(View.VISIBLE);
        }else{
            iv_headbar_other.setVisibility(View.GONE);
        }
    }

    /**
     * 设置右按钮图标
     * @param drawableId
     */
    public void showRightMenuButtonIcon(int drawableId){
        iv_headbar_other.setBackground(getResources().getDrawable(drawableId));
    }

    /**
     * 设置右按钮点击事件
     * @param listener
     */
    public void setRightMenuButtonOnClickListener(View.OnClickListener listener){
        iv_headbar_other.setOnClickListener(listener);
    }

    /**
     * 显示右按钮
     * @param mShow
     */
    public void showRightSearchButton(boolean mShow){
        if(mShow){
            iv_headbar_search.setVisibility(View.VISIBLE);
        }else{
            iv_headbar_search.setVisibility(View.GONE);
        }
    }

    /**
     * 设置右按钮图标
     * @param drawableId
     */
    public void showRightSearchButtonIcon(int drawableId){
        iv_headbar_search.setBackground(getResources().getDrawable(drawableId));
    }

    /**
     * 设置右按钮点击事件
     * @param listener
     */
    public void setRightSearchButtonOnClickListener(View.OnClickListener listener){
        iv_headbar_search.setOnClickListener(listener);
    }

    /**
     * Activity跳转
     * @param activityClass
     */
    public void startMyActivity(Class activityClass){
        Intent intent = new Intent(getApplicationContext(),activityClass);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
