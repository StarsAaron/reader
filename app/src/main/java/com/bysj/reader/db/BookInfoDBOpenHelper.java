package com.bysj.reader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookInfoDBOpenHelper extends SQLiteOpenHelper {
    public BookInfoDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建书表
        db.execSQL("create table bookinfo("
                        + "book_id integer primary key autoincrement,"
                        + "book_name varchar(20) NOT NULL,"
                        + "book_icon_path varchar(100),"
                        + "book_author varchar(20),"
                        + "book_date long,"
                        + "book_size integer,"
                        + "book_path varchar(100) NOT NULL,"
                        + "book_record_position varchar(20),"
                        + "book_lastReadTime long)"
        );
        db.execSQL("create table bookmark(" +
                        "bookmark_id integer primary key autoincrement," +
                        "book_name varchar(20) NOT NULL," +
                        "book_id integer NOT NULL," +
                        "book_position varchar(20) NOT NULL," +
                        "bookmark_desc varchar(20) NOT NULL)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
