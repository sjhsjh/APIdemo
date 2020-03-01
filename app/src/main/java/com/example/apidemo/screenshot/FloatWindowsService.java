package com.example.apidemo.screenshot;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by branch on 2016-5-25.
 * 启动悬浮窗界面 + 截屏功能
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FloatWindowsService extends Service {
    private static final String TAG = "FloatWindowsService";

    public static Intent newIntent(Context context, Intent mResultData) {
        Intent intent = new Intent(context, FloatWindowsService.class);
        if (mResultData != null) {
            intent.putExtras(mResultData);
        }
        return intent;
    }

    private static Intent mResultData = null;

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private GestureDetector mGestureDetector;
    private ImageView mFloatView;

    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;

    private ImageReader mImageReader;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionManager mediaProjectionManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mediaProjectionManager = ((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE));
        createFloatView();
        createImageReader();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startScreenShot();
        return super.onStartCommand(intent, flags, startId);
    }

    public static Intent getResultData() {
        return mResultData;
    }

    public static void setResultData(Intent resultData) {
        mResultData = resultData;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createFloatView() {
        mGestureDetector = new GestureDetector(getApplicationContext(), new FloatGestrueTouchListener());
        mLayoutParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        // mScreenWidth = metrics.widthPixels;
        // mScreenHeight = metrics.heightPixels;
        Point point = new Point();
        mWindowManager.getDefaultDisplay().getRealSize(point);
        mScreenWidth = point.x;
        mScreenHeight = point.y;

        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        // 设置Window flag
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mLayoutParams.x = mScreenWidth;
        mLayoutParams.y = 300;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        mFloatView = new ImageView(getApplicationContext());
        mFloatView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_clip));
        mWindowManager.addView(mFloatView, mLayoutParams);

        mFloatView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });

    }

    private class FloatGestrueTouchListener implements GestureDetector.OnGestureListener {
        int lastX, lastY;
        int paramX, paramY;

        @Override
        public boolean onDown(MotionEvent event) {
            lastX = (int) event.getRawX();
            lastY = (int) event.getRawY();
            paramX = mLayoutParams.x;
            paramY = mLayoutParams.y;
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            startScreenShot();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int dx = (int) e2.getRawX() - lastX;
            int dy = (int) e2.getRawY() - lastY;
            mLayoutParams.x = paramX + dx;
            mLayoutParams.y = paramY + dy;
            // 更新悬浮窗位置
            mWindowManager.updateViewLayout(mFloatView, mLayoutParams);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }


    private void createImageReader() {
        mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 1);
    }

    /**
     * 截屏步骤：
     * 1、需要弹窗申请得到mResultData
     * 2、用mResultData生成mMediaProjection
     * 3、每次截屏将数据保存到mVirtualDisplay和mImageReader
     * 4、从mImageReader读出Bitmap
     */
    private void startScreenShot() {
        mFloatView.setVisibility(View.GONE);
        if (mResultData == null) {
            Intent intent = new Intent(getApplicationContext(), ScreenShotPermissionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            public void run() {
                setUpMediaProjection();
                setUpVirtualDisplay();
            }
        }, 5);

        handler1.postDelayed(new Runnable() {
            public void run() {
                startGetImage();
            }
        }, 30);
    }

    public void setUpMediaProjection() {
        if (mMediaProjection == null) {
            mMediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, mResultData);
        }
    }

    private void setUpVirtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                mScreenWidth, mScreenHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    // 生成图片保存到本地
    private void startGetImage() {
        Image image = mImageReader.acquireLatestImage();
        if (image == null) {
            startScreenShot();
        } else {
            SaveTask mSaveTask = new SaveTask();
            mSaveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, image);
        }
    }

    public class SaveTask extends AsyncTask<Image, Void, Pair<Bitmap, String>> {

        @Override
        protected Pair<Bitmap, String> doInBackground(Image... params) {
            if (params == null || params.length < 1 || params[0] == null) {
                return null;
            }
            // 注意以下2点：
            // 1、Image.Plane中的 buffer 数据并不是完全是Bitmap所需要的，缓冲数据存在行间距，因此我们必须去除这些间距。
            // 2、Image 设置的图片格式与Bitmap设置的必须一致
            Image image = params[0];

            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            //每个像素的间距
            int pixelStride = planes[0].getPixelStride();
            //总的间距
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            image.close();

            File imageFile = null;
            String imagePath = null;
            if (bitmap != null) {
                try {
                    imagePath = FileUtil.getScreenShotsName(getApplicationContext());
                    NLog.d(TAG, "==doInBackground imagePath====" + imagePath);

                    imageFile = new File(imagePath);     // 图片生成目录
                    if (!imageFile.exists()) {
                        imageFile.createNewFile();
                    }
                    FileOutputStream out = new FileOutputStream(imageFile);
                    if (out != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                        // 通知相册更新
                        Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri contentUri = Uri.fromFile(imageFile);
                        media.setData(contentUri);
                        sendBroadcast(media);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    imageFile = null;
                } catch (IOException e) {
                    e.printStackTrace();
                    imageFile = null;
                }
            }

            if (imageFile != null) {
                Pair<Bitmap, String> pair = new Pair<Bitmap, String>(bitmap, imagePath);
                return pair;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Pair<Bitmap, String> pair) {
            super.onPostExecute(pair);
            if (pair == null || pair.first == null || pair.second == null) {
                return;
            }
            NLog.d(TAG, "onPostExecute 获取图片成功");
            // 开启动画
            GlobalScreenshot screenshot = new GlobalScreenshot(getApplicationContext());
            screenshot.setImagePath(pair.second);
            screenshot.takeScreenshot(pair.first, new GlobalScreenshot.onScreenShotListener() {
                @Override
                public void onStartShot() {

                }

                @Override
                public void onFinishShot(boolean success) {

                }
            }, true, true);

            mFloatView.setVisibility(View.VISIBLE);
        }
    }

    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    private void stopVirtual() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NLog.w(TAG, "FloatWindowsService onDestroy. 服务停止");
        if (mFloatView != null) {
            mWindowManager.removeView(mFloatView);
        }
        stopVirtual();
        tearDownMediaProjection();
    }
}
