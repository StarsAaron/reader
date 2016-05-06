package com.bysj.reader.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bysj.reader.R;
import com.bysj.reader.adapter.BookShelfAdapter;
import com.bysj.reader.bean.BookInfo;
import com.bysj.reader.db.BookInfoDao;
import com.bysj.reader.util.ToastUtil;
import com.bysj.reader.util.ToolUtil;
import com.bysj.reader.view.ContentGridView;
import com.bysj.reader.view.ContentScrollView;
import com.bysj.reader.view.SignalChooseDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 本地书库
 */
public class LocalLibraryActivity extends BaseActivity implements BookShelfAdapter.OnNewsItem {
    private ContentScrollView scrollView;
    private ContentGridView bookGridView;
    private BookShelfAdapter adapter;
    private List<BookInfo> bookList = null;
    private BookInfoDao bookInfoDao = null;

    private static final int REQUEST_CODE = 1; // 请求码
    public static final String EXTRA_FILE_CHOOSER = "file_chooser";

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0x121://刷新列表
                    adapter = new BookShelfAdapter(LocalLibraryActivity.this, bookList, LocalLibraryActivity.this);
                    bookGridView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                break;
                case 0x122:
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_library);
        registView();
        initView();
        loadData("","");//加载数据
    }

    private void registView() {
        bookGridView = (ContentGridView) findViewById(R.id.content_gridview);
        scrollView = (ContentScrollView) findViewById(R.id.scrollView);
    }

    private void initView() {
        bookInfoDao = new BookInfoDao(getApplicationContext());
        //初始化标题
        setTitle(getResources().getString(R.string.locallibrary_title));
        showLeftButton(true);
        showRightSearchButton(true);
        showRightMenuButton(true);
        setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        setRightSearchButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查找
                //1获取一个对话框的创建器
                AlertDialog.Builder builder = new AlertDialog.Builder(LocalLibraryActivity.this);
                //2所有builder设置一些参数
                LinearLayout linearLayout = new LinearLayout(LocalLibraryActivity.this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearLayout.setLayoutParams(layoutParams);

                final EditText editText = new EditText(LocalLibraryActivity.this);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams2.weight = 1;
                editText.setLayoutParams(layoutParams2);

                Button btnSearch = new Button(LocalLibraryActivity.this);
                LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                btnSearch.setLayoutParams(layoutParams3);
                btnSearch.setText("搜索");
                btnSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.show(LocalLibraryActivity.this, "搜索中...");
                        String keyWork = editText.getText().toString().trim();
                        loadData(keyWork,"");
                    }
                });
                linearLayout.addView(editText);
                linearLayout.addView(btnSearch);

                builder.setCancelable(true);
                builder.setView(linearLayout);
                builder.create().show();
            }
        });
        setRightMenuButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.popuwindow_view, null);

                final PopupWindow bookShelfMenu = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                bookShelfMenu.setFocusable(true);
                //注意：必须要设置背景
                bookShelfMenu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int intpx = ToolUtil.dipToPixel(35);
                int[] location = new int[2];
                v.getLocationInWindow(location);
                //设置弹出窗口位置
                bookShelfMenu.showAtLocation(v, Gravity.TOP + Gravity.RIGHT, 0, location[1] + intpx);
                //设置菜单
                //导入书籍
                TextView textView = (TextView) view.findViewById(R.id.tv_import);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if(bookShelfMenu.isShowing()){
//                            bookShelfMenu.dismiss();
//                        }
////                        ToastUtil.show(LocalLibraryActivity.this, "daoru");
//                        //调用系统自带的文件浏览器
//                        try {
//                            Intent intent = new Intent();
//                            intent.setAction(Intent.ACTION_GET_CONTENT);
//                            Uri startDir = Uri.fromFile(new File("/sdcard"));
//                            intent.setDataAndType(startDir, "vnd.android.cursor.dir/lysesoft.andexplorer.file");
//                            startActivityForResult(intent, 0x111);
//                        } catch (Exception e) {
//                            ToastUtil.show(LocalLibraryActivity.this, "Please install file explorer!");
//                        }

                        //第二种：调用自己写的文件浏览器
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                            Intent intent = new Intent(LocalLibraryActivity.this, FileChooserActivity.class);
                            startActivityForResult(intent, REQUEST_CODE);
                        } else {
                            ToastUtil.show(LocalLibraryActivity.this,"SDCard不存在！");
                        }
                    }
                });
                //排序
                TextView textView2 = (TextView) view.findViewById(R.id.tv_order);
                textView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(bookShelfMenu.isShowing()){
                            bookShelfMenu.dismiss();
                        }
//                        ToastUtil.show(LocalLibraryActivity.this, "paixu");
                        SignalChooseDialog signalChooseDialog = new SignalChooseDialog(LocalLibraryActivity.this);
                        signalChooseDialog.setOnClickListener(new SignalChooseDialog.MyListener() {
                            @Override
                            public void onClick(String order) {
                                loadData("",order);
                            }
                        });
                        signalChooseDialog.setCancelable(true);
                        signalChooseDialog.show();
                    }
                });
            }
        });

        scrollView.OnBottomClickListener(new ContentScrollView.OnBottomClickListener() {

            @Override
            public void OnBottomClickListener() {
//                ToastUtil.show(LocalLibraryActivity.this, "正在加载更多数据");
                //可以用于分段加载
            }
        });

    }

    /**
     * 加载数据
     */
    private void loadData(final String key,final String order) {
        new Thread(){
            @Override
            public void run() {
                bookList = bookInfoDao.getBookList(key,order);//默认按书名排序
                if(bookList != null || !bookList.isEmpty()){
                    mHandler.sendEmptyMessage(0x121);//刷新列表
                }
            }
        }.start();
    }

    @Override
    public void oNitemClick(BookInfo bookInfo) {
//        ToastUtil.show(LocalLibraryActivity.this,"点击事件");
        //检查文件是否存在，不存在则从列表中去掉
        File file = new File(bookInfo.getBook_path());
        if(file.exists()){
            Intent intent = new Intent(LocalLibraryActivity.this, ReadingPageActivity.class);
            //把book对象传过去
            Bundle bundle = new Bundle();
//            bundle.putInt("slect_book_id", bookInfo.getBook_id());
            bundle.putSerializable("slect_book",bookInfo);
            intent.putExtras(bundle);
            intent.setFlags(0x132);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }else{
            ToastUtil.show(LocalLibraryActivity.this,"书本不存在，可能位置改变了");
            bookInfoDao.deleteBookFromId(bookInfo.getBook_id());
            loadData("","");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // 打印文本
        if (requestCode == 0x111) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri uri = intent.getData();
                    if (uri != null) {
                        String path = uri.toString();
                        path = path.substring(path.indexOf("/storage"), path.length());
                        File mFile = new File(path);
                        if(mFile.exists()){
                            BookInfo bookInfo = new BookInfo();
                            bookInfo.setBook_name(mFile.getName());
                            bookInfo.setBook_path(mFile.getPath());
                            bookInfo.setBook_size((int) mFile.length());
                            bookInfo.setBook_add_date(new Date(System.currentTimeMillis()).getTime());// 获取当前时间
                            if(bookInfoDao.addBook(bookInfo)){
                                ToastUtil.show(LocalLibraryActivity.this,"导入成功");
                                loadData("","");//刷新页面
                            }else{
                                ToastUtil.show(LocalLibraryActivity.this,"导入失败");
                            }
                        }
                    }
                } catch (Exception e) {
                    return;
                }
            }
        }

        if (resultCode == RESULT_CANCELED) { //取消选择
            return;
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // 获取路径名
            String textPath = intent.getStringExtra(EXTRA_FILE_CHOOSER);
            if (textPath != null) {
                File mFile = new File(textPath);
                if(mFile.exists()) {
                	if(bookInfoDao.searchBookByName(mFile.getName())){
                        ToastUtil.show(LocalLibraryActivity.this,"已存在记录，不需再导入");
                    }else {
                    BookInfo bookInfo = new BookInfo();
                    bookInfo.setBook_name(mFile.getName());
                    bookInfo.setBook_path(mFile.getPath());
                    bookInfo.setBook_size((int) mFile.length());
                    bookInfo.setBook_add_date(new Date(System.currentTimeMillis()).getTime());// 获取当前时间
                    if (bookInfoDao.addBook(bookInfo)) {
                        ToastUtil.show(LocalLibraryActivity.this, "导入成功");
                        loadData("", "");//刷新页面
                    } else {
                        ToastUtil.show(LocalLibraryActivity.this, "导入失败");
                    }
                  }
                }
            } else{

            }
        }
    }
}
