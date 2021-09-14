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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ColorDefaultAdapter extends RecyclerView.Adapter<ColorDefaultAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Tags> mTag;
    public static final String SHARED_PREFS = "sharedPrefs";
    private Dialog dialog;
    private onDefaultColor onDefaultColor;

    public ColorDefaultAdapter(Context mContext, ArrayList<Tags> mTag, Dialog dialog, onDefaultColor onDefaultColor) {
        this.mContext = mContext;
        this.mTag = mTag;
        this.dialog = dialog;
        this.onDefaultColor = onDefaultColor;
    }

    @NonNull
    @Override
    public ColorDefaultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View heroView = inflater.inflate(R.layout.color_default, parent, false);
        ColorDefaultAdapter.ViewHolder viewHolder = new ColorDefaultAdapter.ViewHolder(heroView);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull ColorDefaultAdapter.ViewHolder holder, int position) {
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
                //save to prefec
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("defaultColor", tags.getCode());
                editor.apply();

                dialog.dismiss();
//                Toast.makeText(mContext, "Data saved", Toast.LENGTH_SHORT).show();
                onDefaultColor.onChangeColor(tags.getCode());
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