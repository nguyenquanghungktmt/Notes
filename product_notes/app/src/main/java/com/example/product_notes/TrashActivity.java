package com.example.product_notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class TrashActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Notes> notes;
    private NotesTrashAdapter notesAdapter;
    private LinearLayout iconBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);
        recyclerView = findViewById(R.id.recycler_Notes_Trash);
        SQLite db = new SQLite(this);
        notes = db.getAllNotesTrash();

        notesAdapter = new NotesTrashAdapter(this,notes);
        recyclerView.setAdapter(notesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        iconBack = findViewById(R.id.back);
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}