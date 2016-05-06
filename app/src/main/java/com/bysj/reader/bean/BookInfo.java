package com.bysj.reader.bean;

import java.io.Serializable;

/**
 * Created by zhyling on 2016/3/3.
 */
public class BookInfo implements Serializable {
    private int book_id = -1;//书id
    private String book_icon = "";//书图标路径
    private String book_name = "";//书名
    private String book_author = "";//作者
    private long book_add_date = 0;//添加书的日期(毫秒)
    private int book_size = 0;//书大小
    private String book_path = "";//书路径
    private String recordPosition = "0,0";//读书记录位置
    private long lastReadTime = 0;//最后一次阅读时间

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public long getBook_add_date() {
        return book_add_date;
    }

    public void setBook_add_date(long book_add_date) {
        this.book_add_date = book_add_date;
    }

    public String getBook_author() {
        return book_author;
    }

    public void setBook_author(String book_author) {
        this.book_author = book_author;
    }

    public String getBook_icon() {
        return book_icon;
    }

    public void setBook_icon(String book_icon) {
        this.book_icon = book_icon;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getBook_path() {
        return book_path;
    }

    public void setBook_path(String book_path) {
        this.book_path = book_path;
    }

    public int getBook_size() {
        return book_size;
    }

    public void setBook_size(int book_size) {
        this.book_size = book_size;
    }

    public String getRecordPosition() {
        return recordPosition;
    }

    public void setRecordPosition(String recordPosition) {
        this.recordPosition = recordPosition;
    }

    public long getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(long lastReadTime) {
        this.lastReadTime = lastReadTime;
    }
}
