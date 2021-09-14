package com.example.product_notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListViewDialogAdapter extends BaseAdapter {
    private ArrayList<Notes> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomListViewDialogAdapter(Context aContext,  ArrayList<Notes> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_listview_dialog_dialog, null);
            holder = new ViewHolder();
            holder.titleNoteDialog = (TextView) convertView.findViewById(R.id.dialog_title_single_note);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Notes note = this.listData.get(position);
        holder.titleNoteDialog.setText(note.getTitle());

        return convertView;
    }
    static class ViewHolder {
        TextView titleNoteDialog;
    }
}
