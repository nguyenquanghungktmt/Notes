package com.example.product_notes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FilterTagAdapter extends RecyclerView.Adapter<FilterTagAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Tags> mTag;
    private OnFilterTag onFilterTag;
    public FilterTagAdapter(Context mContext, ArrayList<Tags> mTag, OnFilterTag onFilterTag) {
        this.mContext = mContext;
        this.mTag = mTag;
        this.onFilterTag = onFilterTag;
    }

    @NonNull
    @Override
    public FilterTagAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View heroView = inflater.inflate(R.layout.color_default, parent, false);
        FilterTagAdapter.ViewHolder viewHolder = new FilterTagAdapter.ViewHolder(heroView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FilterTagAdapter.ViewHolder holder, int position) {
        Tags tags = mTag.get(position);

        Drawable drawable = holder.color_default.getBackground();
        if (drawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            gradientDrawable.setColor(Color.parseColor(tags.getCode()));
            holder.color_default.setBackground(gradientDrawable);
        }
        holder.color_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFilterTag.onFilterTag(tags.getCode());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTag.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View color_default;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            color_default = itemView.findViewById(R.id.color_default);
        }
    }
}