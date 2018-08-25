package com.arcsoft.sdk_demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.guo.android_extend.image.ImageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gqj3375 on 2017/4/27.
 */

public class RegisterActivity extends Activity implements SurfaceHolder.Callback {
	private final String TAG = this.getClass().toString();
	private final static int MSG_CODE = 0x1000;
	private final static int MSG_EVENT_REG = 0x1001;
	private final static int MSG_EVENT_NO_FACE = 0x1002;
	private final static int MSG_EVENT_NO_FEATURE = 0x1003;
	private final static int MSG_EVENT_FD_ERROR = 0x1004;
	private final static int MSG_EVENT_FR_ERROR = 0x1005;
	private UIHandler mUIHandler;
	// Intent data.
	private String mFilePath;

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Bitmap mBitmap;
	private Rect src = new Rect();
	private Rect dst = new Rect();
	private Thread thread;
	//	private EditText mEditText;
//	private ExtImageView mExtImageView;
//	private HListView mHListView;
//	private RegisterViewAdapter mRegisterViewAdapter;
	private AFR_FSDKFace mAFR_FSDKFace;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_register);
		//initial data.
		if (!getIntentData(getIntent().getExtras())) {
			Log.e(TAG, "getIntentData fail!");
			this.finish() ;
		}

//		mRegisterViewAdapter = new RegisterViewAdapter(this);
//		mHListView = (HListView)findViewById(R.id.hlistView);
//		mHListView.setAdapter(mRegisterViewAdapter);
//		mHListView.setOnItemClickListener(mRegisterViewAdapter);

		mUIHandler = new UIHandler();
		mBitmap = ArcFaceWrapper.decodeImage(mFilePath);
		src.set(0,0,mBitmap.getWidth(),mBitmap.getHeight());
		mSurfaceView = (SurfaceView)this.findViewById(R.id.surfaceView);
		mSurfaceView.getHolder().addCallback(this);

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// 等待mSurfaceHolder初始化完成
				while (mSurfaceHolder == null) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// 虹软人脸SDK使用的图像格式是NV21的格式，所以我们需要将获取到的图像转化为对应的格式
				byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight() * 3 / 2];
				ImageConverter convert = new ImageConverter();
				convert.initial(mBitmap.getWidth(), mBitmap.getHeight(), ImageConverter.CP_PAF_NV21);
				if (convert.convert(mBitmap, data)) {
					Log.d(TAG, "convert ok!");
				}
				convert.destroy();

				// 检索人脸
				AFD_FSDKEngine engine = new AFD_FSDKEngine();
				AFD_FSDKVersion version = new AFD_FSDKVersion();
				List<AFD_FSDKFace> result = new ArrayList<AFD_FSDKFace>();
				AFD_FSDKError err = engine.AFD_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);
				Log.d(TAG, "AFD_FSDK_InitialFaceEngine = " + err.getCode());
				if (err.getCode() != AFD_FSDKError.MOK) {
					Message reg = Message.obtain();
					reg.what = MSG_CODE;
					reg.arg1 = MSG_EVENT_FD_ERROR;
					reg.arg2 = err.getCode();
					mUIHandler.sendMessage(reg);
				}
				err = engine.AFD_FSDK_GetVersion(version);
				Log.d(TAG, "AFD_FSDK_GetVersion =" + version.toString() + ", " + err.getCode());
				err  = engine.AFD_FSDK_StillImageFaceDetection(data, mBitmap.getWidth(), mBitmap.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);	// 识别有多少个人脸！！
				Log.d(TAG, "AFD_FSDK_StillImageFaceDetection =" + err.getCode() + "<" + result.size());

				while (mSurfaceHolder != null) {	// 还不如while(true)
					Canvas canvas = mSurfaceHolder.lockCanvas();
					if (canvas != null) {
						Paint mPaint = new Paint();
						boolean fit_horizontal = canvas.getWidth() / (float)src.width() < canvas.getHeight() / (float)src.height() ? true : false;
						float scale = 1.0f;
						if (fit_horizontal) {
							scale = canvas.getWidth() / (float)src.width();
							dst.left = 0;
							dst.top = (canvas.getHeight() - (int)(src.height() * scale)) / 2;
							dst.right = dst.left + canvas.getWidth();
							dst.bottom = dst.top + (int)(src.height() * scale);
						} else {
							scale = canvas.getHeight() / (float)src.height();
							dst.left = (canvas.getWidth() - (int)(src.width() * scale)) / 2;
							dst.top = 0;
							dst.right = dst.left + (int)(src.width() * scale);
							dst.bottom = dst.top + canvas.getHeight();
						}
						canvas.drawBitmap(mBitmap, src, dst, mPaint);	// 画摄像得到的全图！！
						canvas.save();
						canvas.scale((float) dst.width() / (float) src.width(), (float) dst.height() / (float) src.height());
						canvas.translate(dst.left / scale, dst.top / scale);
						// 画头像框
						for (AFD_FSDKFace face : result) {
							mPaint.setColor(Color.RED);
							mPaint.setStrokeWidth(10.0f);
							mPaint.setStyle(Paint.Style.STROKE);
							canvas.drawRect(face.getRect(), mPaint);
						}
						canvas.restore();
						mSurfaceHolder.unlockCanvasAndPost(canvas);
						break;
					}
				}

				// 人脸特征检测
				if (!result.isEmpty()) {	//如果人脸检测引擎返回的结果不为空
					AFR_FSDKVersion version1 = new AFR_FSDKVersion();
					AFR_FSDKEngine engine1 = new AFR_FSDKEngine();
					AFR_FSDKFace result1 = new AFR_FSDKFace();
					AFR_FSDKError error1 = engine1.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
					Log.d("com.arcsoft", "AFR_FSDK_InitialEngine = " + error1.getCode());
					if (error1.getCode() != AFD_FSDKError.MOK) {
						Message reg = Message.obtain();
						reg.what = MSG_CODE;
						reg.arg1 = MSG_EVENT_FR_ERROR;
						reg.arg2 = error1.getCode();
						mUIHandler.sendMessage(reg);
					}
					error1 = engine1.AFR_FSDK_GetVersion(version1);
					Log.d("com.arcsoft", "FR=" + version.toString() + "," + error1.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
					error1 = engine1.AFR_FSDK_ExtractFRFeature(data, mBitmap.getWidth(), mBitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21,
							new Rect(result.get(0).getRect()), result.get(0).getDegree(), result1);
					Log.d("com.arcsoft", "Face=" + result1.getFeatureData()[0] + "," + result1.getFeatureData()[1] + "," + result1.getFeatureData()[2] + "," + error1.getCode());

					if(error1.getCode() == error1.MOK) {
						mAFR_FSDKFace = result1.clone();	//多个人脸特征时会导致同时操作一个变量出现错误
						int width = result.get(0).getRect().width();
						int height = result.get(0).getRect().height();
						Bitmap face_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
						Canvas face_canvas = new Canvas(face_bitmap);
						face_canvas.drawBitmap(mBitmap, result.get(0).getRect(), new Rect(0, 0, width, height), null);
						Message reg = Message.obtain();
						reg.what = MSG_CODE;
						reg.arg1 = MSG_EVENT_REG;
						reg.obj = face_bitmap;
						Log.w("com.arcsoft" , " face_bitmap = " + face_bitmap);

						mUIHandler.sendMessage(reg);	//弹出对话框提示输入人脸信息并保存人脸
					} else {
						Message reg = Message.obtain();
						reg.what = MSG_CODE;
						reg.arg1 = MSG_EVENT_NO_FEATURE;	//人脸特征无法检测
						mUIHandler.sendMessage(reg);
					}
					error1 = engine1.AFR_FSDK_UninitialEngine();
					Log.d("com.arcsoft", "AFR_FSDK_UninitialEngine : " + error1.getCode());
				} else {
					Message reg = Message.obtain();
					reg.what = MSG_CODE;
					reg.arg1 = MSG_EVENT_NO_FACE;	//没有检测到人脸
					mUIHandler.sendMessage(reg);
				}

				err = engine.AFD_FSDK_UninitialFaceEngine();
				Log.d(TAG, "AFD_FSDK_UninitialFaceEngine =" + err.getCode());
			}
		});
		thread.start();

	}

	/**
	 * @note bundle data :
	 * String imagePath
	 * @param bundle
	 */
	private boolean getIntentData(Bundle bundle) {
		try {
			mFilePath = bundle.getString("imagePath");
			if (mFilePath == null || mFilePath.isEmpty()) {
				return false;
			}
			Log.i(TAG, "getIntentData mFilePath = " + mFilePath);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mSurfaceHolder = holder;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mSurfaceHolder = null;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	class UIHandler extends android.os.Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == MSG_CODE) {
				if (msg.arg1 == MSG_EVENT_REG) {
			//		((ArcFaceWrapper)RegisterActivity.this.getApplicationContext())
					ArcFaceWrapper.getInstance(RegisterActivity.this).mFaceDB.addFace(String.valueOf(System.currentTimeMillis()), mAFR_FSDKFace);
					Toast.makeText(RegisterActivity.this, "人脸特征采集成功", Toast.LENGTH_SHORT).show();
					// tudo startactivity
					finish();

//					LayoutInflater inflater = LayoutInflater.from(RegisterActivity.this);
//					View layout = inflater.inflate(R.layout.dialog_register, null);
//					mEditText = (EditText) layout.findViewById(R.id.editview);
//					mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
//					mExtImageView = (ExtImageView) layout.findViewById(R.id.extimageview);
//					mExtImageView.setImageBitmap((Bitmap) msg.obj);		// Bitmap
//
//					new AlertDialog.Builder(RegisterActivity.this)
//							.setTitle("请输入注册名字")
//							.setIcon(android.R.drawable.ic_dialog_info)
//							.setView(layout)
//							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//									ArcFaceWrapper.getInstance(RegisterActivity.this).mFaceDB.addFace(mEditText.getText().toString(), mAFR_FSDKFace);
//									mRegisterViewAdapter.notifyDataSetChanged();
//									dialog.dismiss();
//								}
//							})
//							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//									dialog.dismiss();
//								}
//							})
//							.show();
				} else if(msg.arg1 == MSG_EVENT_NO_FEATURE ){
					Toast.makeText(RegisterActivity.this, "人脸特征无法检测，请换一张图片", Toast.LENGTH_SHORT).show();
					detectFail();
				} else if(msg.arg1 == MSG_EVENT_NO_FACE ){
					Toast.makeText(RegisterActivity.this, "没有检测到人脸，请换一张图片", Toast.LENGTH_SHORT).show();
					detectFail();
				} else if(msg.arg1 == MSG_EVENT_FD_ERROR ){
					Toast.makeText(RegisterActivity.this, "FD初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
					finish();
				} else if(msg.arg1 == MSG_EVENT_FR_ERROR){
					Toast.makeText(RegisterActivity.this, "FR初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
					finish();
				}
			}
		}
	}

	private void detectFail(){
		Intent intent = new Intent();
		intent.putExtra("fail", true);
		setResult(RESULT_OK, intent);
		finish();
	}


//	class Holder {
//		ExtImageView siv;
//		TextView tv;
//	}
//
//	class RegisterViewAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{
//		Context mContext;
//		LayoutInflater mLInflater;
//
//		public RegisterViewAdapter(Context c) {
//			// TODO Auto-generated constructor stub
//			mContext = c;
//			mLInflater = LayoutInflater.from(mContext);
//		}
//
//		@Override
//		public int getCount() {
//			// TODO Auto-generated method stub
//			return ArcFaceWrapper.getInstance(RegisterActivity.this).mFaceDB.mRegister.size();
//		}
//
//		@Override
//		public Object getItem(int arg0) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			// TODO Auto-generated method stub
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			// TODO Auto-generated method stub
//			Holder holder = null;
//			if (convertView != null) {
//				holder = (Holder) convertView.getTag();
//			} else {
//				convertView = mLInflater.inflate(R.layout.item_sample, null);
//				holder = new Holder();
//				holder.siv = (ExtImageView) convertView.findViewById(R.id.imageView1);
//				holder.tv = (TextView) convertView.findViewById(R.id.textView1);
//				convertView.setTag(holder);
//			}
//
//			if (!(ArcFaceWrapper.getInstance(RegisterActivity.this).mFaceDB.mRegister.isEmpty())) {
//				FaceDB.FaceRegist face = ArcFaceWrapper.getInstance(RegisterActivity.this).mFaceDB.mRegister.get(position);
//				holder.tv.setText(face.mName);
//				//holder.siv.setImageResource(R.mipmap.ic_launcher);
//				convertView.setWillNotDraw(false);
//			}
//
//			return convertView;
//		}
//
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//			Log.d("onItemClick", "onItemClick = " + position + "pos=" + mHListView.getScroll());
//			final String name = ArcFaceWrapper.getInstance(RegisterActivity.this).mFaceDB.mRegister.get(position).mName;
//			final int count = ArcFaceWrapper.getInstance(RegisterActivity.this).mFaceDB.mRegister.get(position).mFaceList.size();
//			new AlertDialog.Builder(RegisterActivity.this)
//					.setTitle("删除注册名:" + name)
//					.setMessage("包含:" + count + "个注册人脸特征信息")
//					.setIcon(android.R.drawable.ic_dialog_alert)
//					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							ArcFaceWrapper.getInstance(RegisterActivity.this).mFaceDB.delete(name);
//							mRegisterViewAdapter.notifyDataSetChanged();
//							dialog.dismiss();
//						}
//					})
//					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							dialog.dismiss();
//						}
//					})
//					.show();
//		}
//	}

}
