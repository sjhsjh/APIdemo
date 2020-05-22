package com.example.apidemo.activity;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
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
        setContentView(R.layout.test);

        // mGestureDetector = new GestureDetector(this, new MyGestureListener());
        // mGestureDetector.setOnDoubleTapListener(new MyDoubleTapListener());

        findViewById(R.id.diy_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.diy_view).requestLayout();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // return mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private class MyGestureListener implements GestureDetector.OnGestureListener{

        public boolean onDown(MotionEvent e) {
            NLog.i("sjh7", "onDown");
            Toast.makeText(GestureDectorActivity.this, "onDown", Toast.LENGTH_SHORT).show();
            return false;
        }

        /**
         * （e为down事件）onDown后TAP_TIMEOUT（100ms）内无up和move事件则触发！（注意有move则不触发）
         * 如果用户按下屏幕后没有立即松开或拖动会调用此方法,事件调用顺序为onDown--->onShowPress.
         */
        public void onShowPress(MotionEvent e) {
            NLog.i("sjh7", "onShowPress");
            Toast.makeText(GestureDectorActivity.this, "onShowPress", Toast.LENGTH_SHORT).show();
        }

        /**
         * （e为up事件）onDown后TAP_TIMEOUT + LONGPRESS_TIMEOUT（100 + 500ms）内up并且之前up之前无move则触发！！（注意有move则不触发）
         * 如果用户按下屏幕后没有移动并且立即松开会调用此方法,
         * 如果点击一下快速抬起则事件调用顺序为onDown-->onSingleTapUp-->onSingleTapUpConfirmed,
         * 如果稍微慢点点击抬起则事件调用顺序为onDown-->onShowPress-->onSingleTapUp-->onSingleTapUpConfirmed.
         */
        public boolean onSingleTapUp(MotionEvent e) {
            NLog.i("sjh7", "onSingleTapUp");
            Toast.makeText(GestureDectorActivity.this, "onSingleTapUp", Toast.LENGTH_SHORT).show();
            return true;
        }

        /**
         *  （e为down事件）onDown后TAP_TIMEOUT + LONGPRESS_TIMEOUT（100 + 500ms）内无up和move事件则触发！！（注意有move则不触发）
         *  mHandler.sendEmptyMessageAtTime(LONG_PRESS, mCurrentDownEvent.getDownTime() + TAP_TIMEOUT + LONGPRESS_TIMEOUT);
         *  如果用户按下屏幕后没有松开或拖动会调用此方法,事件调用顺序为onDown-->onShowPress-->onLongPress.
         */
        public void onLongPress(MotionEvent e) {
            NLog.i("sjh7", "onLongPress");
            Toast.makeText(GestureDectorActivity.this, "onLongPress", Toast.LENGTH_LONG).show();
        }

        /**
         * 用户按下屏幕后手指在屏幕上滑动会调用此方法,与onFling的区别是在一个滑动事件中onScroll会调用多次,参数中的后两个为滑动的距离而不是滑动的速度.
         * onScroll的四个参数分别为e1为down的event,e2为当前Event,distanceX即detaScrollX，距离上次滑动滑动的距离。
         * 事件调用顺序为onDown-->onScroll-->onScroll-->......-->onFling.
         */
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            NLog.i("sjh7", "onScroll:"+(e2.getX()-e1.getX()) +"   "+distanceX);
            return true;
        }

        /**
         * up时若速度大于指定值则触发。用户按下屏幕后手指在屏幕上快速滑动后松开会调用此方法,但是一次滑动事件"仅会调用一次"!
         * onFling()的四个参数分别为e1为down的event, e2为当前up的event,velocityX--X轴上的手指滑动速度,像素/秒。
         * 事件调用顺序为onDown-->onScroll-->onScroll-->......-->onFling.
         */
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            NLog.i("sjh7", "onFling");
            Toast.makeText(GestureDectorActivity.this, "onFling", Toast.LENGTH_LONG).show();
            return true;
        }
    }

    private class MyDoubleTapListener implements GestureDetector.OnDoubleTapListener {
        /**
         * e为down事件，注意它是down后延时触发！！因为它与onDoubleTap是互斥的，因此使用于判断双击动作的时候，而onSingleTapUp则是up触发，更广泛使用。
         * down后DOUBLE_TAP_TIMEOUT（300ms）时判断是否已经up，若是则触发，否则不触发。
         * 当用户按下屏幕后并快速抬起时调用并在之后短时间没有二次点击,主要是用来确认用户该次点击是onSingleTap而不是onDoubleTap.
         * 事件调用顺序为:onDownn-->onSingleTap-->onSingleTapConfirmed.
         */
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            NLog.w("sjh7", "onSingleTapConfirmed. 300ms after down");
            return false;
        }

        /**
         * e为down事件，注意它是down触发！当用户双击屏幕时调用,onDoubleTap与onSingleTapConfirmed的触发是互斥的（一个是down后300ms内触发，一个是300ms时触发）.
         * 如果两次down相差DOUBLE_TAP_TIMEOUT（300ms）以内且down的位置相差100像素以内则触发。
         * 该事件调用顺序为:ondown-->onSingleTap-->onDoubleTap().
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            NLog.w("sjh7", "onDoubleTap " + e.getAction());
            return false;
        }

        /*
         * 从双击动作的第2个down开始，到第2个up之间所有的down,up和move事件！
         * 当用户双击屏幕间,触发的触摸事件,包含down,up和move事件,事件封装在参数MotionEvent中.
         */
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            NLog.w("sjh7", "onDoubleTapEvent " + e.getAction());
            return false;
        }

    }
}