package com.example.apidemo.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import com.blankj.utilcode.util.SizeUtils;
import com.example.apidemo.R;

/**
 * 简易弧形进度条
 */
public class ArcProgressBar extends View {
    private static final String TAG = "ArcProgressBar";

    /**
     * 圆弧开始的角度。3点钟方向为起始方向
     */
    private float mStartAngle = 0;
    /**
     * 圆弧夹角的大小。顺时针方向画圆弧
     */
    private float mSweepAngle = 0;
    /**
     * 当前进度0~100
     */
    private int mCurrentProgress = 0;
    /**
     * 一圈的动画执行时间
     */
    private long mDuration = 3000;
    /**
     * 圆弧的宽度
     */
    private int mStrokeWidth = SizeUtils.dp2px(4);
    /**
     * 圆弧进度条背景颜色
     */
    private int mBgColor = Color.parseColor("#65ffffff");
    /**
     * 圆弧进度条的颜色
     */
    private int mProgressColor = Color.parseColor("#FCC968");

    private boolean needDrawBg = true;

    public ArcProgressBar(Context context) {
        this(context, null);
    }

    public ArcProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressBar);
        mStrokeWidth = (int) array.getDimension(R.styleable.ArcProgressBar_stroke_width, SizeUtils.dp2px(4));
        mProgressColor = array.getColor(R.styleable.ArcProgressBar_progress_color, Color.parseColor("#FCC968"));
        mBgColor = array.getColor(R.styleable.ArcProgressBar_bg_color, Color.parseColor("#65ffffff"));
        mStartAngle = array.getFloat(R.styleable.ArcProgressBar_start_angle, 0f);
        needDrawBg = array.getBoolean(R.styleable.ArcProgressBar_need_draw_bg, true);
        mDuration = array.getInt(R.styleable.ArcProgressBar_duration, 3000);
        mCurrentProgress = array.getInt(R.styleable.ArcProgressBar_barProgress, 0);
        mSweepAngle = mCurrentProgress / 100f * 360;
        array.recycle();

        initArcPaint();
        if (needDrawBg) {
            initBgPaint();
        }
    }

    private RectF mRectF = new RectF();
    // 圆弧的paint
    private Paint mArcPaint;
    // 圆弧背景的paint
    private Paint mBgPaint;

    private void initArcPaint() {
        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setColor(mProgressColor);
        // 设置画笔的画出的形状
        mArcPaint.setStrokeJoin(Paint.Join.ROUND);  // 多线条连接拐角弧度
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);    // 线条两端点
        // 设置画笔类型
        mArcPaint.setStyle(Paint.Style.STROKE);
        // 圆弧的宽度
        mArcPaint.setStrokeWidth(mStrokeWidth);
    }

    private void initBgPaint() {
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(mBgColor);
        mBgPaint.setStrokeJoin(Paint.Join.ROUND);
        mBgPaint.setStrokeCap(Paint.Cap.ROUND);
        mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setStrokeWidth(mStrokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mRectF.left = mStrokeWidth / 2;
        mRectF.top = mStrokeWidth / 2;
        mRectF.right = getWidth() - mStrokeWidth / 2;
        mRectF.bottom = getHeight() - mStrokeWidth / 2;

        // mRectF :圆弧一半厚度的外轮廓矩形区域。
        // startAngle: 圆弧起始角度，单位为度。3点钟方向为起始方向！
        // sweepAngle: 圆弧扫过的角度，单位为度。顺时针方向！
        // useCenter: 如果为true时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形。

        // 画最背景圆弧
        if (needDrawBg) {
            canvas.drawArc(mRectF, 0, 360, false, mBgPaint);
        }

        // 画进度圆弧
        canvas.drawArc(mRectF, mStartAngle, mSweepAngle, false, mArcPaint);
    }

    /**
     * 设置进度圆弧的颜色
     */
    public void setProgressColor(int color) {
        if (color == 0) {
            throw new IllegalArgumentException("Color can no be 0");
        }
        mProgressColor = color;
    }

    /**
     * 设置圆弧的颜色
     */
    public void setBgColor(int color) {
        if (color == 0) {
            throw new IllegalArgumentException("Color can no be 0");
        }
        mBgColor = color;
    }

    /**
     * 设置圆弧的宽度
     *
     * @param strokeWidth px
     */
    public void setStrokeWidth(int strokeWidth) {
        if (strokeWidth < 0) {
            throw new IllegalArgumentException("strokeWidth value can not be less than 0");
        }
        mStrokeWidth = strokeWidth;
    }

    /**
     * 设置动画的执行时长
     *
     * @param duration
     */
    public void setAnimatorDuration(long duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("Duration value can not be less than 0");
        }
        mDuration = duration;
    }

    /**
     * 设置圆弧开始的角度
     *
     * @param startAngle
     */
    public void setStartAngle(int startAngle) {
        mStartAngle = startAngle;
    }

    /**
     * 设置进度条进度
     *
     * @param progress 0~100
     */
    public void setProgress(int progress, boolean withAnimation) {
        if (progress < 0) {
            throw new IllegalArgumentException("Progress value can not be less than 0");
        }
        if (progress > 100) {
            progress = 100;
        }
        if (progress == mCurrentProgress) {
            return;
        }

        if (withAnimation) {
            startAnimation(progress);
        } else {
            mCurrentProgress = progress;
            mSweepAngle = progress / 100f * 360;
            invalidate();
        }
    }

    public int getProgress() {
        return mCurrentProgress;
    }

    private ValueAnimator valueAnimator;

    /**
     * 设置动画
     */
    private void startAnimation(int targetProgress) {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
        valueAnimator = ValueAnimator.ofFloat(mCurrentProgress, targetProgress);
        long duration = (long) ((targetProgress - mCurrentProgress) / 100f * mDuration);
        valueAnimator.setDuration(duration > 0 ? duration : 10);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurrentProgress = ((Float) valueAnimator.getAnimatedValue()).intValue();
                mSweepAngle = mCurrentProgress / 100f * 360;
                invalidate();
            }
        });
        valueAnimator.start();
    }
}