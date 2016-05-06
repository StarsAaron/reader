package com.bysj.reader.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bysj.reader.R;
import com.bysj.reader.adapter.MyBookMarkAdapter;
import com.bysj.reader.bean.BookInfo;
import com.bysj.reader.bean.BookmarkBean;
import com.bysj.reader.db.BookInfoDao;
import com.bysj.reader.util.ToastUtil;
import com.bysj.reader.util.ToolUtil;
import com.bysj.reader.view.AboutDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 书签
 */
public class BookmarkActivity extends BaseActivity {
    private MyBookMarkAdapter myBookMarkAdapter = null;
    private List<BookmarkBean> mBookInfos = null;
    private ListView lv_bookmark_list = null;
    private BookInfoDao bookInfoDao = null;
    private TextView tv_tip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        registView();
        initView();
    }

    private void registView() {
        lv_bookmark_list = (ListView) findViewById(R.id.lv_bookmark_list);
        tv_tip = (TextView)findViewById(R.id.tv_tip);
    }

    private void initView() {
        bookInfoDao = new BookInfoDao(getApplicationContext());
        //初始化标题
        setTitle(getResources().getString(R.string.bookmark_title));
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
                AlertDialog.Builder builder=new AlertDialog.Builder(BookmarkActivity.this);
                //2所有builder设置一些参数
                LinearLayout linearLayout = new LinearLayout(BookmarkActivity.this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                linearLayout.setLayoutParams(layoutParams);

                final EditText editText = new EditText(BookmarkActivity.this);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams2.weight = 1;
                editText.setLayoutParams(layoutParams2);

                Button btnSearch = new Button(BookmarkActivity.this);
                LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                btnSearch.setLayoutParams(layoutParams3);
                btnSearch.setText("搜索");
                btnSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String keyWord = editText.getText().toString().trim();
                        //查找关键字
                        mBookInfos = bookInfoDao.getBookmarkList(keyWord);
                        if(mBookInfos != null || mBookInfos.isEmpty()){
                            myBookMarkAdapter = new MyBookMarkAdapter(getApplicationContext(), mBookInfos);
                            lv_bookmark_list.setAdapter(myBookMarkAdapter);
                        }
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
                //菜单
                Button btnClear = new Button(getApplicationContext());
                btnClear.setText("全部清空");
                btnClear.setTextColor(getResources().getColor(R.color.text_color3));
                btnClear.setBackground(getResources().getDrawable(R.drawable.ic_btn_bg));
                //创建弹框
                final PopupWindow bookmarkClear = new PopupWindow(btnClear, ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                bookmarkClear.setFocusable(true);
                //注意：必须要设置背景
                bookmarkClear.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int intpx = ToolUtil.dipToPixel(35);
                int[] location = new int[2];
                v.getLocationInWindow(location);
                //设置弹出窗口位置
                bookmarkClear.showAtLocation(v, Gravity.TOP + Gravity.RIGHT, 0, location[1] + intpx);
                //按钮点击事件
                btnClear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(bookmarkClear.isShowing()){
                            bookmarkClear.dismiss();
                        }
                        final AboutDialog aboutDialog = new AboutDialog(BookmarkActivity.this);
                        aboutDialog.show();
                        aboutDialog.setTitle("提示");
                        aboutDialog.setAboutTv("是否全部清空");
                        aboutDialog.setCancelable(true);
                        aboutDialog.setOnClickListener(new AboutDialog.MyOnClickListener() {
                            @Override
                            public void onClick(int id) {
                                if (id == 1) { //如果点击确定
                                    if(bookInfoDao.deleteAllBookmark()){
                                        ToastUtil.show(BookmarkActivity.this, "清空完成！");
                                        mBookInfos.clear();
                                        myBookMarkAdapter.notifyDataSetChanged();
                                        showRightMenuButton(false);
                                        showRightSearchButton(false);
                                        tv_tip.setVisibility(View.VISIBLE);
                                        bookmarkClear.dismiss();
                                    }else{
                                        ToastUtil.show(BookmarkActivity.this,"清空失败！");
                                    }
                                } else {
                                    aboutDialog.dismiss();
                                }
                            }
                        });
                    }
                });
            }
        });


        mBookInfos = bookInfoDao.getBookmarkList("");
        if(mBookInfos != null && !mBookInfos.isEmpty()){
            tv_tip.setVisibility(View.GONE);
            myBookMarkAdapter = new MyBookMarkAdapter(getApplicationContext(), mBookInfos);
            lv_bookmark_list.setAdapter(myBookMarkAdapter);
            lv_bookmark_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    ToastUtil.show(getApplicationContext(), "点击了" + String.valueOf(position));
                    BookmarkBean bookmarkBean = (BookmarkBean)myBookMarkAdapter.getItem(position);
                    Intent intent = new Intent(BookmarkActivity.this,ReadingPageActivity.class);
                    //把book对象传过去
                    Bundle bundle = new Bundle();
                    bundle.putInt("slect_book_id", bookmarkBean.getBook_id());
                    bundle.putString("slect_book_position", bookmarkBean.getBook_position());
                    intent.putExtras(bundle);
                    intent.setFlags(0x133);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            });
            lv_bookmark_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    BookmarkBean bookmarkBean = (BookmarkBean)myBookMarkAdapter.getItem(position);
//                    ToastUtil.show(getApplicationContext(), "长按了" + String.valueOf(position));
                    showPopuWindow(view,bookmarkBean.getBookmark_id());//显示删除弹框
                    return false;
                }
            });
        }else{
            showRightMenuButton(false);
            showRightSearchButton(false);
        }
    }

    /**
     * 显示删除弹框
     * @param view
     * @param mId 书签id
     */
    private void showPopuWindow(View view,final int mId){
        ImageView popuDel = new ImageView(getApplicationContext());
        final PopupWindow bookmarkEdit = new PopupWindow(popuDel, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        bookmarkEdit.setFocusable(true);
        //注意：必须要设置背景
        bookmarkEdit.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popuDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookInfoDao.deleteBookmarkFromId(mId)) {
                    ToastUtil.show(BookmarkActivity.this, "删除成功！");
                    bookmarkEdit.dismiss();
                    mBookInfos = bookInfoDao.getBookmarkList("");
                    lv_bookmark_list.invalidate();
                    if (mBookInfos == null || mBookInfos.isEmpty()) {
                        tv_tip.setVisibility(View.VISIBLE);
                        showRightMenuButton(false);
                        showRightSearchButton(false);
                    } else {
                        myBookMarkAdapter = new MyBookMarkAdapter(getApplicationContext(), mBookInfos);
                        lv_bookmark_list.setAdapter(myBookMarkAdapter);
                    }
                } else {
                    ToastUtil.show(BookmarkActivity.this, "删除失败！");
                }
            }
        });
        popuDel.setBackground(getResources().getDrawable(R.mipmap.ic_popu_del2));

        //根据手机手机的分辨率把dip转化成不同的值px
        int intpx = ToolUtil.dipToPixel(getWindowManager().getDefaultDisplay().getWidth() - 10);
        int intpx2 = ToolUtil.dipToPixel(10);
        int[] location = new int[2];
        view.getLocationInWindow(location);
        //设置弹出窗口位置
        bookmarkEdit.showAtLocation(view, Gravity.TOP + Gravity.LEFT, intpx, location[1]+intpx2);

        //设置弹窗动画
        AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
        aa.setDuration(200);

        ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0);

        //缩放的关键,指定x,y轴的缩放方式,由上到下,数字翻转就是由左到右

        sa.setDuration(200);
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(sa);
        set.addAnimation(aa);
        popuDel.startAnimation(set);
    }
}
