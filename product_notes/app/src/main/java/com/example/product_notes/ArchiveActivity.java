package com.example.product_notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.chauthai.swipereveallayout.SwipeRevealLayout;

import java.util.ArrayList;

public class ArchiveActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Notes> notes;
    private NotesArchiveAdapter archiveAdapter;
    private LinearLayout iconBack;
    private SQLite db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        db = new SQLite(this);
        recyclerView = findViewById(R.id.recycler_Notes_Archive);
        archiveAdapter = new NotesArchiveAdapter(this,new ArrayList<>());
        recyclerView.setAdapter(archiveAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notes = db.getAllNotesArchive();
        archiveAdapter.replaceData(notes);
        iconBack = findViewById(R.id.back);
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}