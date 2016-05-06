package com.bysj.reader.ui;

import android.os.Bundle;
import android.view.View;

import com.bysj.reader.R;

/**
 * 网上书店
 */
public class WebLibraryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_library);
        //初始化标题
        setTitle(getResources().getString(R.string.weblibrary_title));
        showLeftButton(true);
        setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

    }
}
