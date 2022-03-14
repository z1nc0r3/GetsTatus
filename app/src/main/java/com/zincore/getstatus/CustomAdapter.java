package com.zincore.getstatus;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private final ArrayList<String> paths;
    private final Context context;

    public CustomAdapter(ArrayList<String> paths, Context context) {
        this.paths = paths;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (paths.get(position).contains(".jpg")) {
            holder.imageView.setImageURI(Uri.fromFile(new File(paths.get(position))));
        } else {
            Glide.with(context).load(paths.get(position)).into(holder.imageView);
            holder.isVideo.setVisibility(View.VISIBLE);
        }

        showPopUp(holder, position);
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void showPopUp(ViewHolder holder, int position) {
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog builder = new Dialog(context);

                builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                int width = context.getResources().getDisplayMetrics().widthPixels;
                int height = context.getResources().getDisplayMetrics().heightPixels;

                if (paths.get(position).contains(".jpg")) {
                    builder.setContentView(R.layout.popup_image);

                    try {
                        downloadImage(builder, position);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ImageView popUp = builder.findViewById(R.id.popUpImage);
                    CardView imageCard = builder.findViewById(R.id.cardView);
                    popUp.setImageURI(Uri.fromFile(new File(paths.get(position))));
                    popUp.setAdjustViewBounds(true);
                    popUp.setMaxHeight((int) (height * 0.75));
                    imageCard.requestLayout();
                    imageCard.getLayoutParams().width = width - 80;

                    WindowManager.LayoutParams lp = builder.getWindow().getAttributes();
                    lp.dimAmount = 0.75f;
                    builder.getWindow().setAttributes(lp);

                    builder.show();

                    popUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            builder.dismiss();
                        }
                    });

                } else {
                    builder.setContentView(R.layout.popup_video);

                    try {
                        downloadVideo(builder, position);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    CardView videoCard = builder.findViewById(R.id.cardView);
                    VideoView popUp = builder.findViewById(R.id.popUpVideo);
                    popUp.setZOrderOnTop(true);
                    popUp.setVideoPath(paths.get(position));
                    popUp.start();

                    MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
                    mRetriever.setDataSource(paths.get(position));

                    videoCard.requestLayout();
                    videoCard.getLayoutParams().width = width - 80;
                    videoCard.getLayoutParams().height = (int) (height * 0.7);

                    WindowManager.LayoutParams lp = builder.getWindow().getAttributes();
                    lp.dimAmount = 0.75f;
                    builder.getWindow().setAttributes(lp);
                    builder.show();

                    popUp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            popUp.start();
                        }
                    });

                    popUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            builder.dismiss();
                        }
                    });

                    videoCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            builder.dismiss();
                        }
                    });
                }
            }
        });
    }

    public void downloadImage(Dialog builder, int position) throws IOException {
        Button downloadImage = builder.findViewById(R.id.downloadImage);
        new DownloadService().singleDownload(downloadImage, position);
    }

    public void downloadVideo(Dialog builder, int position) throws IOException {
        Button downloadVideo = builder.findViewById(R.id.downloadVideo);
        new DownloadService().singleDownload(downloadVideo, position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView isVideo;

        public ViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            isVideo = view.findViewById(R.id.isVideo);
            isVideo.setVisibility(View.GONE);
        }
    }
}