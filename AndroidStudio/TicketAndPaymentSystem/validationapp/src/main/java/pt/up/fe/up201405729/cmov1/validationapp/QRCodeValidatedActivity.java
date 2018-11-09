package pt.up.fe.up201405729.cmov1.validationapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class QRCodeValidatedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_validated);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle("QR code validated");
            bar.hide();
        }

        final VideoView videoView = findViewById(R.id.videoView);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        mediaController.setVisibility(View.GONE);
        videoView.setMediaController(mediaController);
        // Original video URL: https://www.youtube.com/watch?v=CHEoIdRcEnU (this video is under Creative Commons license)
        String path = "android.resource://" + getPackageName() + "/" + R.raw.check_mark_v_tick_4k_green_screen_free_download;
        videoView.setVideoURI(Uri.parse(path));
        videoView.requestFocus();
        videoView.start();

        final Context packageContext = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!videoView.isPlaying())
                        Thread.sleep(100);
                    while (videoView.getCurrentPosition() < 3000)
                        Thread.sleep(100);
                    videoView.stopPlayback();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(packageContext, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
    }
}
