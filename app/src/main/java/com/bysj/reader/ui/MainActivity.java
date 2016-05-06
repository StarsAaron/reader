package com.bysj.reader.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bysj.reader.R;
import com.bysj.reader.bean.BookInfo;
import com.bysj.reader.db.BookInfoDao;

import java.io.File;
import java.util.List;

/**
 * 主页面
 */
public class MainActivity extends BaseActivity implements View.OnClickListener{
    private ImageView iv_library,iv_bookmark,iv_setting,iv_weblibrary;
    private BookInfoDao bookInfoDao = null;
    private LinearLayout ll_rec_book = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registView();
        initView();
    }

    private void registView() {
        iv_weblibrary = (ImageView)findViewById(R.id.iv_weblibrary);
        iv_library = (ImageView)findViewById(R.id.iv_library);
        iv_bookmark = (ImageView)findViewById(R.id.iv_bookmark);
        iv_setting = (ImageView)findViewById(R.id.iv_setting);
        ll_rec_book = (LinearLayout)findViewById(R.id.ll_rec_book);
    }

    private void initView() {
        //初始化标题
        setTitle(getResources().getString(R.string.main_title));

        iv_library.setOnClickListener(this);
        iv_bookmark.setOnClickListener(this);
        iv_setting.setOnClickListener(this);
        iv_weblibrary.setOnClickListener(this);

        bookInfoDao = new BookInfoDao(MainActivity.this);
        loadRecData();
    }

    /**
     * 获取最近阅读记录
     */
    private void loadRecData(){
        List<BookInfo> list = bookInfoDao.getRecBookList(5);//获取前5条最近阅读记录
        if(list != null || !list.isEmpty()){
            for(final BookInfo bb:list){
                View view = getLayoutInflater().inflate(R.layout.item_book,null);
                ImageView icon = (ImageView)view.findViewById(R.id.iv_book_icon);
                TextView title = (TextView)view.findViewById(R.id.tv_book_name);
                TextView process = (TextView)view.findViewById(R.id.tv_process);
                Button cover = (Button)view.findViewById(R.id.btn_book_cover);

                //设置图片
                String icon_path = bb.getBook_icon();
                if(TextUtils.isEmpty(icon_path)){
                    Log.d("load_icon", "-----------无图标");
                    int temp = bb.getBook_id()%4;
                    icon.setImageDrawable(getResources().getDrawable(R.mipmap.book5));
                }else{
                    File file = new File(icon_path);
                    if(file.exists()){
                        Bitmap imageBitmap = BitmapFactory.decodeFile(icon_path);//(path 是图片的路径，跟目录是/sdcard)
                        icon.setImageBitmap(imageBitmap);
                    }else{
                        Log.d("load_icon","-----------无图标");
                        icon.setImageDrawable(getResources().getDrawable(R.mipmap.book5));
                    }
                }
                //设置标题
                title.setText(bb.getBook_name());
                //设置当前进度
                int length = (int) (new File(bb.getBook_path()).length());
                String[] posit = bb.getRecordPosition().split(",");
                int pp = Integer.parseInt(posit[0]);
                if(pp != 0){
                    long prec = pp*100/length;
                    process.setText(prec+"%");
                }else{
                    process.setText("0%");
                }

                //设置点击事件
                cover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,ReadingPageActivity.class);
                        intent.setFlags(0x132);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("slect_book",bb);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
                ll_rec_book.addView(view);
            }

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ll_rec_book.removeAllViews();
        loadRecData();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_library:
                startMyActivity(LocalLibraryActivity.class);
                break;
            case R.id.iv_bookmark:
                startMyActivity(BookmarkActivity.class);
                break;
            case R.id.iv_setting:
                startMyActivity(SettingsActivity.class);
                break;
            case R.id.iv_weblibrary:
                startMyActivity(WebLibraryActivity.class);
                break;
        }
    }

    private boolean mIsExit;
    @Override
    /**
     * 双击返回键退出
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                this.finish();

            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
