package com.example.apidemo;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.crashlytics.android.Crashlytics;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.example.apidemo.activity.ADActivity;
import com.example.apidemo.activity.APMActivity;
import com.example.apidemo.activity.AppBarActivity;
import com.example.apidemo.activity.AutoClickActivity;
import com.example.apidemo.activity.CoordinatorActivity;
import com.example.apidemo.activity.DIYViewActivity;
import com.example.apidemo.activity.EventDispatchActivity;
import com.example.apidemo.activity.FunctionActivity;
import com.example.apidemo.activity.GaussActivity;
import com.example.apidemo.activity.GestureDectorActivity;
import com.example.apidemo.activity.GridViewActivity;
import com.example.apidemo.activity.GsonActivity;
import com.example.apidemo.activity.HardWareActivity;
import com.example.apidemo.activity.JobServiceActivity;
import com.example.apidemo.activity.MessengerActivity;
import com.example.apidemo.activity.NestScrollActivity;
import com.example.apidemo.activity.NewsActivity;
import com.example.apidemo.activity.NotificationActivity;
import com.example.apidemo.activity.PowerManagerActivity;
import com.example.apidemo.activity.ReferenceActivity;
import com.example.apidemo.activity.ResolveInfoActivity;
import com.example.apidemo.activity.RxJavaActivity;
import com.example.apidemo.activity.SDActivity;
import com.example.apidemo.activity.SQLActivity;
import com.example.apidemo.activity.ScaleActivity;
import com.example.apidemo.activity.ScreenShotActivity;
import com.example.apidemo.activity.ScrollActivity;
import com.example.apidemo.activity.SlideActivity;
import com.example.apidemo.activity.SocketClientActivity;
import com.example.apidemo.activity.SocketServerActivity;
import com.example.apidemo.activity.TestServiceActivity;
import com.example.apidemo.activity.ViewModelActivity;
import com.example.apidemo.activity.WindowActivity;
import com.example.apidemo.activity.XmlParseActivity;
import com.example.apidemo.adapter.DemoAdapter;
import com.example.apidemo.data.Student;
import com.example.apidemo.firebase.Usage;
import com.example.apidemo.utils.HardWareUtils;
import com.example.apidemo.utils.NLog;
import com.example.apidemo.utils.PermissionUtils;
import com.qq.TestManager;

public class MainActivity extends BaseActivity {
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listview);

        HardWareUtils.registerBluetoothListener(this);
        DoraemonKit.hide();

        List<Object> list = new ArrayList<Object>();
        list.add(DoraemonKit.class);
        // todo
        list.add(APMActivity.class);
        list.add(ScreenShotActivity.class);
        list.add(JobServiceActivity.class);
        list.add(AutoClickActivity.class);
        list.add(CoordinatorActivity.class);
        list.add(AppBarActivity.class);
        list.add(NestScrollActivity.class);
        list.add(ADActivity.class);
        list.add(DIYViewActivity.class);
        list.add(ScaleActivity.class);
        list.add(GsonActivity.class);
        list.add(GaussActivity.class);
        list.add(ResolveInfoActivity.class);
        list.add(PowerManagerActivity.class);
        list.add(EventDispatchActivity.class);
        list.add(GestureDectorActivity.class);

        list.add(SlideActivity.class);
        list.add(ScrollActivity.class);
        list.add(NewsActivity.class);
        list.add(TestServiceActivity.class);
        list.add(NotificationActivity.class);
        list.add(SQLActivity.class);
        list.add(HardWareActivity.class);

        list.add(WindowActivity.class);
        list.add(RxJavaActivity.class);
        list.add(ReferenceActivity.class);
        list.add(SDActivity.class);
        list.add(MessengerActivity.class);
        list.add(FunctionActivity.class);
//        list.add(PermissionAcitivity.class);
        list.add(GridViewActivity.class);
        list.add(ViewModelActivity.class);
        list.add(XmlParseActivity.class);
        list.add(SocketClientActivity.class);
        list.add(SocketServerActivity.class);

        DemoAdapter adapter = new DemoAdapter(MainActivity.this, list);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                NLog.d("sjh0", "onItemClick position is " + position);
                if(position == 0){
                    if(!DoraemonKit.isShow()){
                        DoraemonKit.show();
                        // firebase 统计
                        Usage.pv("event_test","test_value");
                        // Crashlytics.logException(new Exception("测试crash"));
                    }else {
                        DoraemonKit.hide();
                    }
                }else {
                    Object activityClass = parent.getItemAtPosition(position); // 根据位置判断跳转哪个activity!!!会调用adapter的getItem().
                    NLog.d("sjh0", "activity Class = " + activityClass.toString());

                    Intent intent = new Intent(MainActivity.this, (Class<?>) activityClass);
                    startActivity(intent);
                    overridePendingTransition(R.animator.slide_right_in, R.animator.slide_left_out);
                }

            }

        });

        PermissionUtils.requestForPermissions(this, PermissionUtils.PERMISSIONS);

        TestManager.getInstance().init();
        NLog.v("sjh0", "AbstractProcessor file test = " + TestManager.getInstance().findObj(new Student()));
    }


    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        NLog.d("sjh7", " main onNewIntent\n" + intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AutoClickActivity.enableAutoClickTest = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HardWareUtils.unRegisterBluetoothListener(this);
    }


}
