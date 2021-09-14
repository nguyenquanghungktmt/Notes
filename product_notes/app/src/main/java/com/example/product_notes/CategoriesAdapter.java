package com.example.product_notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Categoties> mCategories;
    private Filter filter;

    public CategoriesAdapter(Context mContext, ArrayList<Categoties> mCategories, Filter filter) {
        this.mContext = mContext;
        this.mCategories = mCategories;
        this.filter = filter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View heroView = inflater.inflate(R.layout.item_category, parent, false);
        ViewHolder viewHolder = new ViewHolder(heroView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Categoties category = mCategories.get(position);
        holder.mTextName.setText(category.getName());
        holder.mTextName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mTextName.getText() == "None") {
                    filter.filterNone();
                } else if (holder.mTextName.getText() == "Text") {
                    filter.filterText();
                } else if (holder.mTextName.getText() == "Checklist") {
                    filter.filterChecklist();
                } else if (holder.mTextName.getText() == "Ngày mới nhất") {
                    filter.filterLatestDate();
                } else if (holder.mTextName.getText() == "Tag") {
                    filter.filterTag();
                } else if (holder.mTextName.getText() == "Nhắc nhở") {
                    filter.filterReminder();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextName = itemView.findViewById(R.id.txt_category);
        }
    }
}