package com.merw_okuw_merkezi.video_trim;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gowtham.library.utils.TrimVideo;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private VideoView videoView;
    private Button button;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);
        button = findViewById(R.id.btnSelectVideo);

        button.setOnClickListener(v -> checkP());
    }

    private void checkP() {
        Dexter.withContext(this).withPermission(READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("video/*");
                startActivityForResult(intent, PICK_IMAGE);

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //saylan file yerleshyan yerini yzyna gaytaryp beryar shu callback de

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectVideo = data.getData();
                //uri saylanan video nyn uri si
                trimVideo(selectVideo);
            }
        }
        
        //video kesilenden son yene funksiya yuzlenyar
        if (requestCode == TrimVideo.VIDEO_TRIMMER_REQ_CODE && data != null) {
            Uri uri = Uri.parse(TrimVideo.getTrimmedVideoPath(data));

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(uri);
            sendBroadcast(intent);

            //kesilen videony gorkezmeli
            showVideo(uri);
        }
        
    }

    private void showVideo(Uri uri) {
        //mp---media player

        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(mp -> mp.start());
        videoView.setOnCompletionListener(mp -> mp.start());
    }

    private void trimVideo(Uri selectVideo) {
        // video ny kesmek ucin kitaphana ulandyk 
        TrimVideo.activity(String.valueOf(selectVideo))
                .setDestination("/storage/emulated/0/DCIM/Trim video")
                .start(this);
        
    }
}