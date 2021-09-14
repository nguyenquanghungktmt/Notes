package com.example.product_notes;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    public static final String SHARED_PREFS = "sharedPrefs";

    private Context mContext;
    private ArrayList<Notes> mNotes;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private SQLite db = null;

    private SharedPreferences prefs;
    private String defaultItemHeight;

    public NotesAdapter(Context mContext, ArrayList<Notes> mNotes) {
        this.mContext = mContext;
        this.mNotes = mNotes;
        this.db = new SQLite(mContext);
    }


    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View heroView = inflater.inflate(R.layout.item_note, parent, false);
        NotesAdapter.ViewHolder viewHolder = new NotesAdapter.ViewHolder(heroView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
        prefs = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        defaultItemHeight = prefs.getString("defaultItemHeight", "Tiny");
        Log.d("myTag", "default item height: " + defaultItemHeight);

        //set item height
        if (defaultItemHeight.equals("Tiny")) {
            holder.layoutItem.setMinimumHeight(50);
        } else if (defaultItemHeight.equals("Small")) {
            holder.swipeRevealLayout.setMinimumHeight(R.dimen.item_height_small);
        } else holder.swipeRevealLayout.setMinimumHeight(R.dimen.item_height_normal);

        Notes note = mNotes.get(position);
        if (note == null) {
            return;
        }
        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(note.getId()));
        holder.mTitle.setText(note.getTitle());

        holder.mContent.setText(note.getContent());

        holder.layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNotes.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                db.deleteNote(note.getId());
                db.addNoteTrash(note);
            }
        });


        holder.layoutArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNotes.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                db.updateNote(new Notes(note.getId(), note.getTitle(), note.getContent(), note.getTag(), note.getType(), note.getLocation(), 1, note.getReminder(), note.getCreated_at(), note.getUpdated_at()));
            }
        });
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(note.getCreated_at()));
        holder.txtDay.setText(simpleDateFormat.format(calendar.getTime()));

        if (note.getTag().equals(mContext.getResources().getString(R.color.background_color).toUpperCase()) == false)
            holder.images_file.setColorFilter(Color.parseColor(note.getTag()));

        if (note.getLocation().equals("")) {
            holder.txtLocation.setText("Vị trí");
        } else {
            holder.txtLocation.setText(note.getLocation());
        }


        if (note.getType().equals("TEXT")) {
            holder.is_checklist.setVisibility(View.GONE);
            holder.layoutLocation.setVisibility(View.VISIBLE);
        } else {
            holder.is_checklist.setVisibility(View.VISIBLE);
            holder.layoutLocation.setVisibility(View.INVISIBLE);
        }

        holder.layoutDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (note.getType().equals("TEXT")) {
                    Intent i = new Intent(mContext, DetailsTextActivity.class);
                    i.putExtra("getNotes", note);
                    mContext.startActivity(i);
                } else {
                    Intent i = new Intent(mContext, DetailsChecklistActivity.class);
                    i.putExtra("getNotes", note);
                    mContext.startActivity(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle, mContent, txtLocation, txtDay;
        private SwipeRevealLayout swipeRevealLayout;
        private LinearLayout layoutDelete, layoutArchive, layoutDetails, layoutLocation, layoutItem;
        private ImageView images_file, is_checklist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.txt_title);
            mContent = itemView.findViewById(R.id.txt_content);
            txtLocation = itemView.findViewById(R.id.txt_location);
            swipeRevealLayout = itemView.findViewById(R.id.swipeRevealLayout);
            layoutDelete = itemView.findViewById(R.id.layout_delete);
            layoutLocation = itemView.findViewById(R.id.location);
            images_file = itemView.findViewById(R.id.images_file);
            is_checklist = itemView.findViewById(R.id.is_checklist);
            layoutDetails = itemView.findViewById(R.id.toDetails);
            layoutArchive = itemView.findViewById(R.id.layout_archive);
            txtDay = itemView.findViewById(R.id.txt_date);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }

    public void replaceData(ArrayList<Notes> notesList) {
        mNotes.clear();
        mNotes.addAll(notesList);
        notifyDataSetChanged();
    }

    public void replaceData(Notes notes) {

    }

}