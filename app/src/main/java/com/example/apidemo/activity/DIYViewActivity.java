package com.example.apidemo.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;
import java.lang.reflect.Method;


public class DIYViewActivity extends AppCompatActivity {    // BaseActivity
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_layout);

        ViewTreeObserver viewTreeObserver  = findViewById(R.id.relative).getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                //移除上一次监听，避免重复监听
                findViewById(R.id.relative).getViewTreeObserver().removeGlobalOnLayoutListener(this);
                printDeviceDimen();
            }
        });

        mImageView = (ImageView) findViewById(R.id.custom_image);
        mImageView.setRotationX(70f);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageView.setRotationX(0f);
            }
        });

//        ((ReadMoreTextView) findViewById(R.id.read_more)).setText("He called for joint efforts in implementing the consensus on strengthening " +
//        "art and cultural exchanges and cooperation in order to consolidate the foundation of goodwill of the people");

        ((Button)findViewById(R.id.btn)).setText("ReadMoreTextView setText ");
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((ReadMoreTextView) findViewById(R.id.read_more)).setText("He called for joint efforts in implementing the consensus on strengthening " +
                        "art and cultural exchanges and cooperation in order to consolidate the foundation of goodwill of the people");
            }
        });

        final ImageView image = (ImageView) findViewById(R.id.pic_skin);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NLog.i("sjh4", "width = " + image.getWidth() + " height =  " + image.getHeight());
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Button btn = (Button) findViewById(R.id.btn);
        NLog.i("sjh0", "minHeight = " + btn.getMinimumHeight() + " measuredHeight = " + btn.getMeasuredHeight()
                + " minWidth = " + btn.getMinimumWidth() + " measuredWidth = " + btn.getMeasuredWidth()
        + " getWidth = " + btn.getWidth());  //352
        // minHeight 默认是48dp（192像素），minHeight改成10dp后measuredHeight是39dp，39dp就是包裹这些文字的最小高度。
    }

    private void printDeviceDimen(){
        boolean hasMenuKey = ViewConfiguration.get(DIYViewActivity.this).hasPermanentMenuKey();    // 判断是否有物理的菜单键。即是否有导航栏。
        NLog.i("sjh0", "是否有物理的菜单键 hasMenuKey = " + hasMenuKey);

        // 状态栏高度
        int resourceId2 = getResources().getIdentifier("status_bar_height","dimen", "android");
        int statusBarHeight1 = getResources().getDimensionPixelSize(resourceId2);
        NLog.i("sjh0", "statusBarHeight1 = " + statusBarHeight1);

        // 状态栏高度(需要onMeasure之后执行)
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight2 = frame.top;
        NLog.i("sjh0", "statusBarHeight2 = " + statusBarHeight2);

        // 导航栏高度 （注意：此方法不会检查导航栏是否存在，直接返回xml中的数值。所以可能手机没有显示导航栏，但是高度依然返回）
        int resourceId = getResources().getIdentifier("navigation_bar_height","dimen", "android");
        int navigationBarHeight = getResources().getDimensionPixelSize(resourceId);
        NLog.i("sjh0", "if naticigationBar exists, navigationBarHeight = " + navigationBarHeight);

        WindowManager wm = (WindowManager) DIYViewActivity.this.getSystemService(Context.WINDOW_SERVICE);
        // 应用区域，全屏减去导航栏、状态栏(需要onMeasure之后执行才得到正确值)
        Rect outRect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        System.out.println("top:"+outRect.top +" ; left: "+outRect.left) ;
        NLog.i("sjh0", "应用区域 outRect.height() " + outRect.height());

        // 应用程序标题栏高度(需要onMeasure之后执行)
        int titleBarHeight = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        NLog.i("sjh0", "应用程序标题栏高度 titleBarHeight = " + titleBarHeight);

        // 用户绘制区域，全屏减去导航栏、状态栏、应用的标题栏(需要onMeasure之后执行)
        Rect outRect2 = new Rect();
        getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(outRect2);
        NLog.i("sjh0", "用户绘制区域 outRect.height() " + outRect2.height());

        // 有导航栏时全屏减去导航栏，无导航栏时等于全屏
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics(); // 方法1
        int mHeightPixels = displayMetrics.heightPixels;
        NLog.i("sjh0", "有导航栏时全屏减去导航栏，无导航栏时等于全屏:\n1、mHeightPixels = " + mHeightPixels);

        DisplayMetrics dm2 = new DisplayMetrics();     // 方法2!!
        wm.getDefaultDisplay().getMetrics(dm2);
        NLog.i("sjh0", "2、dm.heightPixels " + dm2.heightPixels);

        Display display = wm.getDefaultDisplay();     // 方法3,不推荐
        NLog.i("sjh0", "3、display.getHeight() " + display.getHeight());

        Display disp = getWindowManager().getDefaultDisplay();  // 方法4 activity?
        Point outP = new Point();
        disp.getSize(outP);
        NLog.i("sjh0", "4、outP.y " + outP.y);

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // 方法1：存在虚拟按键时，如何正确获取屏幕长宽
        DisplayMetrics dm4 = new DisplayMetrics();
        Display display2 = wm.getDefaultDisplay();
        Class c;
        try {
            c = Class.forName( "android.view.Display");
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display2, dm4);
        } catch (Exception e ){
            e.printStackTrace();
        }
        NLog.i("sjh0", "全屏高度：===============================================\n1、dm.heightPixels " + dm4.heightPixels);

        Point point = new Point();// 方法2
        wm.getDefaultDisplay().getRealSize(point);
        NLog.i("sjh0", "2、point.y " + point.y);

        NLog.w("sjh0", "分辨率： " + dm4.heightPixels + " * " + dm4.widthPixels + " density = " + dm4.density);
    }

}
