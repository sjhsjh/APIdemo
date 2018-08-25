package com.arcsoft.sdk_demo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class ArcMainActivity extends Activity implements OnClickListener {
	private final String TAG = this.getClass().toString();

	private static final int REQUEST_CODE_IMAGE_CAMERA = 1;
	private static final int REQUEST_CODE_IMAGE_OP = 2;
	private static final int REQUEST_CODE_OP = 3;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main_test);
		findViewById(R.id.register).setOnClickListener(this);
		findViewById(R.id.detect).setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_IMAGE_OP && resultCode == RESULT_OK) {		// 打开图库图片
			Uri mPath = data.getData();
			String file = ArcSoftUtils.getPath(this, mPath);
			Bitmap bmp = ArcFaceWrapper.decodeImage(file);
			if (bmp == null || bmp.getWidth() <= 0 || bmp.getHeight() <= 0 ) {
				Log.e(TAG, "error");
			} else {
				Log.i(TAG, "bmp [" + bmp.getWidth() + "," + bmp.getHeight());
			}
			startRegister(bmp, file);
		} else if (requestCode == REQUEST_CODE_OP) {
			Log.i(TAG, "resultCode =" + resultCode);
			if (data == null) {
				return;
			}
			Bundle bundle = data.getExtras();
			// 采集失败重新拍照
			boolean fail = bundle.getBoolean("fail");
			if(fail){
				startCamera();
			}
			String path = bundle.getString("imagePath");
			Log.i(TAG, "path="+path);
		} else if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {		// 拍摄照片
			Uri mPath = ArcFaceWrapper.getInstance(ArcMainActivity.this).getCaptureImage();
			String file = ArcSoftUtils.getPath(this, mPath);
			Bitmap bmp = ArcFaceWrapper.decodeImage(file);
			startRegister(bmp, file);
		}

	}

	@Override
	public void onClick(View paramView) {
		int viewID = paramView.getId();
		if(viewID == R.id.detect){
			if(ArcFaceWrapper.getInstance(ArcMainActivity.this).mFaceDB.mRegister.isEmpty() ) {
				Toast.makeText(this, "没有注册人脸，请先注册！", Toast.LENGTH_SHORT).show();
			} else {
//					new AlertDialog.Builder(this)
//							.setTitle("请选择相机")
//							.setIcon(android.R.drawable.ic_dialog_info)
//							.setItems(new String[]{"后置相机", "前置相机"}, new DialogInterface.OnClickListener() {
//										@Override
//										public void onClick(DialogInterface dialog, int which) {
//											startDetector(which);
//										}
//									})
//							.show();

				startDetector(Camera.CameraInfo.CAMERA_FACING_FRONT);
			}
		}
		else if(viewID == R.id.register){
//				new AlertDialog.Builder(this)
//						.setTitle("请选择注册方式")
//						.setIcon(android.R.drawable.ic_dialog_info)
//						.setItems(new String[]{"打开图片", "拍摄照片"}, new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								switch (which){
//									case 1:
//										Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//										ContentValues values = new ContentValues(1);
//										values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//										Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//										ArcFaceWrapper.getInstance(ArcMainActivity.this).setCaptureImage(uri);
//										intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//										startActivityForResult(intent, REQUEST_CODE_IMAGE_CAMERA);
//										break;
//									case 0:
//										Intent getImageByalbum = new Intent(Intent.ACTION_GET_CONTENT);
//										getImageByalbum.addCategory(Intent.CATEGORY_OPENABLE);
//										getImageByalbum.setType("image/jpeg");
//										startActivityForResult(getImageByalbum, REQUEST_CODE_IMAGE_OP);
//										break;
//									default:;
//								}
//							}
//						})
//						.show();

			startCamera();
		}

	}

	private void startCamera(){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);	// 拍照到指定目录
		ContentValues values = new ContentValues(1);
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		ArcFaceWrapper.getInstance(ArcMainActivity.this).setCaptureImage(uri);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, REQUEST_CODE_IMAGE_CAMERA);
	}

	private void startRegister(Bitmap bitmap, String file) {
		Intent it = new Intent(ArcMainActivity.this, RegisterActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("imagePath", file);
		it.putExtras(bundle);
		startActivityForResult(it, REQUEST_CODE_OP);
	}

	private void startDetector(int camera) {
		Intent it = new Intent(ArcMainActivity.this, DetecterActivity.class);
		it.putExtra("Camera", camera);
		startActivityForResult(it, REQUEST_CODE_OP);
	}

}

