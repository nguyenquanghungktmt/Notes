package com.example.product_notes;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.product_notes.ColorDefaultAdapter.SHARED_PREFS;

public class DetailsChecklistActivity extends AppCompatActivity {
    private static final int LAUNCH_SECOND_REMINDER = 2;

    private SQLite db;
    private Notes note;

    private ArrayList<Content> listContents;
    private ArrayList<Tags> listTags;

    private RecyclerView recyclerViewChecklist, recyclerTags;
    private RecyclerViewAdapter checklistAdapter;
    private TagsAdapter tagsAdapter;

    private LinearLayout btnSave, btnBack, btnAdd, layoutContent, layoutSend, layoutReminder, layoutDelete, layoutArchive, layoutDisable;

    private TextView createAt;
    private EditText editTextTitle, editTextContentDialog;

    private Dialog dialog;

    private Button btnAcceptDialog, btnCancelDialog;

    private ItemTouchHelper touchHelper;

    private String datetime = null;
    private Date currentTime = null;
    private String dataTitle, dataContent, dataLocation, dataTag, dataReminder, dataCreateAt, dataUpdateAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_checklist);

        //get note by intent
        Intent intent = getIntent();
        note = intent.getParcelableExtra("getNotes");

        // connect database
        db = new SQLite(this);

        // find all views by ID
        connectView();

        init();

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(note.getCreated_at()));
        createAt.setText(fmt.format(calendar.getTime()));

//        dialog = new Dialog(this);
//        dialog.setCancelable(false);
//        dialog.setContentView(R.layout.dialog_txt_content);

        layoutContent.setBackgroundColor(Color.parseColor(note.getTag()));

        editTextTitle.setText(note.getTitle());

        // get list content of current note
        listContents = new ArrayList<>();
        listContents = db.getAllContentByIDNote(note.getId());

        ItemTouchHelper.Callback callback =
                new ItemMoveCallback(checklistAdapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerViewChecklist);

        //bind checklist adapter to recycler view
        checklistAdapter = new RecyclerViewAdapter(this, listContents);
        recyclerViewChecklist.setAdapter(checklistAdapter);
        recyclerViewChecklist.setLayoutManager(new LinearLayoutManager(this));

        // get all tags
        listTags = db.getAllTags();
        recyclerTags = findViewById(R.id.recycler_Tags);
        recyclerTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tagsAdapter = new TagsAdapter(this, listTags);
        tagsAdapter.setOnDataCallback(new OnDataCallback() {
            @Override
            public void onColorBackgroundChange(int color) {
                layoutContent.setBackgroundColor(color);
            }


            @Override
            public void onmNotesChange() {

            }

            @Override
            public void onColorChange(String code) {

            }
        });
        recyclerTags.setAdapter(tagsAdapter);


        checklistAdapter.setOnDataCallback(new OnDataCallBackChecklist() {
            @Override
            public void onShowDialog(int i) {
                dialog.show();
                btnAcceptDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listContents.set(i, new Content(editTextContentDialog.getText().toString()));
                        checklistAdapter.notifyItemChanged(i);
                        editTextContentDialog.setText("");
                        dialog.hide();
                    }
                });
            }

        });

        //onclick btn cancel dialog
        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });

        //onclick btn add checklist
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listContents.add(new Content("", 0, 0));
                checklistAdapter.notifyDataSetChanged();
            }
        });

        // onclick btn back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // onclick layout reminder on bottom sheet
        layoutReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(DetailsChecklistActivity.this, ReminderActivity.class);

                intent.putExtra("noteID", note.getId());
                intent.putExtra("noteTitle", editTextTitle.getText().toString());

                String sendContent = "";
                for (Content content : listContents)
                    sendContent += content.getContent() + "\n";

                intent.putExtra("noteContent", sendContent);

                startActivityForResult(intent, LAUNCH_SECOND_REMINDER);
            }
        });

        //click layout Delete
        layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete note in database
                db.deleteNote(note.getId());
                db.addNoteTrash(note);

                Toast.makeText(DetailsChecklistActivity.this, "Deleted Note", Toast.LENGTH_SHORT).show();
                onClickSaveNote();
            }
        });

        //click layout Archive
        layoutArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //archive note
                db.updateNote(new Notes(note.getId(), note.getTitle(), note.getContent(), note.getTag(), note.getType(), note.getLocation(), 1, note.getReminder(), note.getCreated_at(), note.getUpdated_at()));
                Toast.makeText(DetailsChecklistActivity.this, "Archived Note", Toast.LENGTH_SHORT).show();
            }
        });

        //click layout Disable
        layoutDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //disable archive note
                db.updateNote(new Notes(note.getId(), note.getTitle(), note.getContent(), note.getTag(), note.getType(), note.getLocation(), 0, note.getReminder(), note.getCreated_at(), note.getUpdated_at()));
                Toast.makeText(DetailsChecklistActivity.this, "Disable archive Note", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        //onclick save note
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSaveNote();
            }
        });

        // onclick  layout send son bottom sheet
        layoutSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "";
                for (int i = 0; i < listContents.size(); i++) {
                    text += (i + 1) + ". " + listContents.get(i).getContent() + "\n";
                }

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Send this message to"));
            }
        });
    }

    private void onClickSaveNote() {
        dataTitle = editTextTitle.getText().toString();
        Calendar calendar = Calendar.getInstance();
        dataUpdateAt = String.valueOf(calendar.getTimeInMillis());
//                calendar.setTime(new Date(datetime));
        if (dataTitle.equals("")) {
            Toast.makeText(DetailsChecklistActivity.this, "Please type the title", Toast.LENGTH_SHORT).show();
        } else if (!listContents.isEmpty()) {
            dataContent = listContents.get(0).getContent();

            int color = Color.TRANSPARENT;
            Drawable background = layoutContent.getBackground();
            if (background instanceof ColorDrawable) {
                color = ((ColorDrawable) background).getColor();
            }

            String tag = Utils.colorIntToHexString(color);
            db.updateNote(new Notes(note.getId(), dataTitle, dataContent, tag, "CHECKLIST",
                    "", note.getIs_archive(), String.valueOf(calendar.getTimeInMillis()),
                    note.getCreated_at(), dataUpdateAt));
            db.deleteContent(note.getId());
            for (int i = 0; i < listContents.size(); i++) {
                db.addContent(new Content(listContents.get(i).getContent(), 0, note.getId()));
            }

            dialog.dismiss();
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            db.deleteContent(note.getId());
            int color = Color.TRANSPARENT;
            Drawable background = layoutContent.getBackground();
            if (background instanceof ColorDrawable) {
                color = ((ColorDrawable) background).getColor();
            }
            String tag = Utils.colorIntToHexString(color);
            db.updateNote(new Notes(dataTitle, "", tag, "CHECKLIST", "",
                    0, null, note.getCreated_at(),
                    String.valueOf(calendar.getTimeInMillis())));
            dialog.dismiss();
            setResult(Activity.RESULT_OK);
            finish();
        }
    }


    private void connectView() {
        //header
        btnBack = findViewById(R.id.back);
        btnSave = findViewById(R.id.save);

        // init dialog
        dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_txt_content);

        //get view dialog
        btnAcceptDialog = dialog.findViewById(R.id.accept_dialog);
        btnCancelDialog = dialog.findViewById(R.id.cancel_dialog);
        editTextContentDialog = dialog.findViewById(R.id.txt_content_dialog);

        createAt = findViewById(R.id.txt_create_at);
        editTextTitle = findViewById(R.id.txt_title_checklist);
        layoutContent = findViewById(R.id.layout_content);

        // connect recycler views
        recyclerViewChecklist = findViewById(R.id.recycler_checklist);
        recyclerTags = findViewById(R.id.recycler_Tags);

        //btn add item
        btnAdd = findViewById(R.id.layout_Add);

        // layout in bottom navigation
        layoutSend = findViewById(R.id.layout_send);
        layoutReminder = findViewById(R.id.navigateReminder);
        layoutDelete = findViewById(R.id.layout_delete);
        layoutArchive = findViewById(R.id.layout_archive);
        layoutDisable = findViewById(R.id.layout_disable);
    }


    public void init() {
        if (note.getIs_archive() == 0) {
            layoutDisable.setVisibility(View.GONE);
        } else {
            layoutArchive.setVisibility(View.GONE);
        }

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String fontSize = sharedPreferences.getString("defaultFontSize", "Tiny");

        editTextTitle = findViewById(R.id.txt_title_checklist);

        if (fontSize.equals("Tiny")) {
            editTextTitle.setTextSize(18);
        } else if (fontSize.equals("Small")) {
            editTextTitle.setTextSize(20);
        } else if (fontSize.equals("Medium")) {
            editTextTitle.setTextSize(24);
        } else if (fontSize.equals("Large")) {
            editTextTitle.setTextSize(26);
        } else if (fontSize.equals("Huge")) {
            editTextTitle.setTextSize(28);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_SECOND_REMINDER) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");

                datetime = result;
//                System.out.println("--ahihi datetime "+datetime);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }
}