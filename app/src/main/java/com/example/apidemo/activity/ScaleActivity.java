package com.example.apidemo.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.Toast;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;

/**
 * 自行实现photoView
 * GestureDetector：单击、双击、长按
 * 自行判断：拖动drag、滑动fling
 * ScaleGestureDetector：伸缩scale
 *
 * photoview特性：
 * 在滑动父控件下能够运行良好；（例如：ViewPager）
 * 关键点其实就是 Touch 事件处理和 Matrix 图形变换的应用.
 * mBaseMatrix用来保存根据ScaleType调整过的的原始矩阵。
 * mDrawMatrix其实是在mBaseMatrix基础矩阵上后乘mSuppMatrix供应矩阵产生的。
 * photoview内的ImageView的ScaleType已被固定为Matrix，因此通过设置PhotoViewAttacher内的setScaleType来模拟原ImageView scaleType的效果，以满足实际需求。
 * Created by Administrator on 2019/4/7.
 */
public class ScaleActivity extends BaseActivity {
    private static final int SIXTY_FPS_INTERVAL = 1000 / 60;
    private static float DEFAULT_MAX_SCALE = 3.0f;
    private static float DEFAULT_MID_SCALE = 1.75f;
    private static float DEFAULT_MIN_SCALE = 0.5f;
    private static int DEFAULT_ZOOM_DURATION = 200;

    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private Matrix matrix = new Matrix();
    private ImageView imageView;
    private final RectF mDisplayRect = new RectF();
    private float mLastX = 0f;
    private float mLastY = 0f;
    private int mTouchSlop;
    private int mMaxVelocity;
    private int mMinVelocity;
    private boolean mIsDragging;
    private Handler handler;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale);

        mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();  // 21(32)
        mMaxVelocity = ViewConfiguration.get(this).getScaledMaximumFlingVelocity();     // 21000(32000)
        mMinVelocity = ViewConfiguration.get(this).getScaledMinimumFlingVelocity();     // 131(200)

        imageView = findViewById(R.id.image);
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        handler = new Handler();
        mScroller = new Scroller(this);

        initOriginScaleType();

        scaleGestureDetector = new ScaleGestureDetector(this, new MySimpleOnScaleGestureListener(new ScaleListener() {
            @Override
            public void onScale(ScaleGestureDetector detector) {
                NLog.i("sjh1", " getScale() " + getScale() + " imageView.getScaleX = " + imageView.getScaleX());    // imageView的scale一直是1
//                if (getScale() >= DEFAULT_MIN_SCALE && getScale() <= DEFAULT_MAX_SCALE) {
                    // 开始缩放
                    /************************************************ scale ************************************************************/
                    matrix.postScale(detector.getScaleFactor(), detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
                    imageView.setImageMatrix(matrix);
                    /************************************************ scale ************************************************************/
//                }
            }
        }));

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Toast.makeText(ScaleActivity.this, "onLongPress", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }

            public boolean onSingleTapConfirmed(MotionEvent e) {
                Toast.makeText(ScaleActivity.this, "onSingleTapConfirmed", Toast.LENGTH_SHORT).show();
                return true;
            }

            public boolean onDoubleTap(MotionEvent e) {
                Toast.makeText(ScaleActivity.this, "onDoubleTap", Toast.LENGTH_SHORT).show();

                float targetScale = 1;
                if (getScale() >= 1 && getScale() < DEFAULT_MID_SCALE) {
                    targetScale = DEFAULT_MID_SCALE;
                } else if (getScale() >= DEFAULT_MID_SCALE && getScale() < DEFAULT_MAX_SCALE) {
                    targetScale = DEFAULT_MAX_SCALE;
                } else if (getScale() > DEFAULT_MAX_SCALE) {
                    targetScale = 1;
                }
                animateZoom(targetScale, e.getX(), e.getY());
                return true;
            }

        });
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                NLog.w("sjh2", " event.getAction() " + event.getAction());

                boolean handled = false;
                velocityTrackerAddMovement(event);

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN :
                        mLastX = event.getX();
                        mLastY = event.getY();
                        mIsDragging = false;
                        mScroller.forceFinished(true);

                        break;
                    case MotionEvent.ACTION_MOVE :
                        float dx = event.getX() - mLastX;
                        float dy = event.getY() - mLastY;
                        NLog.v("sjh2", " event.getX() " + event.getX());
                        /************************************************ drag ************************************************************/
                        if (!mIsDragging) {
                            if (Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop) {
                                mIsDragging = true;
                            }
                        }
                        if (mIsDragging) {
                            matrix.postTranslate(dx, dy);
                            imageView.setImageMatrix(matrix);
                            updateMatrixBound();

                            mLastX = event.getX();
                            mLastY = event.getY();
                        }
                        /************************************************ drag ************************************************************/
                        break;
                    case MotionEvent.ACTION_CANCEL :
                    case MotionEvent.ACTION_UP :
                        /************************************************ fling ************************************************************/
                        mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                        float xVelocity = mVelocityTracker.getXVelocity();
                        float yVelocity = mVelocityTracker.getYVelocity();

                        if (Math.abs(xVelocity) > mMinVelocity || Math.abs(yVelocity) > mMinVelocity) {
                            fling(getImageViewWidth(imageView), getImageViewHeight(imageView), -(int)xVelocity, -(int)yVelocity);    // 注意scroller向左 和向上为正方向！！！
                        }
                        releaseVelocityTracker();
                        /************************************************ fling ************************************************************/
                        // 过度伸缩后，up时回到最大最小值处
                        RectF rect = getDisplayRect(matrix);
                        if (getScale() > DEFAULT_MAX_SCALE) {
                            animateZoom(DEFAULT_MAX_SCALE, imageView.getLeft() + imageView.getWidth() / 2, imageView.getTop() + imageView.getHeight() / 2); // 以view的中心来缩放
//                            animateZoom(DEFAULT_MAX_SCALE, rect.centerX(), rect.centerY());
//                            animateZoom(DEFAULT_MAX_SCALE, event.getX(), event.getY());   // xx。 up时的单个坐标点对伸缩无意义
                        } else if (getScale() < 1) {
//                            animateZoom(1, rect.centerX(), rect.centerY());
                            animateZoom(1, imageView.getLeft() + imageView.getWidth() / 2, imageView.getTop() + imageView.getHeight() / 2);
                        }
                        break;
                }

                handled = scaleGestureDetector.onTouchEvent(event);
                handled = gestureDetector.onTouchEvent(event) || handled;

                return handled;
            }
        });
//        imageView.setOnClickListener(new View.OnClickListener() {   // onTouch已返回true
//            @Override
//            public void onClick(View v) {
//                matrix.set(imageView.getImageMatrix());
//                matrix.postTranslate(300, 300);
//                imageView.setImageMatrix(matrix);
//            }
//        });

    }

    private float baseScale = 1;    // 使用setRectToRect后图片变成的scale
    /**
     * imageView的ScaleType已被设置成matrix，因此使用setRectToRect的方法来实现原本FIT_CENTER的效果。
     */
    private void initOriginScaleType() {
        final ViewTreeObserver observer = imageView.getViewTreeObserver();
        if (null != observer) {
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Drawable drawable = imageView.getDrawable();
                    if (drawable != null) {
                        RectF mTempSrc = new RectF(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                        RectF mTempDst = new RectF(0, 0, getImageViewWidth(imageView), getImageViewHeight(imageView));
                        matrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.CENTER);    // FIT_CENTER
                        imageView.setImageMatrix(matrix);

                        baseScale = getActuralScale();
                    }
                    imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
    }

    private void animateZoom(float targetScale, final float focusX, final float focusY) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "abc", getScale(), targetScale);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                matrix.setScale((float) animation.getAnimatedValue(), (float) animation.getAnimatedValue(), focusX, focusY);  // xx
                matrix.postScale((float) animation.getAnimatedValue() / getScale(), (float) animation.getAnimatedValue() / getScale(), focusX, focusY);
                NLog.e("sjh1", "===" + animation.getAnimatedValue());
                imageView.setImageMatrix(matrix);
                updateMatrixBound();
            }
        });
        animator.setDuration(DEFAULT_ZOOM_DURATION);
        animator.start();
    }

    /**
     *  matrix.postTranslate 平移回到正常显示的位置
     */
    private void updateMatrixBound(){
        RectF rectF = getDisplayRect(matrix);
        if (rectF != null) {
            // 水平方向
            // 两侧有空隙时怎么拖动都平移回居中位置
            if(rectF.width() < getImageViewWidth(imageView)){
                matrix.postTranslate((getImageViewWidth(imageView) - rectF.width()) / 2 - rectF.left, 0);
            }
            // 若拖动处空隙则平移回去！！
            else if(rectF.left > 0){
                matrix.postTranslate(-rectF.left, 0);
            }
            else if(rectF.right < getImageViewWidth(imageView)){
                matrix.postTranslate(getImageViewWidth(imageView) - rectF.right, 0);
            }

            // 竖直方向
            if(rectF.height() < getImageViewHeight(imageView)){
                matrix.postTranslate(0, (getImageViewHeight(imageView) - rectF.height()) / 2 - rectF.top);
            }
            else if(rectF.top > 0){
                matrix.postTranslate(0, -rectF.top);
            }
            else if(rectF.bottom < getImageViewHeight(imageView)){
                matrix.postTranslate(0, getImageViewHeight(imageView) - rectF.bottom);
            }

            imageView.setImageMatrix(matrix);
        }
    }

    private Scroller mScroller;
    private int mLastScrollerX, mLastScrollerY;
    private void fling(int viewWidth, int viewHeight, int xVelocity, int yVelocity) {
        RectF rectF = getDisplayRect(matrix);
        if (rectF == null) {
            return;
        }
        // 水平方向
        int startX = -(int)rectF.left;
        int minX, maxX;
        if(rectF.width() > viewWidth){
            minX = 0;
            maxX = (int) (rectF.width() - viewWidth);
        }else {
            minX = maxX = startX;   // 图片rect小的时候，该方向就设置成不能移动
        }
        // 竖直方向
        int startY = -(int)rectF.top;
        int minY, maxY;
        if(rectF.height() > viewHeight){
            minY = 0;
            maxY = (int) (rectF.height() - viewHeight);
        }else {
            minY = maxY = startY;// 图片rect小的时候，边界值minY 和 maxY不能是变化的值，否则一直fling到新的边界值
        }

        // 绘制开始时先瞬间回到startX，startY! 限制了边界为范围是(minY,  maxY)
        mScroller.fling(startX, startY, xVelocity, yVelocity, minX, maxX, minY, maxY);  // 本来需要需重载view的computeScroll来使用（draw中调用computeScroll），此处使用循环的handler内移动图片
        mLastScrollerX = startX;    // 初始值并不是mScroller.getCurrX()！！！getCurrX初始值是0.
        mLastScrollerY = startY;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mScroller.computeScrollOffset()) {  // 返回值为boolean，true说明滚动尚未完成，false说明滚动已经完成。
                    matrix.postTranslate(mLastScrollerX - mScroller.getCurrX(), mLastScrollerY - mScroller.getCurrY());
                    imageView.setImageMatrix(matrix);

                    mLastScrollerX = mScroller.getCurrX();
                    mLastScrollerY = mScroller.getCurrY();
                    handler.postDelayed(this, SIXTY_FPS_INTERVAL);
                }
            }
        }, SIXTY_FPS_INTERVAL);

    }

    public float getActuralScale() {
        return (float) Math.sqrt((float) Math.pow(getValue(matrix, Matrix.MSCALE_X), 2) + (float) Math.pow(getValue(matrix, Matrix.MSKEW_Y), 2));
    }

    /**
     * 根据当前的矩阵算出相对于初始状态的scale！
     */
    public float getScale() {
        return (float) Math.sqrt((float) Math.pow(getValue(matrix, Matrix.MSCALE_X), 2) + (float) Math.pow(getValue(matrix, Matrix.MSKEW_Y), 2)) / baseScale;
    }

    private final float[] mMatrixValues = new float[9];
    /**
     * Helper method that 'unpacks' a Matrix and returns the required value
     * @param matrix     Matrix to unpack
     * @param whichValue Which value from Matrix.M* to return
     * @return returned value
     */
    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    /**
     * Helper method that maps the supplied Matrix to the current Drawable
     *
     * @param matrix - Matrix to map Drawable against
     * @return RectF - Displayed Rectangle
     */
    private RectF getDisplayRect(Matrix matrix) {
        Drawable d = imageView.getDrawable();
        if (d != null) {
            mDisplayRect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(mDisplayRect);
            return mDisplayRect;
        }
        return null;
    }

    private int getImageViewWidth(ImageView imageView) {
        return imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
    }

    private int getImageViewHeight(ImageView imageView) {
        return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
    }

    private VelocityTracker mVelocityTracker;
    private void velocityTrackerAddMovement(MotionEvent event){
        if(mVelocityTracker == null){
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker(){
        if(mVelocityTracker != null){
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);

    }


//    float getScaleFactor()： 它的返回值是指本次事件中的缩放值，并不是相对于最开始的值。
//    返回从之前的缩放手势和当前的缩放手势两者的比例因子，即缩放值，默认1.0。
//    在"每一个缩放事件中都会从1.0开始变化"，如果需要做持续性操作，则需要保存上一次的伸缩值，然后当前的伸缩值*上一次的伸缩值，得到连续变化的伸缩值，
//    float getFocusX()、 getFocusY()
//    返回当前缩放手势的焦点X坐标，焦点即两手指的中心点。
//    float getCurrentSpan()
//    返回每个缩放手势的两个手指之间的距离值。

    public static class MySimpleOnScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        private ScaleListener mListener;

        public MySimpleOnScaleGestureListener(ScaleListener listener) {
            mListener = listener;
        }

        /**
         * onScaleBegin 返回true之后在move时触发。
         * 返回true代表本次缩放事件是否已被处理。
         * 如果已被处理，那么detector就会重置缩放事件，且缩放因子将会重置为为1.0！
         * 如果未被处理，detector会继续进行计算，修改getScaleFactor()的返回值，直到被处理为止。
         * 返回值可以用来限制缩放值的最大比例上限和最小比例下限
         */
        public boolean onScale(ScaleGestureDetector detector) {
            NLog.d("sjh1", "onScale:  factor = " + detector.getScaleFactor() + "  currentSpan = " + detector.getCurrentSpan()
                    + "  focusX = " + detector.getFocusX() + "  focusY = " + detector.getFocusY()
            );
            if (mListener != null) {
                mListener.onScale(detector);
            }
            return true;
        }

        /**
         * move达到最小距离时触发
         * 返回true代表从此刻开始接受事件，之后才会在move时回调onScale。
         */
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            NLog.w("sjh1", "onScaleBegin:  factor = " + detector.getScaleFactor() + "  currentSpan = " + detector.getCurrentSpan()
                    + "  focusX = " + detector.getFocusX() + "  focusY = " + detector.getFocusY()
            );
            return true;
        }

        /**
         * up和cancel时触发
         */
        public void onScaleEnd(ScaleGestureDetector detector) { // 有限制最小值450？？
            NLog.w("sjh1", "onScaleEnd:  factor = " + detector.getScaleFactor() + "  currentSpan = " + detector.getCurrentSpan()
                    + "  focusX = " + detector.getFocusX() + "  focusY = " + detector.getFocusY()
            );
            // Intentionally empty
        }

    }


    private interface ScaleListener{
        void onScale(ScaleGestureDetector detector);
    }

}
