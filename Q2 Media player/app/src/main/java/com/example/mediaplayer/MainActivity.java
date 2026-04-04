package com.example.mediaplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.MediaController;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnOpenFile, btnOpenUrl;
    Button btnPlay, btnPause, btnStop, btnRestart;
    EditText etUrl;
    TextView tvStatus;
    VideoView videoView;
    MediaPlayer mediaPlayer;

    boolean isAudioMode = false;
    boolean isVideoMode = false;
    int PICK_AUDIO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOpenFile = findViewById(R.id.btnOpenFile);
        btnOpenUrl  = findViewById(R.id.btnOpenUrl);
        btnPlay     = findViewById(R.id.btnPlay);
        btnPause    = findViewById(R.id.btnPause);
        btnStop     = findViewById(R.id.btnStop);
        btnRestart  = findViewById(R.id.btnRestart);
        etUrl       = findViewById(R.id.etUrl);
        tvStatus    = findViewById(R.id.tvStatus);
        videoView   = findViewById(R.id.videoView);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        btnOpenFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(intent, PICK_AUDIO);
            }
        });

        btnOpenUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = etUrl.getText().toString().trim();
                if (url.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a URL", Toast.LENGTH_SHORT).show();
                    return;
                }
                isVideoMode = true;
                isAudioMode = false;
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(Uri.parse(url));
                tvStatus.setText("Video loaded. Press Play!");
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAudioMode && mediaPlayer != null) {
                    mediaPlayer.start();
                    tvStatus.setText("Playing Audio...");
                } else if (isVideoMode) {
                    videoView.start();
                    tvStatus.setText("Playing Video...");
                } else {
                    Toast.makeText(MainActivity.this, "Open a file or URL first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAudioMode && mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    tvStatus.setText("Paused");
                } else if (isVideoMode && videoView.isPlaying()) {
                    videoView.pause();
                    tvStatus.setText("Paused");
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAudioMode && mediaPlayer != null) {
                    mediaPlayer.stop();
                    tvStatus.setText("Stopped");
                } else if (isVideoMode) {
                    videoView.stopPlayback();
                    tvStatus.setText("Stopped");
                }
            }
        });

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAudioMode && mediaPlayer != null) {
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                    tvStatus.setText("Restarted Audio");
                } else if (isVideoMode) {
                    videoView.seekTo(0);
                    videoView.start();
                    tvStatus.setText("Restarted Video");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO && resultCode == RESULT_OK && data != null) {
            Uri audioUri = data.getData();
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(this, audioUri);
                mediaPlayer.prepare();
                isAudioMode = true;
                isVideoMode = false;
                videoView.setVisibility(View.GONE);
                tvStatus.setText("Audio loaded. Press Play!");
            } catch (Exception e) {
                tvStatus.setText("Error loading audio!");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}