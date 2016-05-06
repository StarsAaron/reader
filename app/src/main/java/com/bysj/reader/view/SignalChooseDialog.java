package com.bysj.reader.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bysj.reader.R;

/**
 * 单选对话框
 */
public class SignalChooseDialog extends Dialog {
    private MyListener myListener = null;
    private Button btn1, btn2, btn3;

    public SignalChooseDialog(Context context) {
        super(context, R.style.dialog);
    }

    public SignalChooseDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_signal_choose);
        btn1 = (Button) findViewById(R.id.btn_order_name);
        btn2 = (Button) findViewById(R.id.btn_order_size);
        btn3 = (Button) findViewById(R.id.btn_order_time);

        //点击事件
        //文件名排序
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myListener != null) {
                    myListener.onClick("name");
                }
            }
        });

        //大小排序
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myListener != null) {
                    myListener.onClick("size");
                }
            }
        });

        //时间排序
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myListener != null) {
                    myListener.onClick("time");
                }
            }
        });
    }

    public void setOnClickListener(MyListener lis) {
        this.myListener = lis;
    }

    public interface MyListener {
        void onClick(String order);
    }
}
