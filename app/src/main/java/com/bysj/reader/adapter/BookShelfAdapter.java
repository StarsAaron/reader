package com.bysj.reader.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bysj.reader.R;
import com.bysj.reader.bean.BookInfo;
import com.bysj.reader.util.ToolUtil;

public class BookShelfAdapter extends BaseAdapter {
	private Context mContext;
	private List<BookInfo> bookList;
	private OnNewsItem clickListener;

	public BookShelfAdapter(Context mContext, List<BookInfo> bookList,
                            OnNewsItem clickListener) {
		this.mContext = mContext;
		this.bookList = bookList;
		this.clickListener = clickListener;
	}

	@Override
	public int getCount() {
		return bookList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return bookList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		final BookInfo bookInfo = (BookInfo) bookList.get(arg0);

        View view = null;
        ViewHolder viewHolder = null;
        if(convertView == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_book,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)convertView.getTag();
        }

        //设置图片
        String icon_path = bookInfo.getBook_icon();
        if(TextUtils.isEmpty(icon_path)){
            Log.d("load_icon","-----------无图标");
            viewHolder.book_icon.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.book5));
        }else{
            File file = new File(icon_path);
            if(file.exists()){
                Bitmap imageBitmap = BitmapFactory.decodeFile(icon_path);//(path 是图片的路径，跟目录是/sdcard)
                viewHolder.book_icon.setImageBitmap(imageBitmap);
            }else{
                Log.d("load_icon","-----------无图标");
                viewHolder.book_icon.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.book5));
            }
        }
        //设置书名
        viewHolder.book_name.setText(bookInfo.getBook_name());
        //设置当前读书进度
        String record = bookInfo.getRecordPosition();
        String[] recordArray = record.split(",");

        int length = (int) (new File(bookInfo.getBook_path()).length());
        String[] posit = bookInfo.getRecordPosition().split(",");
        int pp = Integer.parseInt(posit[0]);
        if(pp != 0){
            long prec = pp*100/length;
            viewHolder.book_prec.setText(prec + "%");
        }else{
            viewHolder.book_prec.setText("0%");
        }
        viewHolder.btn_book_cover.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.oNitemClick(bookInfo);
            }
        });
		return view;
	}

    public static class ViewHolder{
        public ImageView book_icon;//图标路径
        public TextView book_name;//书名
        public TextView book_prec;//当前进度
        public Button btn_book_cover;//覆盖层，点击用

        public ViewHolder(View view){
            book_icon = (ImageView)view.findViewById(R.id.iv_book_icon);
            book_name = (TextView)view.findViewById(R.id.tv_book_name);
            book_prec = (TextView)view.findViewById(R.id.tv_process);
            btn_book_cover = (Button)view.findViewById(R.id.btn_book_cover);
        }
    }

	public interface OnNewsItem {
		void oNitemClick(BookInfo bookInfo);
	}
}
