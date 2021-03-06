package com.arushi.popularmovies.detail;
/*
 * This project was submitted by Arushi Pant as part of the Android Developer Nanodegree at Udacity.
 *
 * As part of Udacity Honor code, your submissions must be your own work, hence
 * submitting this project as yours will cause you to break the Udacity Honor Code
 * and the suspension of your account.
 *
 * I, the author of the project, allow you to check the code as a reference, but if
 * you submit it, it's your own responsibility if you get expelled.
 *
 * Besides the above notice, the MIT license applies and this license notice
 * must be included in all works derived from this project
 *
 * Copyright (c) 2018 Arushi Pant
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.arushi.popularmovies.R;
import com.arushi.popularmovies.data.model.YoutubeItem;
import com.arushi.popularmovies.utils.GlideApp;

import java.util.ArrayList;
import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private List<YoutubeItem> mTrailerList = new ArrayList<>();
    private Context mContext;

    public TrailerAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_trailer, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        YoutubeItem item = mTrailerList.get(position);
        String url = item.getYoutubeThumbnailUrl();

        GlideApp.with(mContext)
                .load(url)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_broken_image)
                .into(holder.imageThumbnail);

        holder.itemView.setTag(item.getYoutubeVideoUrl());
    }

    @Override
    public int getItemCount() {
        if(mTrailerList==null) return 0;
        return mTrailerList.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        LinearLayout movieThumbnail;
        ImageView imageThumbnail;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            movieThumbnail = itemView.findViewById(R.id.movieThumbnail);
            imageThumbnail = itemView.findViewById(R.id.imageThumbnail);
            movieThumbnail.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Open trailer video in youtube
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(view.getTag().toString()));
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(intent);
            }
        }
    }

    public void setTrailerList(List<YoutubeItem> trailerList) {
        this.mTrailerList.clear();
        this.mTrailerList.addAll(trailerList);
        // The adapter needs to know that the data has changed. If we don't call this, app will crash
        notifyDataSetChanged();
    }

}
