package com.example.apidemo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.telecom.Log;

/**
 * <br>
 * Created by jinhui.shao on 2017/4/14.
 */
public class DataResolver {
    private DataSQLiteOpenHelper mDataSQLiteOpenHelper;

    public static DataResolver getInstance(){
        return DataResolverProducer.sInstance;
    }

    private static class DataResolverProducer {
        private static DataResolver sInstance ;//= new DataResolver();
    }

    // private DataResolver() {
    public DataResolver(Context context) {
        mDataSQLiteOpenHelper = new DataSQLiteOpenHelper(context);
    }

    /**
     * 向数据库插入数据
     * @param tableName 表名字
     * @param contentValues  需要保存的数据。格式如：ContentValues contentValues = new ContentValues();
     *                        contentValues.put(NewsTable.NEWS_LIST, "ok");
     * @return 操作是否成功
     */
    public boolean insertData(String tableName, ContentValues contentValues) {
        SQLiteDatabase db = mDataSQLiteOpenHelper.getWritableDatabase();

        long rowID = db.insert(tableName, null, contentValues);
        Log.w("sjh1", "rowID = " + rowID);  // W/TelecomFramework: sjh1: rowID = 5
        // rowID从1开始
        return rowID > 0;
    }

    /**
     * 删除指定table所有行的数据.（暂时删除所有行的数据）
     * @param tableName 表名字
     * @return 返回成功删除了的行数量.
     */
    public int deleteData(String tableName) {
        // DELETE FROM COMPANY WHERE ID = 7;
        SQLiteDatabase db = mDataSQLiteOpenHelper.getWritableDatabase();

        String sql = "SELECT COUNT(*) FROM " + tableName;
        SQLiteStatement statement = db.compileStatement(sql);
        long rowCount = statement.simpleQueryForLong();

        int row  = 0;
        String selection = "rowid" + " > ?";
        String[] selectionArgs = new String[]{Integer.toString(row)};
        int deleteCount = db.delete(tableName, selection, selectionArgs);
        Log.w("sjh1", "rowCount = " + rowCount + "deleteCount = " + deleteCount);
        return deleteCount;
    }

    /**
     * 更新数据库中已存在的数据（暂时每次更新第一行）
     * @param tableName 表名字
     * @param contentValues  需要保存的数据。格式如：ContentValues contentValues = new ContentValues();
     *                        contentValues.put(NewsTable.NEWS_LIST, "ok");
     * @return 返回成功更新了的行数量.
     */
    public int updateData(String tableName, ContentValues contentValues) {
        SQLiteDatabase db = mDataSQLiteOpenHelper.getWritableDatabase();
        //  db.execSQL("UPDATE person SET phone='0592' WHERE personid=5");

        int row  = 1;
        String selection = "rowid" + " = ?";
        String[] selectionArgs = new String[]{Integer.toString(row)};
        int updateCount = db.update(tableName, contentValues, selection, selectionArgs);
        Log.w("sjh1", "updateCount = " + updateCount);
        return updateCount;
    }

    /**
     * 取出指定的数据.（暂时每次只取出第一行）
     * @param tableName 表名字
     * @return 返回查询得到的字符串，若失败则返回""
     */
    public String queryData(String tableName) {
        SQLiteDatabase db = mDataSQLiteOpenHelper.getWritableDatabase();

        String newsJSON = "";
        String allColumns[] = { "*" };
        Cursor cursor = db.query(tableName, allColumns, null, null, null, null, null);

        try {
            if(cursor != null && cursor.moveToFirst()){
                int columnIndex = cursor.getColumnIndex(NewsTable.NEWS_LIST);
                newsJSON = cursor.getString(columnIndex);
                Log.w("sjh1", "newsJSON = " + newsJSON);

//        while(cursor.moveToNext())
//        {
//            int columnIndex = cursor.getColumnIndex("name");
//            String name = cursor.getString(columnIndex);
//            int columnIndex2 = cursor.getColumnIndex("phone");
//            String phone = cursor.getString(columnIndex2);
//        }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return newsJSON;

    }


}