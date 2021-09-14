package com.example.product_notes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Tags> mTag;
    private OnDataCallback onDataCallback;

    public TagsAdapter(Context mContext, ArrayList<Tags> mTag) {
        this.mContext = mContext;
        this.mTag = mTag;
    }

    @NonNull
    @Override
    public TagsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View heroView = inflater.inflate(R.layout.item_tag, parent, false);
        TagsAdapter.ViewHolder viewHolder = new TagsAdapter.ViewHolder(heroView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TagsAdapter.ViewHolder holder, int position) {
        Tags tags = mTag.get(position);

        Drawable drawable = holder.tagColor.getBackground();
        if (drawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            gradientDrawable.setColor(Color.parseColor(tags.getCode()));
            holder.tagColor.setBackground(gradientDrawable);
        }
        holder.tagColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("myTag","tags.getCode() "+tags.getCode());
                onDataCallback.onColorBackgroundChange(Color.parseColor(tags.getCode()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTag.size();
    }

    public void setOnDataCallback(OnDataCallback onDataCallback) {
        this.onDataCallback = onDataCallback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout tagColor;
        private LinearLayout bgrColor;
        private ImageView imgActive;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tagColor = itemView.findViewById(R.id.tag_color);
            bgrColor = itemView.findViewById(R.id.layout_create_note);
            //imgActive = itemView.findViewById(R.id.active);

        }
    }
}