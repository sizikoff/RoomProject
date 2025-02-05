package com.amicus.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnRecord;
    Button btnStop;

    TextView tvRecordingStatus;
    RecyclerView rvRecordings;

    String filePath;
    MediaRecorder mediaRecorder;
    private static final int PERMISSION_CODE = 1000;

    List<File> recordingFiles;
    RecordingAdapter adapter;
    Handler handler = new Handler();
    boolean isBlinking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRecord = findViewById(R.id.btnRecord);
        btnStop = findViewById(R.id.btnStop);
        tvRecordingStatus = findViewById(R.id.tvRecordingStatus);
        rvRecordings = findViewById(R.id.rvRecordings);
        rvRecordings.setLayoutManager(new LinearLayoutManager(this));

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });
        loadRecordings();
        checkPermissions();
    }

    private void loadRecordings() {
        File directory = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        recordingFiles = new ArrayList<>();
        if (directory != null) {
            File[]files = directory.listFiles();
            if (files != null) {
                for (File file:files) {
                    recordingFiles.add(file);
                }
            }
        }
        adapter = new RecordingAdapter(this,recordingFiles);
        rvRecordings.setAdapter(adapter);
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            Toast.makeText(this, "Record save", Toast.LENGTH_SHORT).show();
            btnStop.setVisibility(View.GONE);
            btnRecord.setVisibility(View.VISIBLE);

            tvRecordingStatus.setVisibility(View.GONE);
            isBlinking = false;
            loadRecordings();
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
        }
    }

    private void startRecording() {
        filePath = getExternalFilesDir(Environment.DIRECTORY_MUSIC)+"/recording_"+System.currentTimeMillis()+".3gp";
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(filePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Recording start", Toast.LENGTH_SHORT).show();
            btnRecord.setVisibility(View.GONE);
            btnStop.setVisibility(View.VISIBLE);
            tvRecordingStatus.setVisibility(View.VISIBLE);
            startBlinking();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startBlinking() {
        isBlinking = true;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isBlinking) {
                    tvRecordingStatus.setVisibility(tvRecordingStatus.getVisibility()==View.VISIBLE?
                            View.INVISIBLE:View.VISIBLE);
                            handler.postDelayed(this,500);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permissoon granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}