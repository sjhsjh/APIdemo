package com.example.apidemo.activity;

import android.app.ActivityManager;
import android.app.IntentService;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;
import java.util.ArrayList;

/**
 * <br> 悬浮header的ListView，根布局是StickyLayout
 * Created by jinhui.shao on 2017/8/28.
 */
public class SlideActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_layout);
        createList(R.id.listview_slide);

        MyAsyncTask at = new MyAsyncTask();
        at.execute();
//        at.execute();     //      Caused by: java.lang.IllegalStateException: Cannot execute task: the task is already running.
        HandlerThread h;
        Thread t = new Thread();
        IntentService it ;

    }

    class MyAsyncTask extends AsyncTask<Integer, Integer, String>{

        public MyAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {
            NLog.d("sjh8", "values = " +  params.toString() );
            for(int i = 0; i <= 20; i++){
                publishProgress(i);//此行代码对应下面onProgressUpdate方法
                try {
                    Thread.sleep(1000);//耗时操作，如网络请求
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "执行完毕";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            NLog.d("sjh8", "values = " + values[0].intValue());
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

   private static Handler h = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {

        }
    };

    private void createList(int resourceID) {
        ListView listView = (ListView) findViewById(resourceID);
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            list.add(resourceID + " item " + i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item, R.id.itemview, list);
        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                    int position, long id) {
//                Toast.makeText(DemoActivity_1.this, "click item " + position, Toast.LENGTH_SHORT).show();
//            }
//        });
    }


}