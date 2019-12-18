package com.example.apidemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;

/**
 * <br> 桌面添加小window
 * Created by jinhui.shao on 2017/10/31.
 */
public class WindowActivity extends BaseActivity {
    private static final int OVERLAY_REQUEST_CODE = 11;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams layoutParams;
    private LinearLayout linearLayout;
    private int downOffsetX, downOffsetY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);
        initWindow();

        ((Button)findViewById(R.id.button1)).setText("open overlay permission");
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_REQUEST_CODE);
            }
        });
        ((Button)findViewById(R.id.button2)).setText("add window");
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // mWindowManager.removeView(mWindowBtn);  // mWindowManager先removeView报错!!!
                if(linearLayout.getParent() == null){
                    mWindowManager.addView(linearLayout, layoutParams);
                }

            }
        });
        ((Button)findViewById(R.id.button3)).setText("remove window");
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(linearLayout.getParent() != null){   // Activity退出后成员变量linearLayout就访问不到了，导致不能removeView了！！
                    mWindowManager.removeView(linearLayout);
                }
            }
        });
    }

    private void initWindow(){
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        linearLayout = new LinearLayout(this);
        Button windowBtn = new Button(this);  // Button竟然自带padding！！mWindowBtn.setPadding(0, 0, 0, 0);则出现文字拉长但是灰色按钮背景不变长。
        windowBtn.setText("windowBtn");
        windowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WindowActivity.this,"window btn click ", Toast.LENGTH_SHORT).show();
            }
        });
        Button dismissBtn = new Button(this);
        dismissBtn.setText("dismissBtn");
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindowManager.removeView(linearLayout);
            }
        });

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setMinimumWidth(600);
        linearLayout.setMinimumHeight(800);
        linearLayout.setBackgroundColor(getResources().getColor(R.color.flashlight_color));
        linearLayout.addView(windowBtn);
        linearLayout.addView(dismissBtn);
        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                switch(action){
                    case MotionEvent.ACTION_DOWN:
                        downOffsetX = (int) event.getRawX() - layoutParams.x;
                        downOffsetY = (int) event.getRawY() - layoutParams.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        layoutParams.x = (int) event.getRawX() - downOffsetX;
                        layoutParams.y = (int) event.getRawY() - downOffsetY;
                        mWindowManager.updateViewLayout(linearLayout, layoutParams);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        layoutParams = new WindowManager.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR, // WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                PixelFormat.TRANSPARENT);
        // FLAG_NOT_TOUCH_MODAL把该window区域外的事件交还给其他Window处理;一般来说，都需要开启此标记，否则其它 WIndow 将无法接收到单击事件.
        // 不设置FLAG_NOT_FOCUSABLE返回键无效了;设置FLAG_NOT_FOCUSABLE了里面的button也能响应;？？
        // FLAG_NOT_FOCUSABLE下，Window 不会获取焦点，此标记会同时启用 FLAG_NOT_TOUCH_MODAL，注释上虽然说该模式下不会接收触摸事件，但是实验证明，它还是可以接收到触摸事件的.
        // FLAG_NOT_TOUCHABLE模式下，Window 不会接收任何的触摸事件，它会将触摸事件传递给下层的具有焦点的 Window
        // 设置TYPE_APPLICATION + FLAG_SHOW_WHEN_LOCKED且无overlay权限时window所在的activity也显示在锁屏上了
        // TYPE_SYSTEM_ERROR + overlay权限即可在锁屏上显示window；
        // FLAG_NOT_TOUCHABLE	窗口不接受任何触摸事件。 FLAG_NOT_FOCUSABLE貌似会不响应返回键+整个窗口接收事件。貌似不能窗口部分区域接收事件其他区域交给其他window处理。
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.y = 100;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        NLog.d("sjh7", "onActivityResult: requestCode = " + requestCode +  ", resultCode = " + resultCode);
        // Activity.RESULT_OK = -1;     RESULT_CANCELED = 0; // 按返回键都为cancel
        if (requestCode == OVERLAY_REQUEST_CODE) {
            //再次判断“悬浮权限”
            if (Build.VERSION.SDK_INT >= 23) {
                if (Settings.canDrawOverlays(this)) {
                    NLog.i("sjh7", "overlay permission:  true");
                }
            }
        }

    }




}