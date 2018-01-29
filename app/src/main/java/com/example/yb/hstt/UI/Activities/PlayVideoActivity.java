package com.example.yb.hstt.UI.Activities;

import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.R;

/**
 * 播放视频
 */
public class PlayVideoActivity extends HsttBaseActivity {

    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        videoPath=getIntent().getStringExtra("videoPath");

        MediaController controller = new MediaController(this);
        VideoView vv_testmediaplay = (VideoView) findViewById(R.id.vv_testmediaplay);

        vv_testmediaplay.setVideoPath(videoPath);
        vv_testmediaplay.setMediaController(controller);

        controller.setMediaPlayer(vv_testmediaplay);


        vv_testmediaplay.requestFocus();
        vv_testmediaplay.start();
    }
}
