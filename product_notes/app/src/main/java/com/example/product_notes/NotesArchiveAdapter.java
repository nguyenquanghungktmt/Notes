package com.example.product_notes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.ArrayList;

public class NotesArchiveAdapter extends RecyclerView.Adapter<NotesArchiveAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Notes> mNotes;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private SQLite db = null;
    public NotesArchiveAdapter(Context mContext, ArrayList<Notes> mNotes) {
        this.mContext = mContext;
        this.mNotes = mNotes;
        this.db = new SQLite(mContext);
    }
    @NonNull
    @Override
    public NotesArchiveAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View heroView = inflater.inflate(R.layout.item_note_archive, parent, false);
        NotesArchiveAdapter.ViewHolder viewHolder = new NotesArchiveAdapter.ViewHolder(heroView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotesArchiveAdapter.ViewHolder holder, int position) {
        Notes note = mNotes.get(position);
        if (note == null){
            return;
        }
        viewBinderHelper.bind(holder.swipeRevealLayout,String.valueOf(note.getId()));
        holder.mTitle.setText(note.getTitle());
        holder.mContent.setText(note.getContent());
        holder.layoutDeleteTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNotes.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                db.updateNote(new Notes(note.getId(),note.getTitle(),note.getContent(),note.getTag(),note.getType(),note.getLocation(),0,note.getReminder(),note.getCreated_at(),note.getUpdated_at()));
            }
        });
        
        if (note.getTag().equals(mContext.getResources().getString(R.color.background_color).toUpperCase()) == false)
            holder.images_file.setColorFilter(Color.parseColor(note.getTag()));


        if (note.getLocation().equals("")){
            holder.txtLocation.setText("Vị trí");
        }else{
            holder.txtLocation.setText(note.getLocation());
        }

        if (note.getType().equals("TEXT")){
            holder.is_checklist.setVisibility(View.GONE);
            holder.layoutLocation.setVisibility(View.VISIBLE);
        }else{
            holder.is_checklist.setVisibility(View.VISIBLE);
            holder.layoutLocation.setVisibility(View.INVISIBLE);
        }

        holder.layoutDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (note.getType().equals("TEXT")){
                    Intent i = new Intent(mContext, DetailsTextActivity.class);
                    i.putExtra("getNotes", note);
                    mContext.startActivity(i);
                }else{
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

    public void replaceData(ArrayList<Notes> notesList) {
        mNotes.clear();
        mNotes.addAll(notesList);
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mContent,txtLocation;
        private SwipeRevealLayout swipeRevealLayout;
        private LinearLayout layoutDeleteTrash,layoutDetails, layoutLocation;
        private ImageView images_file,is_checklist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.txt_title);
            mContent = itemView.findViewById(R.id.txt_content);
            swipeRevealLayout = itemView.findViewById(R.id.swipeRevealLayout_archive);
            layoutDeleteTrash = itemView.findViewById(R.id.layout_delete_trash);
            layoutLocation = itemView.findViewById(R.id.location);
            images_file = itemView.findViewById(R.id.images_file);
            txtLocation = itemView.findViewById(R.id.txt_location);
            layoutDetails = itemView.findViewById(R.id.toDetails);
            is_checklist = itemView.findViewById(R.id.is_checklist);
        }
    }
}