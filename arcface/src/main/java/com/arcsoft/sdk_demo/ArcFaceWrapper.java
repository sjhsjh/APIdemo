package com.arcsoft.sdk_demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

/**
 * Created by gqj3375 on 2017/4/28.
 */

public class ArcFaceWrapper {
	private final String TAG = this.getClass().toString();
	private static ArcFaceWrapper instance;
	FaceDB mFaceDB;
	Uri mImage;

	private ArcFaceWrapper(Context context){
		mFaceDB = new FaceDB(context.getExternalFilesDir(null).getPath());
		mImage = null;
	}

	public static ArcFaceWrapper getInstance(Context context){
		if (instance == null) {
			synchronized (ArcFaceWrapper.class) {
				if (instance == null) {
					instance = new ArcFaceWrapper(context);
				}
			}
		}
		return instance;
	}

	public void setCaptureImage(Uri uri) {
		mImage = uri;
	}

	public Uri getCaptureImage() {
		return mImage;
	}

	/**
	 * @param path
	 * @return
	 */
	public static Bitmap decodeImage(String path) {
		Bitmap res;
		try {
			ExifInterface exif = new ExifInterface(path);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

			BitmapFactory.Options op = new BitmapFactory.Options();
			op.inSampleSize = 1;
			op.inJustDecodeBounds = false;
			//op.inMutable = true;
			res = BitmapFactory.decodeFile(path, op);
			//rotate and scale.
			Matrix matrix = new Matrix();

			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				matrix.postRotate(90);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
				matrix.postRotate(180);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				matrix.postRotate(270);
			}

			Bitmap temp = Bitmap.createBitmap(res, 0, 0, res.getWidth(), res.getHeight(), matrix, true);
			Log.d("com.arcsoft", "check target Image:" + temp.getWidth() + "X" + temp.getHeight());

			if (!temp.equals(res)) {
				res.recycle();
			}
			return temp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
