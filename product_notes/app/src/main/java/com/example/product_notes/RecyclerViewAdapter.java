package com.example.product_notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;
import static com.example.product_notes.ColorDefaultAdapter.SHARED_PREFS;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {
    private Context mContext;
    private ArrayList<Content> mContent;
    private OnDataCallBackChecklist onDataCallBackChecklist;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView txtContentChecklist;
        private ImageView checked,delete;
        private View check;
        public MyViewHolder(View itemView) {
            super(itemView);
            txtContentChecklist = itemView.findViewById(R.id.txt_content_checklist);
            checked = itemView.findViewById(R.id.checked);
            delete = itemView.findViewById(R.id.delete_checklist);
        }
    }

    public RecyclerViewAdapter(Context mContext, ArrayList<Content> content) {
        this.mContext = mContext;
        this.mContent = content;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checked, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Content content = mContent.get(position);
        holder.txtContentChecklist.setText(content.getContent());
        holder.txtContentChecklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDataCallBackChecklist.onShowDialog(holder.getAdapterPosition());
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContent.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String fontSize = sharedPreferences.getString("defaultFontSize", "Tiny");
        if (fontSize.equals("Tiny")) {
            holder.txtContentChecklist.setTextSize(14);
        } else if (fontSize.equals("Small")) {
            holder.txtContentChecklist.setTextSize(16);
        } else if (fontSize.equals("Medium")) {
            holder.txtContentChecklist.setTextSize(18);
        } else if (fontSize.equals("Large")) {
            holder.txtContentChecklist.setTextSize(20);
        } else if (fontSize.equals("Huge")) {
            holder.txtContentChecklist.setTextSize(22);
        }
    }


    @Override
    public int getItemCount() {
        return mContent.size();
    }

    public void setOnDataCallback(OnDataCallBackChecklist onDataCallback) {
        this.onDataCallBackChecklist = onDataCallback;
    }
    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mContent, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mContent, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        System.out.println("--ahihi: " + mContent.toString());
    }

    @Override
    public void onRowSelected(MyViewHolder myViewHolder) {
        myViewHolder.txtContentChecklist.setTextColor(0x94aaea4a);
    }

    @Override
    public void onRowClear(MyViewHolder myViewHolder) {
        myViewHolder.txtContentChecklist.setTextColor(0xFFFFFFFF);

    }
}