package com.example.yb.hstt.record;

import android.graphics.Bitmap;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.yb.hstt.EventBus.RecorderEvent;
import com.example.yb.hstt.R;
import com.example.yb.hstt.record.views.VideoView;
import com.yixia.camera.model.MediaObject;
import com.yixia.camera.util.DeviceUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 视频预览
 *
 * @author 景鹏宇第一版
 */
public class MediaPreviewActivity extends RecorderBaseActivity implements
        OnClickListener, VideoView.OnPlayStateListener, OnPreparedListener {

    /**
     * 视频预览
     */
    private VideoView mVideoView;
    /**
     * 暂停图标
     */
    private View mRecordPlay;

    /**
     * 窗体宽度
     */
    private int mWindowWidth;
    /**
     * 视频信息
     */
    private MediaObject mMediaObject;
    /**
     * 主题缓存的目录
     */
    private File mThemeCacheDir;
    String dir = null;
    String filename = null;
    String functionLabel = null;
    private String TAG = "MediaPreviewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");

        String obj = getIntent().getStringExtra("obj");
        dir = getIntent().getStringExtra("dir");
        filename = getIntent().getStringExtra("filename");
        functionLabel = getIntent().getStringExtra("functionLabel");

        mMediaObject = restoneMediaObject(obj);
        if (mMediaObject == null) {
            Toast.makeText(this, R.string.record_read_object_faild,
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 防止锁屏
        mWindowWidth = DeviceUtils.getScreenWidth(this);
        setContentView(R.layout.activity_media_preview);

        // ~~~ 绑定控件
        mVideoView = (VideoView) findViewById(R.id.record_preview);
        mRecordPlay = findViewById(R.id.record_play);

        // ~~~ 绑定事件
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnPlayStateListener(this);
        findViewById(R.id.title_right).setOnClickListener(this);

        // ~~~ 初始数据
        findViewById(R.id.record_layout).getLayoutParams().height = mWindowWidth;// 设置1：1预览范围
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState()) /* && !isExternalStorageRemovable() */)
            mThemeCacheDir = new File(getExternalCacheDir(), "Theme");
        else
            mThemeCacheDir = new File(getCacheDir(), "Theme");
        mVideoView.setVideoPath(mMediaObject.getOutputTempVideoPath());
        Log.i(TAG, "VideoPath: " + mMediaObject.getOutputTempVideoPath());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPrepared(android.media.MediaPlayer mp) {
        if (!isFinishing()) {
            mVideoView.start();
            mVideoView.setLooping(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record_preview:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                } else {
                    mVideoView.start();
                }
                break;
            case R.id.title_right:
                //缩略图
                saveToFile(dir, filename);

                RecorderEvent event = new RecorderEvent();
                event.setEventId(RecorderEvent.MEDIA_SAVE_FLAG);
                event.setMsg(dir + "/" + filename + "/" + filename);
                event.setFunctionLabel(functionLabel);
                EventBus.getDefault().post(event);
                finish();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mVideoView.pause();
    }

    @Override
    public void onStateChanged(boolean isPlaying) {
        if (isPlaying)
            mRecordPlay.setVisibility(View.GONE);
        else
            mRecordPlay.setVisibility(View.VISIBLE);
    }


    /**
     * 生成视频缩略图
     *
     * @param filePath
     * @param width
     * @param height
     * @param kind
     * @return
     */
    public static Bitmap getVidioBitmap(String filePath, int width, int height,
                                        int kind) {
        Bitmap bitmap = null;

        bitmap = ThumbnailUtils.createVideoThumbnail(filePath, kind);

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        // 放回bitmap对象；
        return bitmap;
    }

    public void saveToFile(String dir, String filename) {

        Bitmap vidioBitmap = getVidioBitmap(dir + "/" + filename + "/" + filename + ".mp4", 200, 200,
                MediaStore.Images.Thumbnails.MICRO_KIND);
        File file = new File(dir + "/" + filename + "/" + filename + ".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        vidioBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        vidioBitmap.recycle();
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
    }


}
