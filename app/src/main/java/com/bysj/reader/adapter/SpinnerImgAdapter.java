package com.bysj.reader.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bysj.reader.R;

/**
 * Created by zhyling on 2016/3/7.
 */
public class SpinnerImgAdapter extends BaseAdapter{
    private String[] mArray = null;
    private Context context = null;

    public SpinnerImgAdapter(Context context, String[] mArray) {
        this.context = context;
        this.mArray = mArray;
    }

    @Override
    public int getCount() {
        return mArray.length;
    }

    @Override
    public Object getItem(int position) {
        return mArray[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_spinner_img,null);
        if(convertView != null){
            ImageView img = (ImageView)convertView.findViewById(R.id.iv_name);
            img.setBackgroundColor(Color.parseColor(mArray[position]));
        }
        return convertView;
    }
}
