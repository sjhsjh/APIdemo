package com.example.apidemo.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import com.blankj.utilcode.util.FileUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;
import com.example.apidemo.utils.image.BitmapUtils;
import com.tencent.matrix.resource.analyzer.BitmapAnalyzer;

import java.io.File;

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

        ((Button)findViewById(R.id.button1)).setText("dumpHprof");
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BitmapAnalyzer.INSTANCE.dumpHprof();
            }
        });



        imageView1.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imageView1.getViewTreeObserver().removeOnPreDrawListener(this);

//                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.splash);
//                blur(bitmap, imageView1);

                Bitmap blurBitmap = blur(BitmapUtils.shotActivity(GaussActivity.this), imageView1.getMeasuredWidth(), imageView1.getMeasuredHeight());
                if (blurBitmap != null) {
                    imageView2.setImageBitmap(blurBitmap);
//                    imageView2.setBackground(new BitmapDrawable(getResources(), blurBitmap));
                }

                return true;
            }
        });

        // glideTest(imageView1);
    }

    private void glideTest(final ImageView imageView1) {
        RequestOptions options = new RequestOptions()
                .override(imageView1.getWidth(), imageView1.getHeight());
//                .format(DecodeFormat.PREFER_RGB_565);
        // options.placeholder(R.drawable.loading)
        //         .error(R.drawable.error)
        //         .centerCrop()
        //         .transform(new BlurTransformation(), new GrayscaleTransformation())

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/APIDemoLog/CameraView.png");
        if (FileUtils.isFileExists(file)) {
            try {
                Glide.with(this).load(file)
//                        .load("https://yjmf.bs2dl.yy.com/yym101ip_2328321633_1555410430_395619399.jpg?ips_convert/format=webp")
//                         .apply(options)

                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                int width = resource.getIntrinsicWidth();
                                int height = resource.getIntrinsicHeight();
                                // java.lang.RuntimeException: Canvas: trying to draw too large(192000000bytes) bitmap.（from DisplayListCanvas）
                                // DisplayListCanvas.MAX_BITMAP_SIZE等于100 MB.
//                                imageView1.setImageDrawable(resource);

                                NLog.i("sjh0", "onResourceReady byteCount : " + BitmapUtils.drawableToBitamp(resource).getAllocationByteCount());   // 192000000
                                NLog.i("sjh0", "onResourceReady format : " + BitmapUtils.drawableToBitamp(resource).getConfig().name());            // ARGB_8888
                            }
                        });
            } catch (OutOfMemoryError error) {
                NLog.w("sjh0", "glide OutOfMemoryError = " + error);    // 无OOM，该大图还没达到OOM触发条件
                error.printStackTrace();
            } catch (Exception e) {
                NLog.w("sjh0", "glide Exception = " + e.getMessage());
                e.printStackTrace();
            }
        }
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
