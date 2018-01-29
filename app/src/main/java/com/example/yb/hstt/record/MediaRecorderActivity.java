package com.example.yb.hstt.record;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yb.hstt.R;
import com.example.yb.hstt.record.views.ProgressView;
import com.yixia.camera.FFMpegUtils;
import com.yixia.camera.MediaRecorder;
import com.yixia.camera.MediaRecorder.OnErrorListener;
import com.yixia.camera.MediaRecorder.OnPreparedListener;
import com.yixia.camera.MediaRecorderFilter;
import com.yixia.camera.model.MediaObject;
import com.yixia.camera.model.MediaObject.MediaPart;
import com.yixia.camera.util.DeviceUtils;
import com.yixia.camera.util.FileUtils;
import com.yixia.camera.view.CameraNdkView;

/**
 * 视频录制
 *
 * @author 景鹏宇第一版
 */
public class MediaRecorderActivity extends RecorderBaseActivity implements
		OnErrorListener, OnClickListener, OnPreparedListener {

	public final static int REQUEST_CODE_IMPORT_VIDEO_EDIT = 997;
	/** 录制最长时间 */
	public final static int RECORD_TIME_MAX = 10 * 1000;
	/** 录制最小时间 */
	public final static int RECORD_TIME_MIN = 3 * 1000;

	/** 前后摄像头切换 */
	ImageButton btnSwitchLed;

	private CheckedTextView mRecordDelete;
	private ProgressView mProgressView;
	private CameraNdkView mSurfaceView;
	private TextView mTitleText, mTitleNext;
	private ImageView mPressText;

	private String functionLabel;

	private MediaRecorderFilter mMediaRecorder;
	private MediaObject mMediaObject;
	private int mWindowWidth;
	/** 是否是点击状态 */
	private volatile boolean mPressedStatus, mReleased, mStartEncoding;
	
	String filename = "";
	String dir = null;
	private SurfaceHolder holder;
	private String TAG="MediaRecorderActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置默认异常捕获
		Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler());
		// 初始化拍摄SDK，必须
		
		Intent intent = getIntent();
		//接收传过来的路径和文件名
		dir = intent.getStringExtra("dir");
		filename = intent.getStringExtra("filename");
		functionLabel = intent.getStringExtra("functionLabel");

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 防止锁屏
		mWindowWidth = DeviceUtils.getScreenWidth(this);
		setContentView(R.layout.activity_media_recorder);

		// ~~~ 绑定控件
		mSurfaceView = (CameraNdkView) findViewById(R.id.record_preview);
		mProgressView = (ProgressView) findViewById(R.id.record_progress);
		mTitleText = (TextView) findViewById(R.id.title_text);
		mTitleNext = (TextView) findViewById(R.id.title_right);
		mRecordDelete = (CheckedTextView) findViewById(R.id.record_delete);
		mPressText = (ImageView) findViewById(R.id.record_tips_text);
		btnSwitchLed =  (ImageButton) findViewById(R.id.btn_switch_light);

		// ~~~ 绑定事件
		findViewById(R.id.record_layout).setOnTouchListener(
				mOnSurfaceViewTouchListener);
		mTitleNext.setOnClickListener(this);
		mRecordDelete.setOnClickListener(this);

		// ~~~ 初始数据
		mSurfaceView.getLayoutParams().height = mWindowWidth;// (int)
																// (mWindowWidth
																// * 3F /
																// 2);//视频为640x480，后期裁剪成1：1的视频
		holder = mSurfaceView.getHolder();

		findViewById(R.id.record_layout).getLayoutParams().height = mWindowWidth;// 设置1：1预览范围
		mProgressView.invalidate();

	}

	@Override
	protected void onStart() {
		super.onStart();

		if (mMediaRecorder == null)
			initMediaRecorder();
		else {
			mMediaRecorder.setSurfaceHolder(holder);
			mMediaRecorder.prepare();
		}
		checkStatus();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mMediaRecorder != null && !mReleased) {
			mMediaRecorder.release();
		}
	}

	@Override
	public void onBackPressed() {
		if (mRecordDelete.isChecked()) {
			cancelDelete();
			return;
		}

		if (mMediaObject != null && mMediaObject.getDuration() > 1) {
			// 未转码
			new AlertDialog.Builder(this)
					.setTitle(R.string.hint)
					.setMessage(R.string.record_camera_exit_dialog_message)
					.setNegativeButton(R.string.dialog_yes,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mMediaObject.delete();
									finish();
								}

							}).setPositiveButton(R.string.dialog_no, null)
					.setCancelable(false).show();
			return;
		}

		if (mMediaObject != null)
			mMediaObject.delete();
		super.onBackPressed();
	}

	private void initMediaRecorder() {
		mMediaRecorder = new MediaRecorderFilter();
		mMediaRecorder.setOnErrorListener(this);
		mMediaRecorder.setOnPreparedListener(this);
		// WIFI下800k码率，其他情况（4G/3G/2G）600K码率
		mMediaRecorder
				.setVideoBitRate(isWifiAvailable(this) ? MediaRecorder.VIDEO_BITRATE_MEDIUM
						: MediaRecorder.VIDEO_BITRATE_NORMAL);
		mMediaRecorder.setSurfaceView(mSurfaceView);
		mMediaObject = mMediaRecorder.setOutputDirectory(filename,dir+"/"+filename);
		
		
		
		if (mMediaObject != null) {
			mMediaRecorder.prepare();
			mMediaRecorder
					.setCameraFilter(MediaRecorderFilter.CAMERA_FILTER_NO);
			mProgressView.setData(mMediaObject);
		} else {
			Toast.makeText(this, R.string.record_camera_init_faild,
					Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	/**
	 * 判断网络是否可用
	 */
	public static boolean isWifiAvailable(Context ctx) {
		ConnectivityManager manager = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnected();
	}

	private View.OnTouchListener mOnSurfaceViewTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mMediaRecorder == null || mMediaObject == null) {
				return false;
			}

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				// 取消回删
				if (cancelDelete())
					return true;

				// 判断是否已经超时
				if (mMediaObject.getDuration() >= RECORD_TIME_MAX) {
					return true;
				}

				// 显示当前时间
				mTitleText.setText(String.format("%.1f",
						mMediaObject.getDuration() / 1000F));

				startRecord();

				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				// 暂停
				if (mPressedStatus) {
					stopRecord();

					// 检测是否已经完成
					if (mMediaObject.getDuration() >= RECORD_TIME_MAX) {
						mTitleNext.performClick();
					}
				}

				mTitleText.setText(R.string.record_camera_title);
				break;
			}
			return true;
		}

	};

	/** 开始拍摄 */
	private void startRecord() {
		mPressedStatus = true;

		if (mMediaRecorder != null) {
			mMediaRecorder.startRecord();
		}

		if (mHandler != null) {
			mHandler.sendEmptyMessage(HANDLE_INVALIDATE_PROGRESS);
			mHandler.sendEmptyMessageDelayed(HANDLE_STOP_RECORD,
					RECORD_TIME_MAX - mMediaObject.getDuration());
		}

		mHandler.removeMessages(HANDLE_SHOW_TIPS);
		mHandler.sendEmptyMessage(HANDLE_SHOW_TIPS);
		mRecordDelete.setEnabled(false);

		mPressText.setImageResource(R.drawable.record_tips_pause);
	}

	private void stopRecord() {
		mPressedStatus = false;
		// 提示完成
		mPressText.setImageResource(R.drawable.record_tips_press);

		if (mMediaRecorder != null){
			mMediaRecorder.stopRecord();
		}
		// 取消倒计时
		mHandler.removeMessages(HANDLE_STOP_RECORD);
		mRecordDelete.setEnabled(true);
	}

	/** 是否可回删 */
	private boolean cancelDelete() {
		if (mMediaObject != null) {
			MediaPart part = mMediaObject.getCurrentPart();
			if (part != null && part.remove) {
				part.remove = false;
				mRecordDelete.setChecked(false);

				if (mProgressView != null)
					mProgressView.invalidate();

				return true;
			}
		}
		return false;
	}

	/** 刷新进度条 */
	private static final int HANDLE_INVALIDATE_PROGRESS = 0;
	/** 延迟拍摄停止 */
	private static final int HANDLE_STOP_RECORD = 1;
	/** 显示下一步 */
	private static final int HANDLE_SHOW_TIPS = 2;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_INVALIDATE_PROGRESS:
				if (mMediaObject != null && !isFinishing()) {
					if (mProgressView != null)
						mProgressView.invalidate();
					if (mPressedStatus)
						mTitleText.setText(String.format("%.1f",
								mMediaObject.getDuration() / 1000F));
					if (mPressedStatus)
						sendEmptyMessageDelayed(0, 30);
				}
				break;
			case HANDLE_SHOW_TIPS:
				if (mMediaRecorder != null && !isFinishing()) {
					int duration = checkStatus();

					if (mPressedStatus) {
						if (duration < RECORD_TIME_MAX) {
							sendEmptyMessageDelayed(HANDLE_SHOW_TIPS, 200);
						} else {
							sendEmptyMessageDelayed(HANDLE_SHOW_TIPS, 500);
						}
					}
				}
				break;
			case HANDLE_STOP_RECORD:
				stopRecord();
				startEncoding();
				break;
			}
		}
	};

	private void startEncoding() {
		// 检测磁盘空间
		if (FileUtils.showFileAvailable() < 200) {
			Toast.makeText(this, R.string.record_camera_check_available_faild,
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (!isFinishing() && mMediaRecorder != null && mMediaObject != null
				&& !mStartEncoding) {
			mStartEncoding = true;

			new AsyncTask<Void, Void, Boolean>() {

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					showProgress("",getString(R.string.record_camera_progress_message));
				}

				@Override
				protected Boolean doInBackground(Void... params) {
					boolean result = FFMpegUtils.videoTranscoding(mMediaObject,
							mMediaObject.getOutputTempVideoPath(),
							mWindowWidth, false);
					if (result && mMediaRecorder != null) {
						mMediaRecorder.release();
						mReleased = true;
					}
					return result;
				}

				@Override
				protected void onCancelled() {
					super.onCancelled();
					mStartEncoding = false;
				}

				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					hideProgress();
					if (result) {
						/** 序列化保存数据 */
						if (saveMediaObject(mMediaObject)) {
							// TODO 此处可以跳转到原来activity
							Intent intent = new Intent(
									MediaRecorderActivity.this,
									MediaPreviewActivity.class);
							intent.putExtra("obj",
									mMediaObject.getObjectFilePath());
							intent.putExtra("dir", dir);
							intent.putExtra("filename", filename);
							intent.putExtra("functionLabel", functionLabel);
							startActivity(intent);
							finish();
						} else {
							Toast.makeText(MediaRecorderActivity.this,
									R.string.record_camera_save_faild,
									Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(MediaRecorderActivity.this,
								R.string.record_video_transcoding_faild,
								Toast.LENGTH_SHORT).show();
					}
					mStartEncoding = false;
				}
			}.execute();
		}
		

		
	}

	/** 检测是否超过三秒 */
	private int checkStatus() {
		int duration = 0;
		if (!isFinishing() && mMediaObject != null) {
			duration = mMediaObject.getDuration();
			if (duration < RECORD_TIME_MIN) {
				// 视频必须大于3秒
				if (mTitleNext.getVisibility() != View.INVISIBLE)
					mTitleNext.setVisibility(View.INVISIBLE);
			} else {
				// 下一步
				if (mTitleNext.getVisibility() != View.VISIBLE) {
					mTitleNext.setVisibility(View.VISIBLE);
					mTitleNext.setText(R.string.record_camera_next);
				}
			}
		}
		return duration;
	}

	@Override
	public void onVideoError(int what, int extra) {
		Log.i(TAG, "onVideoError: ");
	}

	@Override
	public void onAudioError(int what, String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MediaRecorderActivity.this,
						R.string.record_camera_open_audio_faild,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();

		// 处理开启回删后其他点击操作
		if (id != R.id.record_delete) {
			if (mMediaObject != null) {
				MediaPart part = mMediaObject.getCurrentPart();
				if (part != null) {
					if (part.remove) {
						part.remove = false;
						mRecordDelete.setChecked(false);
						if (mProgressView != null)
							mProgressView.invalidate();
					}
				}
			}
		}

		switch (v.getId()) {
		case R.id.title_right://下一步
			startEncoding();

			break;
		case R.id.record_delete:
			if (mMediaObject != null) {
				MediaPart part = mMediaObject.getCurrentPart();
				if (part != null) {
					if (part.remove) {
						// 确认删除分块
						part.remove = false;
						mMediaObject.removePart(part, true);
						mRecordDelete.setChecked(false);
					} else {
						part.remove = true;
						mRecordDelete.setChecked(true);
					}
				}
				if (mProgressView != null)
					mProgressView.invalidate();

				// 检测按钮状态
				checkStatus();
			}
			break;
		}
	}

	@Override
	public void onPrepared() {
		if (mMediaRecorder != null) {
			// 自动对焦
			mMediaRecorder.autoFocus(new AutoFocusCallback() {

				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					if (success) {

					}
				}
			});
		}
	}

	Boolean bool = true;
	public void doClick(View view) {
		switch (view.getId()) {

		case R.id.btn_switch_light:			 

			if (mMediaRecorder != null ) {
				mMediaRecorder.toggleFlashMode();
				if(bool){
					btnSwitchLed.setBackgroundResource(R.drawable.record_camera_flash_led_on_pressed);	
					bool = false;
				}else{					
					btnSwitchLed.setBackgroundResource(R.drawable.record_camera_flash_led_off_pressed);
					bool = true;
				}
			}
			break;
		}
	}


	public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {
		private static final String TAG = "CustomExceptionHandler";

		private Thread.UncaughtExceptionHandler mDefaultUEH;

		public CustomExceptionHandler() {
			Log.d(TAG, "------------ CustomExceptionHandler ------------");
			mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		}

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			Log.e(TAG, "------------ uncaughtException ------------ " + ex.getMessage());
			mDefaultUEH.uncaughtException(thread, ex); // 不加本语句会导致ANR
		}

	}


}
