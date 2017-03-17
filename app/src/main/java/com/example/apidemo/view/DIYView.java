package com.example.apidemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.example.apidemo.R;
import java.io.InputStream;

/**
 * Created on 2017/3/16.
 */
public class DIYView extends View {
    private Paint mPaint;

    public DIYView(Context context) {
        super(context);
    }

    public DIYView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(0xFF0000FF); // Color.BLUE ,设置画笔颜色
        mPaint.setTextSize((float) 48.0);  //设置字体大小
        mPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mPaint.setAntiAlias(true);   // 设置画笔为无锯齿

        int statusBarHeight1 = -1;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            // 根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);    // 72
        }
        Log.e("sjh0", "statusBarHeight1 = " + statusBarHeight1);
    }

    /**
     * 注意：直接继承ImageView的自定义View设置为wrap，长宽都为0，则不触发onDraw！此时的ImageView要设置非0长和宽才能触发onDraw。
     * @param canvas 存在canvas对象，即存在默认的显示区域.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Log.e("sjh0", "onDraw");
        super.onDraw(canvas);

        canvas.drawRect(10, 10, 40, 40, mPaint);
        canvas.clipRect(0, 0, 100 , 100);// clipxx方法只对设置以后的drawxx起作用，已经画出来的图形，是不会有作用的。

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        mPaint.setColor(0xFF00FF00);
        // 这里的y是指定这个字符baseline在屏幕上的位置！而不是左上角的y！上移fontMetrics.top的位置即可！
        canvas.drawText("自定义View,canvas对象已经存在", 0, - fontMetrics.top, mPaint);

//        // Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.earth) ; // 有bug，以为是456dp。 456 * density = 1368
//        // 将res目录下的图片文件转换为bitmap对象方法一
//        InputStream inputStream = getResources().openRawResource(R.drawable.earth);
//        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
////        将res目录下的图片文件转换为bitmap对象方法二
////        InputStream inputStream = getResources().openRawResource(R.drawable.earth);
////        BitmapDrawable  bmpDraw = new BitmapDrawable(inputStream);
////        Bitmap bitmap = bmpDraw.getBitmap();
//        Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); // 456px。 是对图片进行裁截，若是空null则显示整个图片
//        Rect dstRect = new Rect(0, 0, bitmap.getWidth() / 2, bitmap.getHeight() / 2);  // 是图片在Canvas画布中显示的区域, 可拉伸图片！！!
//        Log.e("sjh0", bitmap.getWidth() + "==" + bitmap.getHeight());
//        // canvas.drawBitmap(bitmap, 0, 0, new Paint());   // 只能定义图片的起始坐标，不能改变图片显示的大小。
//        canvas.drawBitmap(bitmap, null, dstRect, mPaint);  // null即代表使用整幅图片.


////        int width = mBmp.getWidth();
////        6         int height = mBmp.getHeight();
//        8         canvas.save();
//        9         mPaint.setColor(Color.CYAN);
//        10         canvas.drawRect(0, 0, width, height, mPaint);
//        11         canvas.restore();
//        12
//        13         canvas.save();
//        14         canvas.clipRect(0, 0, width*2, height*2);
//        15         canvas.drawBitmap(mBmp, width, height, mPaint);
//        16         canvas.restore();
    }


}