package com.zincore.getstatus;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class DownloadService {

    private static ArrayList<String> paths = new ArrayList<>();
    private static ArrayList<String> fileNames = new ArrayList<>();
    private static Context context;
    private static SingleMediaScanner scanMedia;
    private final String outputPath = (Environment.getExternalStorageDirectory().getPath() + "/GetsTatus/");

    DownloadService(ArrayList<String> paths, ArrayList<String> fileNames, Context context) {
        DownloadService.paths = paths;
        DownloadService.fileNames = fileNames;
        DownloadService.context = context;
        DownloadService.scanMedia = new SingleMediaScanner(DownloadService.context);
    }

    DownloadService() {
        // intentionally blank
    }

    public void singleDownload(Button downloadButton, int position) {

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File saveDir = new File(outputPath);
                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }

                try {
                    downloadService(position);
                    Toast.makeText(view.getContext(), "Status downloaded successfully.", Toast.LENGTH_SHORT).show();
                } catch (NullPointerException e) {
                    Toast.makeText(view.getContext(), "Please select a status to download.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(view.getContext(), String.valueOf(e), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void allDownload(Button downloadAll) throws IOException {

        downloadAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File saveDir = new File(outputPath);
                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }

                AlertDialog.Builder confirmDownloadAlert = new AlertDialog.Builder(view.getContext());
                confirmDownloadAlert.setTitle("Do you want to download all statuses?");
                confirmDownloadAlert.setCancelable(true);


                confirmDownloadAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        for (int x = 0; x < paths.size(); x++) {
                            try {
                                downloadService(x);
                            } catch (IOException ignored) {
                            }
                        }

                        if (!paths.isEmpty())
                            Toast.makeText(view.getContext(), "All statuses successfully downloaded.", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(view.getContext(), "No statuses found!", Toast.LENGTH_SHORT).show();
                    }
                });

                confirmDownloadAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = confirmDownloadAlert.create();
                alertDialog.show();
            }
        });
    }

    public void downloadService(int position) throws IOException {

        String fullOutputPath = outputPath + "z1nc0r3_" + fileNames.get(position);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Path source = Paths.get(paths.get(position));
            Path destination = Paths.get(fullOutputPath);

            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);

        } else {
            InputStream in = new BufferedInputStream(new FileInputStream(paths.get(position)));
            OutputStream out = new BufferedOutputStream(new FileOutputStream(fullOutputPath));

            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, lengthRead);
                out.flush();
            }
        }

        scanMedia.scanNow(fullOutputPath);

    }

}

