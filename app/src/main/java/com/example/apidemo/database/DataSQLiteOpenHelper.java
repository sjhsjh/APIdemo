package com.example.apidemo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <br> 负责创建数据库or更新数据库的列结构.
 * Created by jinhui.shao on 2017/4/12.
 */
public class DataSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "lockscreen.db";
    /** 桌面数据库原始版本号 */
    private static final int INITIAL_DB_VERSION = 1;
    /** 桌面数据库当前版本号 */
    private static final int CURRENT_DATABASE_VERSION = 1;
    private Context mContext;

    public DataSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DataSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public DataSQLiteOpenHelper(Context context){
        super(context, DATABASE_NAME, null, CURRENT_DATABASE_VERSION);
        mContext = context;
        // 获取数据库，触发onCreate或者onUpgrade函数执行。
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w("sjh1", "DataBase onCreate");
        // 执行初始创建数据库脚本
        createSQL(db);
        saveData2DB(db);
    }

    private void createSQL(final SQLiteDatabase db) {
//        TaskManager.execWorkTask(new Runnable() {
//            @Override
//            public void run() {
                db.beginTransaction();
                try {
                    db.execSQL(NewsTable.CREATE_SQL);

                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Log.e("sjh1", "createSQL", e);
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }
//            }
//        });
    }

    public void saveData2DB(SQLiteDatabase db) {
        try {
            db.beginTransaction();

            ContentValues values = getContentValues();
            //保存到数据库
            long rowId = db.insert(NewsTable.TABLE_NAME, null, values); // 只能在已存在的列中插入数据。内含try catch
            //插入记录失败，打印错误日志，但是不中断操作
            if (rowId <= 0) {
                Log.e("sjh1", "Insert {0} to DB error, please check!");
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("sjh1", "saveData2DB", e);
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

    public void saveData2DB_2(SQLiteDatabase db) {
        try {
            db.beginTransaction();

            ContentValues contentValues = new ContentValues();
            contentValues.put(NewsTable.NEWS_LIST, "ok!!!!");
            contentValues.put(NewsTable.ORIGIN, "myOrigin");
            contentValues.put(NewsTable.IMAGE_URL, "myURL");
            contentValues.put(NewsTable.PEOPLE_READ, 654654654);
            //保存到数据库
            long rowId = db.insert(NewsTable.TABLE_NAME, null, contentValues); // 只能在已存在的列中插入数据。内含try catch
            //插入记录失败，打印错误日志，但是不中断操作
            if (rowId <= 0) {
                Log.e("sjh1", "Insert {0} to DB error, please check!");
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("sjh1", "saveData2DB", e);
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }
    /**
     * 表创建的时候确定每列的类型，保存的时候会转成每列的类型。boolean值会存储为0和1。
     * @return
     */
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NewsTable.NEWS_LIST, "myTitle");
        contentValues.put(NewsTable.ORIGIN, "myOrigin");
        contentValues.put(NewsTable.IMAGE_URL, "myURL");
        contentValues.put(NewsTable.PEOPLE_READ, 12);
        return contentValues;
    }

    /**
     * 表结构变化时才执行
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("sjh1", "onUpgrade oldVersion = " + oldVersion + "  newVersion = " + newVersion);
        if (oldVersion < INITIAL_DB_VERSION || oldVersion >= newVersion || newVersion > CURRENT_DATABASE_VERSION) {
            return;
        }
        createNewTable(db, NewsTable.TABLE_NAME, NewsTable.fieldMap);
    }

    /**
     * 新表可能有删减字段，以最新字段及其类型创建一份新表（就字段的内容会保留下来）
     * @param db
     * @param tableName
     * @param map key：最新的字段；value：最新字段对应的类型。格式：fieldMap.put(NEWS_LIST, TYPE_TEXT);
     * @return
     */
    private boolean createNewTable(SQLiteDatabase db, String tableName, LinkedHashMap<String, String> map) {
        Log.w("sjh1", "map = " + map.toString());
        boolean result = false;
        Cursor cursor = null;
        try {
            // 查询列数
            String allColumns[] = { "*" };
            cursor = db.query(tableName, allColumns, null, null, null, null, null);
            String oldColumns[] = cursor.getColumnNames();

            List<String> existColumns = new ArrayList<String>();
            List<String> newAddedColumn = new ArrayList<String>();


            for(String newColumn : map.keySet()){
                int i;
                for(i = 0; i < oldColumns.length; i++){
                    if(!TextUtils.isEmpty(newColumn) && newColumn.equals(oldColumns[i])){
                        existColumns.add(newColumn);
                        break;
                    }
                }
                if(i == oldColumns.length){
                    newAddedColumn.add(newColumn);
                }

            }
            // ①将已存在的字段所在的列全部取出来创建一个新表
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < existColumns.size(); i++){
                sb.append(existColumns.get(i));
                if(i < existColumns.size() - 1){
                    sb.append(", ");
                }
            }
            createExistFieldTable(db, NewsTable.TABLE_NAME, sb.toString());

            // ②将新增的字段插入上述的新表
            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String columnName = (String) entry.getKey();
                String columnType = (String) entry.getValue();
                addColumnToTable(db, NewsTable.TABLE_NAME, columnName, columnType,  null);
            }

        }
        catch (Exception e) {
            Log.e("sjh1", "createNewTable has exception");
            e.printStackTrace();
            result = false;
        }
        finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return result;
    }

    /**
     * 删除表中字段.步骤：先取需要留下的所有字段复制到临时表中，删掉原来的表，再将临时的表重命名为原表的名字.
     * SQLite supports a limited subset of ALTER TABLE. The ALTER TABLE command in SQLite
     * allows the user to rename a table or to add a new column to an existing table.
     * It is not possible to rename a column, remove a column, or add or remove constraints
     * from a table.
     * @param db
     * @param tableName  要修改的表名称
     * @param existFieldStr 删除字段后table的所有字段！格式“a, b, c”
     */
    private void createExistFieldTable(SQLiteDatabase db, String tableName, String existFieldStr){
        Log.w("sjh1", "createExistFieldTable");
        db.beginTransaction();
        try {
            //create table temp as select recordId, customer, place, time from record
            //drop table record;
            //alter table temp rename to record;
            //"ALTER TABLE " + tableName + " DROP COLUMN " + columnName; 无效
            String createTableSql = "create table temp as select " + existFieldStr + " from " + tableName;
            db.execSQL(createTableSql);
            String deleteTableSql = "drop table " + tableName;
            db.execSQL(deleteTableSql);
            String renameTableSql = "alter table temp rename to " + tableName;
            db.execSQL(renameTableSql);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("sjh1", "SQLException : " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 新添加单个字段到表中
     * @param db            :db 名字
     * @param tableName    : 修改表名
     * @param columnName   ：新增字段名
     * @param columnType   ：新增字段类型
     * @param defaultValue ：新增字段默认值。为null，则不提供默认值
     */
    public void addColumnToTable(SQLiteDatabase db, String tableName,
                                 String columnName, String columnType, String defaultValue) {
        Log.w("sjh1", "addColumnToTable");
        if (!isExistColumnInTable(db, tableName, columnName)) {
            db.beginTransaction();
            try {
                // 增加字段
                String updateSql = "ALTER TABLE " + tableName + " ADD "
                        + columnName + " " + columnType;
                db.execSQL(updateSql);

                // 提供默认值
                if (defaultValue != null) {
                    if (columnType.equals(Table.TYPE_TEXT)) {
                        // 如果是字符串类型，则需加单引号
                        defaultValue = "'" + defaultValue + "'";
                    }
                    updateSql = "update " + tableName + " set " + columnName
                            + " = " + defaultValue;
                    db.execSQL(updateSql);
                }
                db.setTransactionSuccessful();

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                db.endTransaction();
            }
        }
    }

    /**
     * 检查表中是否存在该字段  selete [列] from [table]  where [条件]。“*”代表所有；sql语句不区分大小写。
     * select * from table a;
     * @param db
     * @param tableName
     * @param columnName
     * @return
     */
    private boolean isExistColumnInTable(SQLiteDatabase db, String tableName, String columnName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            String allColumns[] = { "*" };
            cursor = db.query(tableName, allColumns, null, null, null, null, null);
            result = cursor != null && cursor.getColumnIndex(columnName) >= 0;
        }
        catch (Exception e) {
            Log.e("sjh1", "isExistColumnInTable has exception");
            e.printStackTrace();
            result = false;
        }
        finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return result;
    }

}