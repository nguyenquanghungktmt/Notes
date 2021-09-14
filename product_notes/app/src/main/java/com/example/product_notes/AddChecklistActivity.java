package com.example.product_notes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.product_notes.ColorDefaultAdapter.SHARED_PREFS;

public class AddChecklistActivity extends AppCompatActivity {
    private static final int LAUNCH_SECOND_REMINDER = 2;

    private SQLite db;

    private ArrayList<Content> listContents;
    private ArrayList<Tags> listTags;

    private RecyclerView recyclerViewChecklist, recyclerTags;
    private RecyclerViewAdapter checklistAdapter;
    private TagsAdapter tagsAdapter;

    private LinearLayout btnSave, btnBack, btnAdd, layoutContent, layoutReminder, layoutDisable;

    private TextView createAt;
    private EditText editTextTitle, editTextContentDialog;

    private Dialog dialog;

    private Button btnAcceptDialog, btnCancelDialog;

    private ItemTouchHelper touchHelper;

    private int noteID;
    private String datetime = null;
    private Date currentTime = null;
    private String dataTitle, dataContent, dataLocation, dataTag, dataReminder, dataCreateAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_checklist);

        //connect database
        db = new SQLite(this);

        //generate note ID
        noteID = db.getAllNotes().size();

        // find all views by ID
        connectView();

        init();

        //get time if create Note by Day
        Intent receiveIntent = getIntent();
        dataCreateAt = receiveIntent.getStringExtra("addCheckListByDay");

        // set create_at time
        currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        createAt.setText(fmt.format(currentTime));


        //initialize list contents and bind to recycler view checklist
        listContents = new ArrayList<>();
        checklistAdapter = new RecyclerViewAdapter(this, listContents);
        recyclerViewChecklist.setAdapter(checklistAdapter);
        recyclerViewChecklist.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback =
                new ItemMoveCallback(checklistAdapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerViewChecklist);

        listTags = db.getAllTags();

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

        //btn cancel dialog
        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        //btn add
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listContents.add(new Content("", 0, 0));
                checklistAdapter.notifyDataSetChanged();
            }
        });

        // click button back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // click reminder
        layoutReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AddChecklistActivity.this, ReminderActivity.class);

                intent.putExtra("noteID", noteID);
                intent.putExtra("noteTitle", editTextTitle.getText().toString());

                String sendContent = "";
                for (Content content : listContents)
                    sendContent += content.getContent() + "\n";

                intent.putExtra("noteContent", sendContent);

                startActivityForResult(intent, LAUNCH_SECOND_REMINDER);
            }
        });

        //click layout disable
        layoutDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //click save
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSaveNote();
            }
        });

    }

    private void onClickSaveNote() {
        dataTitle = editTextTitle.getText().toString();
        dataReminder = "";

        if (datetime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(datetime));
            dataReminder = String.valueOf(calendar.getTimeInMillis());
        }

        if (dataTitle.equals("")) {
            Toast.makeText(AddChecklistActivity.this, "Nhập chủ đề", Toast.LENGTH_SHORT).show();
        }
        else if (!listContents.isEmpty()) {
            dataContent = listContents.get(0).getContent();

            //get color
            int color = Color.TRANSPARENT;
            Drawable background = layoutContent.getBackground();
            if (background instanceof ColorDrawable) {
                color = ((ColorDrawable) background).getColor();
            }

            dataTag = Utils.colorIntToHexString(color);

            if (dataCreateAt == null) dataCreateAt = String.valueOf(currentTime.getTime());

            int idNote = db.addNote(new Notes(noteID, dataTitle, dataContent, dataTag, "CHECKLIST",
                    "", 0, dataReminder, dataCreateAt, dataCreateAt));

            if (idNote > 0) {
                for (int i = 0; i < listContents.size(); i++) {
                    db.addContent(new Content(listContents.get(i).getContent(), 0, idNote));
                }
            }

            dialog.dismiss();
            setResult(Activity.RESULT_OK);

            finish();
        }
        else {

            int color = Color.TRANSPARENT;
            Drawable background = layoutContent.getBackground();
            if (background instanceof ColorDrawable) {
                color = ((ColorDrawable) background).getColor();
            }


            if (dataCreateAt == null) dataCreateAt = String.valueOf(currentTime.getTime());

            String tag = Utils.colorIntToHexString(color);
            db.addNote(new Notes(noteID, dataTitle, "", tag, "CHECKLIST", "", 0,
                    dataReminder, dataCreateAt, dataCreateAt));
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
        layoutReminder = findViewById(R.id.navigateReminder);
        layoutDisable = findViewById(R.id.layout_disable);
    }

    public void init() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        String text;
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            text =  "#FF000000";
        }
        else text = sharedPreferences.getString("defaultColor", "#FFFFFFFF");

        layoutContent.setBackgroundColor(Color.parseColor(text));

        String fontSize = sharedPreferences.getString("defaultFontSize", "Tiny");

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
                System.out.println("--ahihi datetime " + datetime);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }
}