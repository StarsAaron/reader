package com.bysj.reader.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bysj.reader.R;

/**
 * 关于对话框
 */
public class AboutDialog extends Dialog {
    private MyOnClickListener myOnClickListener = null;
    private LinearLayout ll_btn_bar;

	public AboutDialog(Context context) {
        super(context, R.style.dialog);
	}

    public AboutDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    private TextView aboutTv,tv_dialog_title;
    private Button btn_ok,btn_cancel;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_about);
		
		aboutTv = (TextView) findViewById(R.id.tv_dialog_msg);
        tv_dialog_title = (TextView) findViewById(R.id.tv_dialog_title);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        ll_btn_bar = (LinearLayout)findViewById(R.id.ll_btn_bar);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myOnClickListener != null) {
                    myOnClickListener.onClick(1);
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myOnClickListener != null) {
                    myOnClickListener.onClick(2);
                }
            }
        });

	}

    public void setOnClickListener(MyOnClickListener lis) {
        ll_btn_bar.setVisibility(View.VISIBLE);
        this.myOnClickListener = lis;
    }

    public void setTitle(String str){
        tv_dialog_title.setText(str);
    }

	public void setAboutTv(String str){
		aboutTv.setText(str);
	}

    public interface MyOnClickListener{
        void onClick(int id);
    }
}
