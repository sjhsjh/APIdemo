package com.example.apidemo.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.utils.NLog;

/**
 * <br>
 * Created by jinhui.shao on 2017/9/25.
 */
public class GestureDectorActivity extends BaseActivity{
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGestureDetector = new GestureDetector(this, new MyGestureListener());
        mGestureDetector.setOnDoubleTapListener(new MyDoubleTapListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
        // super.onTouchEvent(event);
    }

    private class MyGestureListener implements GestureDetector.OnGestureListener{

        public boolean onDown(MotionEvent e) {
            NLog.i("sjh7", "onDown");
            Toast.makeText(GestureDectorActivity.this, "onDown", Toast.LENGTH_SHORT).show();
            return false;
        }

        /*    
         * 用户轻触触摸屏，尚未松开或拖动，由一个1个MotionEvent ACTION_DOWN触发    
         * 注意和onDown()的区别，强调的是没有松开或者拖动的状态    
         *   
         * 而onDown也是由一个MotionEventACTION_DOWN触发的，但是他没有任何限制，  
         * 也就是说当用户点击的时候，首先MotionEventACTION_DOWN，onDown就会执行，
         * 如果在按下的瞬间没有松开或者是拖动的时候onShowPress就会执行.
         */
        public void onShowPress(MotionEvent e) {
            NLog.i("sjh7", "onShowPress");
            Toast.makeText(GestureDectorActivity.this, "onShowPress", Toast.LENGTH_SHORT).show();
        }

        // 用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发       
        ///轻击一下屏幕，立刻抬起来，才会有这个触发    
        //从名子也可以看出,一次单独的轻击抬起操作,当然,如果除了Down以外还有其它操作,那就不再算是Single操作了,所以这个事件 就不再响应    
        public boolean onSingleTapUp(MotionEvent e) {
            NLog.i("sjh7", "onSingleTapUp");
            Toast.makeText(GestureDectorActivity.this, "onSingleTapUp", Toast.LENGTH_SHORT).show();
            return true;
        }

        /**
         *  mHandler.sendEmptyMessageAtTime(LONG_PRESS, mCurrentDownEvent.getDownTime() + TAP_TIMEOUT + LONGPRESS_TIMEOUT);
         */
        public void onLongPress(MotionEvent e) {
            NLog.i("sjh7", "onLongPress");
            Toast.makeText(GestureDectorActivity.this, "onLongPress", Toast.LENGTH_LONG).show();
        }

        // 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            NLog.i("sjh7", "onScroll:"+(e2.getX()-e1.getX()) +"   "+distanceX);
            // Toast.makeText(GestureDectorActivity.this, "onScroll", Toast.LENGTH_LONG).show();
            return true;
        }

        // 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE, 1个ACTION_UP触发       
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            NLog.i("sjh7", "onFling");
            Toast.makeText(GestureDectorActivity.this, "onFling", Toast.LENGTH_LONG).show();
            return true;
        }
    }

    private class MyDoubleTapListener implements GestureDetector.OnDoubleTapListener {
        /*
         * 用来判定该次点击是SingleTap而不是DoubleTap，如果连续点击两次就是DoubleTap手势，如果只点击一次，
         * 系统等待一段时间后没有收到第二次点击则判定该次点击为SingleTap而不是DoubleTap,
         * 然后触发SingleTapConfirmed事件。
         */
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            NLog.w("sjh7", "onSingleTapConfirmed");
            return false;
        }

        /*双击事件
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            NLog.w("sjh7", "onDoubleTap " + e.getAction());
            return false;
        }

        /*
         * 双击间隔中发生的动作。指触发onDoubleTap以后，在双击之间发生的其它动作，包含down、up和move事件
         * @MotionEvent e中包含了双击直接发生的其他动作.
         */
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            NLog.w("sjh7", "onDoubleTapEvent " + e.getAction());
            return false;
        }

    }
}