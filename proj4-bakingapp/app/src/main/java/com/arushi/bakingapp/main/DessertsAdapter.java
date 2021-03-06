/*
 *
 *  *
 *  *  This project was submitted by Arushi Pant as part of the Android Developer Nanodegree at Udacity.
 *  *
 *  *  As part of Udacity Honor code, your submissions must be your own work, hence
 *  *  submitting this project as yours will cause you to break the Udacity Honor Code
 *  *  and the suspension of your account.
 *  *
 *  *  I, the author of the project, allow you to check the code as a reference, but if
 *  *  you submit it, it's your own responsibility if you get expelled.
 *  *
 *  *  Besides the above notice, the MIT license applies and this license notice
 *  *  must be included in all works derived from this project
 *  *
 *  *  Copyright (c) 2018 Arushi Pant
 *  *
 *
 */

package com.arushi.bakingapp.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arushi.bakingapp.R;
import com.arushi.bakingapp.data.local.entity.DessertEntity;
import com.arushi.bakingapp.recipe.RecipeActivity;
import com.arushi.bakingapp.utils.GlideApp;

import java.util.ArrayList;
import java.util.List;

public class DessertsAdapter extends RecyclerView.Adapter<DessertsAdapter.DessertListViewHolder> {
    private List<DessertEntity> mDessertList;
    private Context mContext;

    public DessertsAdapter(Context context){
        this.mContext = context;
        this.mDessertList = new ArrayList<>();
    }

    @NonNull
    @Override
    public DessertListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_dessert, parent, false);
        return new DessertListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DessertListViewHolder holder, int position) {
        final DessertEntity dessert = mDessertList.get(position);
        Bundle bundle = new Bundle();
        int id = dessert.getId();
        bundle.putInt(RecipeActivity.KEY_RECIPE_ID, id);

        int defaultResource; // For default drawable shown when image not available
        if(position%2 == 0) {
            defaultResource = R.drawable.cakes;
        } else {
            defaultResource = R.drawable.oven;
        }

        Drawable defaultImg = mContext.getResources().getDrawable(defaultResource);

        GlideApp.with(mContext)
                .load(dessert.getImage())
                .thumbnail(0.1f)
                .placeholder(defaultImg)
                .error(defaultImg)
                .into(holder.dessertImageView);

        bundle.putInt(RecipeActivity.KEY_RECIPE_DEFAULT_IMG, defaultResource);

        String name = dessert.getName();
        holder.tvDessertName.setText(name);
        bundle.putString(RecipeActivity.KEY_RECIPE_NAME, name);

        holder.itemView.setTag(bundle);
    }

    @Override
    public int getItemCount() {
        if( mDessertList==null || mDessertList.size() == 0 ) return 0;
        return mDessertList.size();
    }

    class DessertListViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
        ImageView dessertImageView;
        TextView tvDessertName;

        public DessertListViewHolder(View itemView) {
            super(itemView);
            dessertImageView = itemView.findViewById(R.id.iv_dessert);
            tvDessertName = itemView.findViewById(R.id.tv_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Bundle bundle = (Bundle) view.getTag();
            Intent intent = new Intent(mContext, RecipeActivity.class);
            intent.putExtra(RecipeActivity.KEY_RECIPE_DATA, bundle);

            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
                // Shared image transition animation
                String id = String.valueOf(bundle.getInt(RecipeActivity.KEY_RECIPE_ID));

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) mContext,
                                dessertImageView,
                                mContext.getString(R.string.text_transition_img) + id);
                mContext.startActivity(intent, options.toBundle());
            } else {
                mContext.startActivity(intent);
            }
        }
    }

    public void setDessertList(List<DessertEntity> dessertList){
        this.mDessertList.clear();
        this.mDessertList.addAll(dessertList);
        notifyDataSetChanged();
    }
}
