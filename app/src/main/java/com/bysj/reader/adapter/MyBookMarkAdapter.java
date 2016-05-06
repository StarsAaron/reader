package com.bysj.reader.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bysj.reader.R;
import com.bysj.reader.bean.BookInfo;
import com.bysj.reader.bean.BookmarkBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhyling on 2016/3/3.
 */
public class MyBookMarkAdapter extends BaseAdapter{
    private List<BookmarkBean> mBooks = new ArrayList<BookmarkBean>();
    private Context context = null;

    public MyBookMarkAdapter(Context context,List<BookmarkBean> mBookInfos) {
        this.context = context;
        this.mBooks = mBookInfos;
    }

    @Override
    public int getCount() {
        return mBooks.size();
    }

    @Override
    public Object getItem(int position) {
        return mBooks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        MyViewHolder myViewHolder = null;
        BookmarkBean bookmarkBean = mBooks.get(position);
        if(convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_bookmark,null);
            myViewHolder = new MyViewHolder(view);
            view.setTag(myViewHolder);
        }else{
            view = convertView;
            myViewHolder = (MyViewHolder)convertView.getTag();
        }
        myViewHolder.tvTitle.setText(bookmarkBean.getBookmark_bookname());
        String desc = "描述: " + bookmarkBean.getBook_page_deac();
        myViewHolder.tvDesc.setText(desc);
//        File file = new File();
//        if(file.exists()){
//            myViewHolder.ivBook.setImageBitmap(BitmapFactory.decodeFile(bookInfo.getBook_icon()));
//        }else{
//            myViewHolder.ivBook.setBackground(context.getResources().getDrawable(R.mipmap.book1));
//        }
        return view;
    }

    public static class MyViewHolder {
//        public ImageView ivBook;
        public TextView tvTitle;
        public TextView tvDesc;

        public MyViewHolder(View itemView) {
//            ivBook = (ImageView) itemView.findViewById(R.id.ivBook);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDesc = (TextView) itemView.findViewById(R.id.tvDesc);
        }
    }
}
