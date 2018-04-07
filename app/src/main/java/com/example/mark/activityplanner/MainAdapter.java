package com.example.mark.activityplanner;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.mark.activityplanner.utils.Upload;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 04/12/2017.
 */

class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<Upload> mDataset;
    private Context mContext;
    RequestOptions options = new RequestOptions();

    public MainAdapter(Context context, List<Upload> mDataset) {
        mContext = context;
        this.mDataset = mDataset;
        options.placeholder(R.drawable.placeholder);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(mContext)
               .inflate(R.layout.row, parent, false);

       ViewHolder vh = new ViewHolder(v);
       return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        Upload uploadCurrent = mDataset.get(position);
        holder.mTitle.setText(uploadCurrent.getName());
        Glide.with(mContext)
                .load(uploadCurrent.getImageUrl())
                .apply(requestOptions)
                .apply(options)
                .into(holder.mImageView);
        holder.mImageView.setTag(uploadCurrent.getImageUrl());

       // holder.mImageView.setImageResource(R.drawable.placeholder);
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent picIntent = new Intent(context, ImageActivity.class);
                picIntent.putExtra("imageUrl", holder.mImageView.getTag().toString());
                context.startActivity(picIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitle;
        public ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
            mImageView = itemView.findViewById(R.id.image);
        }
    }

}
