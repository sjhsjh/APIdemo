package com.example.apidemo.activity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.database.DataResolver;
import com.example.apidemo.database.DataSQLiteOpenHelper;
import com.example.apidemo.database.NewsTable;

/**
 * <br>
 * Created by jinhui.shao on 2017/4/12.
 */
public class SQLActivity extends BaseActivity {
    private DataSQLiteOpenHelper mDdataSQLiteOpenHelper;
    private DataResolver dataResolver;
    private int i = 100;
    private int j = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);
        mDdataSQLiteOpenHelper = new DataSQLiteOpenHelper(this);
        dataResolver = DataResolver.getInstance();

        ((Button)findViewById(R.id.button1)).setText("open OR create OR upgrade db");
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDdataSQLiteOpenHelper = new DataSQLiteOpenHelper(SQLActivity.this);
            }
        });

        ((Button)findViewById(R.id.button2)).setText("insert one row data ");
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = mDdataSQLiteOpenHelper.getWritableDatabase();
                mDdataSQLiteOpenHelper.saveData2DB_2(db);
            }
        });


        ((Button)findViewById(R.id.button3)).setText("insert");
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(NewsTable.NEWS_LIST, Integer.toString(i++));
                dataResolver.insertData(NewsTable.TABLE_NAME, contentValues);
            }
        });

        ((Button)findViewById(R.id.button4)).setText("delete all");
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataResolver.deleteData(NewsTable.TABLE_NAME);
            }
        });

        ((Button)findViewById(R.id.button5)).setText("query");
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataResolver.queryData(NewsTable.TABLE_NAME);
            }
        });

        ((Button)findViewById(R.id.button6)).setText("update");
        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(NewsTable.NEWS_LIST, Integer.toString(j++));
                dataResolver.updateData(NewsTable.TABLE_NAME, contentValues);
            }
        });
    }


}