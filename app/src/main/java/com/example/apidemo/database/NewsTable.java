package com.example.apidemo.database;

import android.net.Uri;
import java.util.LinkedHashMap;

/**
 * <br>类描述 : 数据表
 */
public class NewsTable extends Table {
    /** 数据库表名称 */
    public static final String TABLE_NAME = "news";
    /** uri的authority */
    public static final String AUTHORITY = "com.tcl.joylockscreen.authority.NewsProvider";
    /** 访问该数据库表的uri */
    public static final Uri CONTENT_URI = Uri.parse("content://" + NewsTable.AUTHORITY + "/" + TABLE_NAME);
    public static LinkedHashMap<String, String> fieldMap = new LinkedHashMap<String, String>();

    public static final String NEWS_LIST = "news_list";
    public static final String ORIGIN = "origin";
    public static final String IMAGE_URL = "image_url";
    public static final String PEOPLE_READ = "people_read";

    // ps：CREATE_SQL的内容要与fieldMap保持一致，且改变CREATE_SQL的内容后要提升数据库的版本（DataSQLiteOpenHelper.CURRENT_DATABASE_VERSION）才会升级数据库.
    public static final String CREATE_SQL = "create table " + TABLE_NAME + " ("
            + NEWS_LIST + " " + TYPE_TEXT + ", "
            + ORIGIN + " " + TYPE_TEXT + ", "
            + IMAGE_URL + " " + TYPE_TEXT + ", "
            + PEOPLE_READ + " " + TYPE_NUMERIC
            + ")";

    static {
        fieldMap.put(NEWS_LIST, TYPE_TEXT);
        fieldMap.put(ORIGIN, TYPE_TEXT);
        fieldMap.put(IMAGE_URL, TYPE_TEXT);
        fieldMap.put(PEOPLE_READ, TYPE_NUMERIC);
    }

}
