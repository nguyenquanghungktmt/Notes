package com.example.product_notes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TagsViewAdapter extends RecyclerView.Adapter<TagsViewAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Tags> mTag;
    private SQLite db;
    private FragmentManager manager;
    public TagsViewAdapter(Context mContext, ArrayList<Tags> mTag) {
        this.mContext = mContext;
        this.mTag = mTag;
        this.db = new SQLite(mContext);
        this.manager = ((AppCompatActivity)mContext).getSupportFragmentManager();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View heroView = inflater.inflate(R.layout.view_tag, parent, false);
        ViewHolder viewHolder = new ViewHolder(heroView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tags tags = mTag.get(position);

        Drawable drawable = holder.viewTagColor.getBackground();
        if (drawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            gradientDrawable.setColor(Color.parseColor(tags.getCode()));
            holder.viewTagColor.setBackground(gradientDrawable);
        }
        holder.textView.setText(tags.getTitle());
        holder.delete_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTag.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                db.deleteTag(tags.getId());
            }
        });
        holder.edit_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetEditTag bottomSheetEditTag = new BottomSheetEditTag(tags.getTitle(),tags.getCode(),tags.getId());
                bottomSheetEditTag.show(manager, "TAG");

            }
        });
    }

    @Override
    public int getItemCount() {
        return mTag.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout viewTagColor;
        private TextView textView;
        private LinearLayout delete_tag,edit_tag;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewTagColor = itemView.findViewById(R.id.layoutAddTag);
            textView = itemView.findViewById(R.id.txtTag);
            delete_tag = itemView.findViewById(R.id.ic_delete_tag);
            edit_tag = itemView.findViewById(R.id.  view_edit_tag);
        }
    }
}