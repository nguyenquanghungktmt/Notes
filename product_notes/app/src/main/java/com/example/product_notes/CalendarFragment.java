package com.example.product_notes;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class CalendarFragment extends Fragment {
    private View view;
    private Context context;

    private RecyclerView recyclerView;
    private NotesAdapter mNotesAdapter;
    private CalendarView calendarView;

    private ArrayList<Notes> mNotes;
    private ArrayList<Notes> notesArrayList;

    private TextView txtNotes;

    private SimpleDateFormat formatData;

    private Calendar calendar;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLite db = new SQLite(getContext());
        mNotes = db.getAllNotes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        context = view.getContext();

        recyclerView = view.findViewById(R.id.recycler_Notes);
        txtNotes = view.findViewById(R.id.txt_notes_day);
        calendarView = view.findViewById(R.id.calendarView);


        calendar = Calendar.getInstance();
        notesArrayList = new ArrayList<>();

        SimpleDateFormat curFormater = new SimpleDateFormat("MM/dd/yyyy");
        calendar.setTime(new Date(curFormater.format(calendar.getTime())));

        //getN Note by Day
        for (int i = 0; i < mNotes.size(); i++) {
            if (Long.parseLong(mNotes.get(i).getCreated_at()) - calendar.getTimeInMillis() < 86400000 && Long.parseLong(mNotes.get(i).getCreated_at()) > calendar.getTimeInMillis()) {
                notesArrayList.add(mNotes.get(i));
            }
        }

        mNotesAdapter = new NotesAdapter(getContext(), notesArrayList);
        recyclerView.setAdapter(mNotesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mNotesAdapter.notifyDataSetChanged();

        formatData = new SimpleDateFormat("dd MMM");
        if (notesArrayList.isEmpty()) {
            txtNotes.setText(formatData.format(new Date()) + " Chưa có ghi chú mới ");
        } else {
            txtNotes.setText(formatData.format(new Date()) + " Có " + notesArrayList.size() + " ghi chú mới");
        }

//        calendarView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                showDialogAddNote(year, month, dayOfMonth);

                showNoteByDay(year, month, dayOfMonth);
            }
        });


        return view;
    }

    private void showNoteByDay(int year, int month, int dayOfMonth){
        //1 ngày 86,400 giây
        calendar.setTime(new Date((month + 1) + "/" + dayOfMonth + "/" + year));

        notesArrayList.clear();
        for (Notes note: mNotes) {
            long tmpTime = Long.parseLong(note.getCreated_at()) - calendar.getTimeInMillis();

            if (tmpTime  < 86400000 && tmpTime >= 0) {
                notesArrayList.add(note);
            }
        }
        mNotesAdapter.notifyDataSetChanged();

        if (notesArrayList.isEmpty()) {
            txtNotes.setText(formatData.format(new Date((month + 1) + "/" + dayOfMonth + "/" + year)) + " Chưa có ghi chú mới ");
        } else {
            txtNotes.setText(formatData.format(new Date((month + 1) + "/" + dayOfMonth + "/" + year)) + " Có " + notesArrayList.size() + " ghi chú mới");
        }
    }

    private void showDialogAddNote(int year, int month, int dayOfMonth) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_note_calendar);
        dialog.setCancelable(true);

        TextView txtDate, btnAddNote;
        txtDate = dialog.findViewById(R.id.txt_date);
        btnAddNote = dialog.findViewById(R.id.btn_add_note);

        txtDate.setText(formatData.format(new Date((month + 1) + "/" + dayOfMonth + "/" + year)));


        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogChooseTypeNote(year, month, dayOfMonth);
                dialog.dismiss();
                Toast.makeText(getContext(), "Click add note", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogChooseTypeNote(int year, int month, int dayOfMonth) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Add Note");
        alertDialog.setCancelable(true);
//        alertDialog.setIcon(R.drawable.ic_baseline_add_24);
//        alertDialog.setIcon(R.drawable.img_notes);

        String[] items = {"Text", "CheckList"};
        alertDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                Date currentTime = new Date((month + 1) + "/" + dayOfMonth + "/" + year);

                switch (choice) {
                    case 0:
                        Intent intentAddText = new Intent(getContext(), AddTextActivity.class);

//                        Log.d("myTag", "Calendar: " + String.valueOf(currentTime.getTime()));
                        intentAddText.putExtra("addNoteByDay", String.valueOf(currentTime.getTime()));
                        startActivity(intentAddText);

//                        Toast.makeText(getContext(), "Clicked on Text", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Intent intentAddChecklist = new Intent(getContext(), AddChecklistActivity.class);

                        intentAddChecklist.putExtra("addCheckListByDay", String.valueOf(currentTime.getTime()));
                        startActivity(intentAddChecklist);
//                        Toast.makeText(getContext(), "Clicked on Checklist", Toast.LENGTH_LONG).show();
                        break;
                }

//                SQLite db = new SQLite(getContext());
//                mNotes = db.getAllNotes();
                showNoteByDay(year, month, dayOfMonth);
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

}