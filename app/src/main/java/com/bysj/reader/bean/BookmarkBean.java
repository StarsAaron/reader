package com.bysj.reader.bean;

import java.io.Serializable;

/**
 * Created by zhyling on 2016/3/8.
 */
public class BookmarkBean implements Serializable{
    private int bookmark_id = -1;//书签id
    private String bookmark_bookname = "";//书名
    private int book_id = -1;//书id
    private String book_position = "";//阅读位置
    private String book_page_desc = "";//书签所在页的首行字符

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public int getBookmark_id() {
        return bookmark_id;
    }

    public void setBookmark_id(int bookmark_id) {
        this.bookmark_id = bookmark_id;
    }

    public String getBookmark_bookname() {
        return bookmark_bookname;
    }

    public void setBookmark_bookname(String bookmark_bookname) {
        this.bookmark_bookname = bookmark_bookname;
    }

    public String getBook_position() {
        return book_position;
    }

    public void setBook_position(String book_position) {
        this.book_position = book_position;
    }

    public String getBook_page_deac() {
        return book_page_desc;
    }

    public void setBook_page_deac(String book_page_deac) {
        this.book_page_desc = book_page_deac;
    }
}
