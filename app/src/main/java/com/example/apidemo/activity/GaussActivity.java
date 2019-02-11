package com.example.apidemo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.utils.BitmapUtils;

/**
 * Created by Administrator on 2019/2/12.
 */
public class GaussActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gauss_layout);

        final ImageView imageView1 = findViewById(R.id.image1);
        final ImageView imageView2 = findViewById(R.id.image2);

        imageView1.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imageView1.getViewTreeObserver().removeOnPreDrawListener(this);

//                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.splash);
//                blur(bitmap, imageView1);

                Bitmap blurBitmap = blur(BitmapUtils.shotActivity(GaussActivity.this), imageView1.getMeasuredWidth(), imageView1.getMeasuredHeight());
                if(blurBitmap != null){
                    imageView2.setImageBitmap(blurBitmap);
//                    imageView2.setBackground(new BitmapDrawable(getResources(), blurBitmap));
                }

                return true;
            }
        });

    }

    /**
     * @param width outputBitmap的宽度，必须大于0
     * @param height outputBitmap的高度，必须大于0
     * @return
     */
    private Bitmap blur(Bitmap srcBitmap, int width, int height) {
        if (Build.VERSION.SDK_INT >= 17) {
            //创建一个缩小后的bitmap
            Bitmap inputBitmap = Bitmap.createScaledBitmap(srcBitmap, width, height, false);
            //创建将在ondraw中使用到的经过模糊处理后的bitmap
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            //创建RenderScript，ScriptIntrinsicBlur固定写法
            RenderScript rs = RenderScript.create(this);
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            //根据inputBitmap，outputBitmap分别分配内存
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);    // 貌似可被tmpOut代替即可
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

            //设置模糊半径取值0-25之间，不同半径得到的模糊效果不同
            blurScript.setRadius(20);
            blurScript.setInput(tmpIn);
            blurScript.forEach(tmpOut);

            //得到最终的模糊bitmap
            tmpOut.copyTo(outputBitmap);    // inputBitmap或outputBitmap都可以
            rs.destroy();

            return outputBitmap;
        }

        return null;
    }

    /**
     * 一个高斯模糊的算法x
     */
    private void blur(Bitmap bitmap, ImageView view) {
        if (Build.VERSION.SDK_INT >= 17) {
            Bitmap outputBitmap = Bitmap.createBitmap((view.getMeasuredWidth()), (view.getMeasuredHeight()), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(outputBitmap);    // 画出来的图片尺寸不对
            canvas.translate(-view.getLeft(), -view.getTop());
            canvas.drawBitmap(bitmap, 0, 0, null);

            RenderScript rs = RenderScript.create(this);

            Allocation tmpIn = Allocation.createFromBitmap(rs, outputBitmap);

            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, tmpIn.getElement());
            blur.setInput(tmpIn);
            blur.setRadius(20);
            blur.forEach(tmpIn);

            tmpIn.copyTo(outputBitmap);
            view.setBackground(new BitmapDrawable(getResources(), outputBitmap));

            rs.destroy();


        }
    }


}
