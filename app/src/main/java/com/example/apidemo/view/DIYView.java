package com.example.apidemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;
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
        mPaint.setColor(0xFFFF0000); // Color.RED ,设置画笔颜色
        mPaint.setTextSize((float) 48.0);  //设置字体大小
        mPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL)); // BOLD
      //  mPaint.setAntiAlias(true);   // 设置画笔为无锯齿
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        NLog.w("sjh5", "DIYView event = " + event + "\nisFocused = " + isFocused() + " hasFocus = " + hasFocus());
        boolean consume = super.dispatchKeyEvent(event);  // 默认false
        NLog.w("sjh5", "DIYView consume = " + consume);
        return consume;
    }

    /**
     * 注意：直接继承ImageView的自定义View设置为wrap，长宽都为0，则不触发onDraw！此时的ImageView要设置非0长和宽才能触发onDraw。
     * @param canvas 存在canvas对象，即存在默认的显示区域.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        NLog.i("sjh0", "onDraw");
        super.onDraw(canvas);
        // onDraw会多次触发！！若没设置线宽、画笔颜色等，则这里的画笔会使用onDraw方法结束时mPaint的画笔颜色、线宽粗细等！！！！！因此画东西前先设置好画笔。
        // 画长方形
        // mPaint.setColor(0xFFFF0000);
        canvas.drawRect(200, 200, 240, 240, mPaint);
        canvas.save();  // 保存当前画布的各种状态
        // 该方法用于裁剪画布，也就是设置画布的显示区域 ，调用clipRect()方法后，只会显示被裁剪的区域，之外的区域将不会显示
        // clipxx方法只对设置以后的drawxx起作用，已经画出来的图形，是不会有作用的。
        canvas.clipRect(0, 0, 300, 300);
//        canvas.drawColor(Color.RED);  // 画布涂色

        // 画旋转文字
        mPaint.setColor(0xFF00FF00);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        // 这里的y是指定这个字符baseline在屏幕上的位置！而不是左上角的y！上移fontMetrics.top的位置即可！
        canvas.rotate(30);
        canvas.drawText("旋转文字", 0, 100 - fontMetrics.top, mPaint);

        // 画正常文字
        canvas.restore();
        mPaint.setColor(0xFF0000FF);
        canvas.drawText("restore之后恢复了正常的角度、显示区域和scale", 100, 100 - fontMetrics.top, mPaint);
        // canvas.drawTextOnPath(文字, 文字的baseline对齐path, x偏移, y偏移, 画笔); // 沿着线写字

        // 绘制三角形
        Path path3 = new Path();
        path3.moveTo(90, 340);
        path3.lineTo(150, 340);
        path3.lineTo(120, 290);
        path3.close();  // 将路径闭合
        mPaint.setStyle(Paint.Style.STROKE); // 设置空心. 填充风格：Paint.Style.FILL
        mPaint.setStrokeWidth(5);      // 默认头发线，设置线宽粗细。
        canvas.drawPath(path3, mPaint); // 绘制三角形



        // 画图片
        // Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.earth) ; //xx 有bug，以为是456dp。 456 * density = 1368
        // 将res目录下的图片文件转换为bitmap对象方法一
        InputStream inputStream = getResources().openRawResource(R.drawable.wechat_share_img);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//        将res目录下的图片文件转换为bitmap对象方法二
//        InputStream inputStream = getResources().openRawResource(R.drawable.earth);
//        BitmapDrawable  bmpDraw = new BitmapDrawable(inputStream);
//        Bitmap bitmap = bmpDraw.getBitmap();

        // 200px。 是对图片进行裁截，若是空null则显示整个图片
        Rect srcRect = new Rect(0, 0, bitmap.getWidth() / 1, bitmap.getHeight() / 1);
        // 是图片在Canvas画布中显示的区域, 可拉伸图片！！即在imageview中显示的坐标区域！
        Rect dstRect = new Rect(00, 00, bitmap.getWidth() / 2 , bitmap.getHeight() / 2);
        NLog.e("sjh0", "bitmap.getWidth = " + bitmap.getWidth() + "bitmap.getHeight() = " + bitmap.getHeight());
        // canvas.drawBitmap(bitmap, 0, 0, new Paint());   // 只能定义图片的起始坐标，不能改变图片显示的大小。
        canvas.drawBitmap(bitmap, null, dstRect, mPaint);  // null即代表使用整幅图片.


    }


}