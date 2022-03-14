package com.zincore.getstatus;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

    private final MediaScannerConnection mMs;

    public SingleMediaScanner(Context context) {
        mMs = new MediaScannerConnection(context, this);
        mMs.connect();
    }

    public void scanNow(String path) {
        mMs.scanFile(path, null);
    }

    @Override
    public void onMediaScannerConnected() {

    }

    @Override
    public void onScanCompleted(String s, Uri uri) {

    }
}