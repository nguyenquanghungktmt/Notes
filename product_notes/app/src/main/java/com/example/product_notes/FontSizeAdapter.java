package com.example.product_notes;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class FontSizeAdapter extends RecyclerView.Adapter<FontSizeAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<FontSize> size;
    public static final String SHARED_PREFS = "sharedPrefs";
    private Dialog dialog;
    private onDefaultSize onDefaultSize;

    public FontSizeAdapter(Context mContext, ArrayList<FontSize> size, Dialog dialog, onDefaultSize onDefaultSize) {
        this.mContext = mContext;
        this.size = size;
        this.dialog = dialog;
        this.onDefaultSize= onDefaultSize;
    }

    @NonNull
    @Override
    public FontSizeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View heroView = inflater.inflate(R.layout.item_defaul_fontsize, parent, false);
        FontSizeAdapter.ViewHolder viewHolder = new FontSizeAdapter.ViewHolder(heroView);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull FontSizeAdapter.ViewHolder holder, int position) {
        FontSize fontSize = size.get(position);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String text = sharedPreferences.getString("defaultFontSize", "Tiny");
        holder.fonSizeDefault.setText(fontSize.getSize());
        if (text.equals(fontSize.getSize())) {
            holder.fonSizeDefault.setTextColor(Color.parseColor("#FF4880FF"));
        }
        holder.layoutSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("defaultFontSize", fontSize.getSize());
                editor.apply();
                dialog.dismiss();
                Toast.makeText(mContext, "Data saved", Toast.LENGTH_SHORT).show();
                onDefaultSize.onChangeSize(fontSize.getSize());
            }
        });
    }

    @Override
    public int getItemCount() {
        return size.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fonSizeDefault;
        private LinearLayout layoutSize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fonSizeDefault = itemView.findViewById(R.id.size_default);
            layoutSize = itemView.findViewById(R.id.layout_size);
        }
    }
}