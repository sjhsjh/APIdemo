package com.example.apidemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;
import java.io.InputStream;

/**
 * Created on 2017/3/16.
 */
public class DIYView extends View {
    private static final boolean DEBUG = false;
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

        int statusBarHeight = -1;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            // 根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);    // 72
        }
        NLog.i("sjh0", "statusBarHeight = " + statusBarHeight);
    }

    /**
     * 注意：直接继承ImageView的自定义View设置为wrap，长宽都为0，则不触发onDraw！此时的ImageView要设置非0长和宽才能触发onDraw。
     * @param canvas 存在canvas对象，即存在默认的显示区域.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        NLog.i("sjh0", "onDraw");
        super.onDraw(canvas);

        canvas.drawRect(10, 10, 40, 40, mPaint);
        canvas.save();
        canvas.rotate(30);
        canvas.clipRect(0, 0, 300 , 300);// clipxx方法只对设置以后的drawxx起作用，已经画出来的图形，是不会有作用的。

        mPaint.setColor(0xFF00FF00);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        // 这里的y是指定这个字符baseline在屏幕上的位置！而不是左上角的y！上移fontMetrics.top的位置即可！
        canvas.drawText("旋转文字", 0, - fontMetrics.top, mPaint);

        canvas.restore();
        mPaint.setColor(0xFF0000FF);
        canvas.drawText("restore之后恢复了正常的角度和显示区域", 100, 100 - fontMetrics.top, mPaint);

        Path path3 = new Path();
        path3.moveTo(90, 340);
        path3.lineTo(150, 340);
        path3.lineTo(120, 290);
        path3.close();  // 将路径闭合
        mPaint.setStyle(Paint.Style.STROKE); // 设置空心. 填充风格：Paint.Style.FILL
        mPaint.setStrokeWidth(10);      // 默认头发线
        canvas.drawPath(path3, mPaint); // 绘制三角形
        // canvas.drawTextOnPath(文字, 文字的baseline对齐path, x偏移, y偏移, 画笔); // 沿着线写字


//        // Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.earth) ; //xx 有bug，以为是456dp。 456 * density = 1368
//        // 将res目录下的图片文件转换为bitmap对象方法一
//        InputStream inputStream = getResources().openRawResource(R.drawable.earth);
//        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
////        将res目录下的图片文件转换为bitmap对象方法二
////        InputStream inputStream = getResources().openRawResource(R.drawable.earth);
////        BitmapDrawable  bmpDraw = new BitmapDrawable(inputStream);
////        Bitmap bitmap = bmpDraw.getBitmap();
//        Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); // 456px。 是对图片进行裁截，若是空null则显示整个图片
//        Rect dstRect = new Rect(0, 0, bitmap.getWidth() / 2, bitmap.getHeight() / 2);  // 是图片在Canvas画布中显示的区域, 可拉伸图片！！!
//       NLog.e("sjh0", bitmap.getWidth() + "==" + bitmap.getHeight());
//        // canvas.drawBitmap(bitmap, 0, 0, new Paint());   // 只能定义图片的起始坐标，不能改变图片显示的大小。
//        canvas.drawBitmap(bitmap, null, dstRect, mPaint);  // null即代表使用整幅图片.


    }


}