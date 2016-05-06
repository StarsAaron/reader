package com.bysj.reader.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bysj.reader.bean.BookInfo;
import com.bysj.reader.bean.BookmarkBean;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhyling on 2016/3/7.
 */
public class BookInfoDao {
    private Context context;
    private BookInfoDBOpenHelper bookInfoDBOpenHelper = null;

    public BookInfoDao(Context context) {
        this.context = context;
        bookInfoDBOpenHelper = new BookInfoDBOpenHelper(context, "bookInfo.db", null, 1);
    }

    /**
     * 添加书籍记录
     *
     * @param bookInfo
     * @return
     */
    public boolean addBook(BookInfo bookInfo) {
        SQLiteDatabase db = bookInfoDBOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("book_name", bookInfo.getBook_name());
        values.put("book_icon_path", bookInfo.getBook_icon());
        values.put("book_author", bookInfo.getBook_author());
        values.put("book_date", bookInfo.getBook_add_date());
        values.put("book_size", bookInfo.getBook_size());
        values.put("book_path", bookInfo.getBook_path());
        values.put("book_record_position", bookInfo.getRecordPosition());
        values.put("book_lastReadTime", bookInfo.getLastReadTime());
        long i = db.insert("bookinfo", null, values);
        db.close();
        if (i != -1) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 查找是否存在书籍记录
     * @param name
     * @return
     */
    public boolean searchBookByName(String name){
        SQLiteDatabase db = bookInfoDBOpenHelper.getReadableDatabase();
        Cursor cursor = null;
        cursor = db.query("bookinfo", null, "book_name=?", new String[]{name}, null, null,null,null);
        if(cursor != null && cursor.moveToNext()) {
            return true;
        }else{
            return false;
        }
    }

    /**
     * 添加书签记录
     *
     * @param bookmarkBean
     * @return
     */
    public boolean addBookmark(BookmarkBean bookmarkBean) {
        SQLiteDatabase db = bookInfoDBOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("book_name", bookmarkBean.getBookmark_bookname());
        values.put("book_id", bookmarkBean.getBook_id());
        values.put("book_position", bookmarkBean.getBook_position());
        values.put("bookmark_desc", bookmarkBean.getBook_page_deac());

        long i = db.insert("bookmark", null, values);
        db.close();
        if (i != -1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取书列表
     * @param key 为空时全选
     * @param order 为空时默认按名称排序
     * @return
     */
    public List<BookInfo> getBookList(String key,String order) {
        List<BookInfo> bookInfos = new ArrayList<>();
        SQLiteDatabase db = bookInfoDBOpenHelper.getReadableDatabase();
        Cursor cursor = null;
        if (key == null || key.equals("")) {
            if(order.equals("") || order.equals("name")){
                cursor = db.query("bookinfo", null, null, null, null, null,"book_name");
            }
            if(order.equals("time")){
                cursor = db.query("bookinfo", null, null, null, null, null,"book_date");
            }
            if(order.equals("size")){
                cursor = db.query("bookinfo", null, null, null, null, null,"book_size");
            }
        } else {
            String sql = null;
            if(order.equals("") || order.equals("name")){
                sql = "select * from bookinfo where book_name like '%" + key.trim() + "%' ORDER BY book_name DESC";
            }
            if(order.equals("time")){
                sql = "select * from bookinfo where book_name like '%" + key.trim() + "%' ORDER BY book_date DESC";
            }
            if(order.equals("size")){
                sql = "select * from bookinfo where book_name like '%" + key.trim() + "%' ORDER BY book_size DESC";
            }
            cursor = db.rawQuery(sql, null);
//            cursor = db.query("password", null, "keyword like '%?%'", new String[]{key}, null, null, null);//不知道为什么不行
        }
        while (cursor != null && cursor.moveToNext()) {
            BookInfo bookInfo = new BookInfo();
            bookInfo.setBook_id(cursor.getInt(0));
            bookInfo.setBook_name(cursor.getString(1));
            bookInfo.setBook_icon(cursor.getString(2));
            bookInfo.setBook_author(cursor.getString(3));
            bookInfo.setBook_add_date(cursor.getLong(4));
            bookInfo.setBook_size(cursor.getInt(5));
            bookInfo.setBook_path(cursor.getString(6));
            bookInfo.setRecordPosition(cursor.getString(7));
            bookInfo.setLastReadTime(cursor.getLong(8));
            bookInfos.add(bookInfo);
        }
        cursor.close();
        db.close();
        return bookInfos;
    }

    /**
     * 获取最近阅读的书
     * @param count
     * @return
     */
    public List<BookInfo> getRecBookList(int count) {
        List<BookInfo> bookInfos = new ArrayList<>();
        SQLiteDatabase db = bookInfoDBOpenHelper.getReadableDatabase();
        Cursor cursor = null;
        cursor = db.query("bookinfo", null, null, null, null, null,"book_lastReadTime DESC","0,"+String.valueOf(count));
        while (cursor != null && cursor.moveToNext()) {
            BookInfo bookInfo = new BookInfo();
            bookInfo.setBook_id(cursor.getInt(0));
            bookInfo.setBook_name(cursor.getString(1));
            bookInfo.setBook_icon(cursor.getString(2));
            bookInfo.setBook_author(cursor.getString(3));
            bookInfo.setBook_add_date(cursor.getLong(4));
            bookInfo.setBook_size(cursor.getInt(5));
            bookInfo.setBook_path(cursor.getString(6));
            bookInfo.setRecordPosition(cursor.getString(7));
            bookInfo.setLastReadTime(cursor.getLong(8));
            bookInfos.add(bookInfo);
        }
        cursor.close();
        db.close();
        return bookInfos;
    }

    /**
     * 根据id获取书本信息
     * @param id
     * @return
     */
    public BookInfo getBookFromId(int id) {
        BookInfo bookInfo = null;
        SQLiteDatabase db = bookInfoDBOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("bookinfo", null, "book_id=?", new String[]{String.valueOf(id)}, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            bookInfo = new BookInfo();
            bookInfo.setBook_id(cursor.getInt(0));
            bookInfo.setBook_name(cursor.getString(1));
            bookInfo.setBook_icon(cursor.getString(2));
            bookInfo.setBook_author(cursor.getString(3));
            bookInfo.setBook_add_date(cursor.getLong(4));
            bookInfo.setBook_size(cursor.getInt(5));
            bookInfo.setBook_path(cursor.getString(6));
            bookInfo.setRecordPosition(cursor.getString(7));
            bookInfo.setLastReadTime(cursor.getLong(8));
        }
        cursor.close();
        db.close();
        return bookInfo;
    }

    /**
     * 获取书签列表
     *
     * @return
     */
    public List<BookmarkBean> getBookmarkList(String key) {
        List<BookmarkBean> bookmarkBeans = new ArrayList<>();
        SQLiteDatabase db = bookInfoDBOpenHelper.getReadableDatabase();
        Cursor cursor;
        if (key == null || key.equals("")) {
            cursor = db.query("bookmark", null, null, null, null, null, null);
        } else {
            String sql = "select * from bookmark where book_name like '%" + key.trim() + "%'";
            cursor = db.rawQuery(sql, null);
//            cursor = db.query("password", null, "keyword like '%?%'", new String[]{key}, null, null, null);//不知道为什么不行
        }
        while (cursor.moveToNext()) {
            BookmarkBean bookmarkBean = new BookmarkBean();
            bookmarkBean.setBookmark_id(cursor.getInt(0));
            bookmarkBean.setBookmark_bookname(cursor.getString(1));
            bookmarkBean.setBook_id(cursor.getInt(2));
            bookmarkBean.setBook_position(cursor.getString(3));
            bookmarkBean.setBook_page_deac(cursor.getString(4));
            bookmarkBeans.add(bookmarkBean);
        }
        cursor.close();
        db.close();
        return bookmarkBeans;
    }

    /**
     * 删除书记录和相关的书签记录
     */
    public void deleteBookFromId(int id) {
        SQLiteDatabase db = bookInfoDBOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            db.delete("bookinfo", "book_id=?", new String[]{String.valueOf(id)});
            db.delete("bookmark","book_id=?",new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
        }finally{
            db.endTransaction();
        }
        db.close();

    }

    /**
     * 删除全部书记录
     */
    public void deleteAllBook() {
        SQLiteDatabase db = bookInfoDBOpenHelper.getWritableDatabase();
        if (db.delete("bookinfo", "1", null) != -1) {
            db.close();
        } else {
            db.close();
        }

    }

    /**
     * 删除某个书签记录
     */
    public boolean deleteBookmarkFromId(int id) {
        SQLiteDatabase db = bookInfoDBOpenHelper.getWritableDatabase();
        if (db.delete("bookmark", "bookmark_id=?", new String[]{String.valueOf(id)}) != -1) {
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }

    }

    /**
     * 删除全部书签记录
     */
    public boolean deleteAllBookmark() {
        SQLiteDatabase db = bookInfoDBOpenHelper.getWritableDatabase();
        if (db.delete("bookmark", "1", null) != -1) {
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }

    }

    /**
     * 更新书信息
     *
     * @param bookInfo
     */
    public int chgBookInfo(BookInfo bookInfo) {
        SQLiteDatabase db = bookInfoDBOpenHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("book_name", bookInfo.getBook_name());
        values.put("book_icon_path", bookInfo.getBook_icon());
        values.put("book_author", bookInfo.getBook_author());
        values.put("book_date", bookInfo.getBook_add_date());
        values.put("book_size", bookInfo.getBook_size());
        values.put("book_path", bookInfo.getBook_path());
        values.put("book_record_position", bookInfo.getRecordPosition());
        values.put("book_lastReadTime", bookInfo.getLastReadTime());
        int i = db.update("bookinfo", values, "book_id=?", new String[]{String.valueOf(bookInfo.getBook_id())});
        if (i != 0) {
            db.close();
            return 0x124;
        } else {
            db.close();
            return 0x125;
        }
    }
}
