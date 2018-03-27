package com.example.mark.activityplanner;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mark.activityplanner.utils.Upload;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 04/12/2017.
 */

class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<Upload> mDataset;
    private Context mContext;

    public MainAdapter(Context context, List<Upload> mDataset) {
        mContext = context;
        this.mDataset = mDataset;
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
        Upload uploadCurrent = mDataset.get(position);
        holder.mTitle.setText(uploadCurrent.getName());
        Glide.with(mContext)
                .load(uploadCurrent.getImageUrl())
                .into(holder.mImageView);
       // holder.mImageView.setImageResource(R.drawable.placeholder);
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
