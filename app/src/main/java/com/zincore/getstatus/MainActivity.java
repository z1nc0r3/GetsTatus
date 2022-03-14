package com.zincore.getstatus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<String> paths = new ArrayList<>();
    private final ArrayList<String> fileNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button downloadAll = findViewById(R.id.downloadAll);

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 100);
        DownloadService ds = new DownloadService(paths, fileNames, MainActivity.this);

        try {
            ds.allDownload(downloadAll);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkPermission(String permissions, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permissions) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
        } else {
            getFileList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);

        if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Storage permission granted.", Toast.LENGTH_SHORT).show();
            getFileList();
        } else {
            Toast.makeText(this, "Please grant storage permission in order to use the application.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void getFileList() {
        String pathForStatusesNew = Environment.getExternalStorageDirectory().getPath() + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses/";
        String pathForStatusesOld = Environment.getExternalStorageDirectory().getPath() + "/WhatsApp/Media/.Statuses/";
        String pathForStatuses = pathForStatusesNew;

        File directoryNew = new File(pathForStatusesNew);
        File[] fileList = directoryNew.listFiles();

        if (fileList == null) {
            fileList = new File(pathForStatusesOld).listFiles();
            pathForStatuses = pathForStatusesOld;
        }

        if (fileList != null) {
            for (File eachFile : fileList) {
                if (!eachFile.getName().equals(".nomedia")) {
                    paths.add(pathForStatuses + eachFile.getName());
                    fileNames.add(eachFile.getName());
                }
            }
        } else {
            Toast.makeText(this, "No statuses found !", Toast.LENGTH_SHORT).show();
            MainActivity.this.finish();
        }

        RecyclerView myRecyclerView = findViewById(R.id.recyclerView);
        myRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        myRecyclerView.setAdapter(new CustomAdapter(paths, MainActivity.this));

    }

}