package com.example.quizmania_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class PlayVideo extends AppCompatActivity {
    private int position = 0;
    private MediaController mediaControls;
    VideoView videoView;
    TextView video;
    private Dialog loadingDialog;
    String url,name,videoName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        videoView = findViewById(R.id.playVideo);
        video = findViewById(R.id.videonameplay);

        url = getIntent().getStringExtra("Url");
        name = getIntent().getStringExtra("Name");
        videoName = getIntent().getStringExtra("videoName");
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        // set the media controller buttons
        if (mediaControls == null)
        {
            mediaControls = new MediaController(PlayVideo.this);
        }

        loadingDialog.show();
        try
        {

            // set the media controller in the VideoView
            videoView.setMediaController(mediaControls);

            // set the uri of the video to be played
            videoView.setVideoURI(Uri.parse(url));

        } catch (Exception e)
        {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoView.requestFocus();

        // we also set an setOnPreparedListener in order to know when the video
        // file is ready for playback

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {

            public void onPrepared(MediaPlayer mediaPlayer)
            {
                // if we have a position on savedInstanceState, the video
                // playback should start from here
                videoView.seekTo(position);

                loadingDialog.dismiss();

                if (position == 0)
                {
                    videoView.start();
                } else
                {
                    // if we come from a resumed activity, video playback will
                    // be paused
                    videoView.pause();
                }
            }
        });


//        loadingDialog.show();
//        videoView.setVideoURI(Uri.parse(url));
//        videoView.requestFocus();
//        videoView.start();
//        loadingDialog.dismiss();

        video.setText(videoName);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
