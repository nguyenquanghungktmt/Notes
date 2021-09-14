package com.example.product_notes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.product_notes.ColorDefaultAdapter.SHARED_PREFS;

public class DetailsTextActivity extends AppCompatActivity implements FetchAddressTask.OnTaskCompleted {
    private static final int SELECT_PHOTO = 1;
    private static final int LAUNCH_SECOND_REMINDER = 2;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private SQLite database;

    private Notes currentNote, saveNote;

    private ArrayList<Tags> listTags;
    private ArrayList<File> listFiles;

    private LinearLayout btnSave, btnBack, layoutContent;
    private TextView createAt;
    private EditText editTextTitle, editTextContent;
    private TextView txtLocation;

    private RecyclerView recyclerFile, recyclerTags;
    private FileAdapter fileAdapter;

    private LinearLayout layoutReminder, layoutUpload, layoutLocation, layoutOnEdit, layoutArchive, layoutDisable, layoutSend;

    private FusedLocationProviderClient mFusedLocationClient;

    // specify data
    private String dataTitle, dataContent, dataLocation, dataTag, dataReminder;
    private String datetime = null;
    private Date currentTime = null;


    String reminder = null;
    String tag;
    String updated_at;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_text);

        //get Note when client clicks on a note in Home activity
        Intent intent = getIntent();
        currentNote = intent.getParcelableExtra("getNotes");

        connectView();

        init();

        restoreData();

        // get data from database
        database = new SQLite(this);
        listTags = database.getAllTags();

        listFiles = database.getAllFileByIdNote(currentNote.getId());
        fileAdapter = new FileAdapter(this, listFiles);
        recyclerFile.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerFile.setAdapter(fileAdapter);

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(currentNote.getCreated_at()));
        createAt.setText(fmt.format(calendar.getTime()));

        RecyclerView recyclerTags = findViewById(R.id.recycler_Tags);
        recyclerTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        TagsAdapter tagsAdapter = new TagsAdapter(this, listTags);
        recyclerTags.setAdapter(tagsAdapter);
        tagsAdapter.setOnDataCallback(new OnDataCallback() {
            @Override
            public void onColorBackgroundChange(int color) {
                layoutOnEdit.setBackgroundColor(color);
            }


            @Override
            public void onmNotesChange() {

            }

            @Override
            public void onColorChange(String code) {

            }
        });

        // click reminder field
        layoutReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendItemIntent = new Intent();
                sendItemIntent.setClass(DetailsTextActivity.this, ReminderActivity.class);

                sendItemIntent.putExtra("noteID", currentNote.getId());
                sendItemIntent.putExtra("noteTitle", editTextTitle.getText().toString());
                sendItemIntent.putExtra("noteContent", editTextContent.getText().toString());
                startActivityForResult(sendItemIntent, LAUNCH_SECOND_REMINDER);
            }
        });

        // choose layout upload
        layoutUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, SELECT_PHOTO);
            }
        });

        //click layout Archive
        layoutArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //archive note
                database.updateNote(new Notes(currentNote.getId(),
                        currentNote.getTitle(),
                        currentNote.getContent(),
                        currentNote.getTag(),
                        currentNote.getType(),
                        currentNote.getLocation(),
                        1,
                        currentNote.getReminder(),
                        currentNote.getCreated_at(),
                        currentNote.getUpdated_at()));
                Toast.makeText(DetailsTextActivity.this, "Archived Note", Toast.LENGTH_SHORT).show();
            }
        });


        //click layout Disable
        layoutDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //disable archive note
                database.updateNote(new Notes(currentNote.getId(),
                        currentNote.getTitle(),
                        currentNote.getContent(),
                        currentNote.getTag(),
                        currentNote.getType(),
                        currentNote.getLocation(),
                        1,
                        currentNote.getReminder(),
                        currentNote.getCreated_at(),
                        currentNote.getUpdated_at()));
                Toast.makeText(DetailsTextActivity.this, "Disable archive Note", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        // click on location and get location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LinearLayout layoutGetLocation = findViewById(R.id.get_location);
        layoutGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });


        // click icon back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // click icon save
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
                onClickSaveNote();
            }
        });

        // click on layout send to send data
        layoutSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, editTextContent.getText().toString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Send this message to"));
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                if (data != null && selectedImage != null) {
//                    Log.d("myTag", "get file success " + selectedImage.toString());

                    listFiles.add(new File(selectedImage.toString()));
                    fileAdapter.notifyDataSetChanged();
                }
            } else if (resultCode == RESULT_OK) {
                Toast.makeText(DetailsTextActivity.this, "Error choose file", Toast.LENGTH_SHORT).show();
            }

        }

        if (requestCode == LAUNCH_SECOND_REMINDER) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");

                datetime = result;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(DetailsTextActivity.this, "Error set reminder", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void connectView() {
        //header
        btnBack = findViewById(R.id.back);
        btnSave = findViewById(R.id.save);

        createAt = findViewById(R.id.txt_create_at);
        editTextTitle = findViewById(R.id.txt_title_note_text);
        editTextContent = findViewById(R.id.txt_content_note_text);
        layoutContent = findViewById(R.id.layout_content);
        txtLocation = findViewById(R.id.txt_location);

        // connect recycler views
        recyclerFile = findViewById(R.id.rcv_list_file_upload);
        recyclerTags = findViewById(R.id.recycler_Tags);

        // layout in bottom navigation
        layoutReminder = findViewById(R.id.navigateReminder);
        layoutUpload = findViewById(R.id.upload_file);
        layoutLocation = findViewById(R.id.get_location);
        layoutSend = findViewById(R.id.layout_send);
        layoutArchive = findViewById(R.id.layout_archive);
        layoutDisable = findViewById(R.id.layout_disable);
    }


    private void setFontSize(int titleSize, int contentSize) {
        editTextTitle.setTextSize(titleSize);
        editTextContent.setTextSize(contentSize);
    }

    public void init() {
        if (currentNote.getIs_archive() == 0) {
            layoutDisable.setVisibility(View.GONE);
        } else {
            layoutArchive.setVisibility(View.GONE);
        }

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String fontSize = sharedPreferences.getString("defaultFontSize", "Tiny");

        if (fontSize.equals("Tiny")) {
            setFontSize(18, 14);
        } else if (fontSize.equals("Small")) {
            setFontSize(20, 16);
        } else if (fontSize.equals("Medium")) {
            setFontSize(24, 18);
        } else if (fontSize.equals("Large")) {
            setFontSize(26, 20);
        } else if (fontSize.equals("Huge")) {
            setFontSize(28, 22);
        }
    }

    private void restoreData() {
        //restore color
        layoutOnEdit = findViewById(R.id.layout_onEdit);
        layoutOnEdit.setBackgroundColor(Color.parseColor(currentNote.getTag()));

        //restore title, content, location
        editTextTitle.setText(currentNote.getTitle());
        editTextContent.setText(currentNote.getContent());
        txtLocation.setText(currentNote.getLocation());

    }

    // Get location then convert into address
    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(
                    location -> {
                        if (location != null) {
                            // Start the reverse geocode AsyncTask
                            new FetchAddressTask(DetailsTextActivity.this,
                                    DetailsTextActivity.this).execute(location);
                            txtLocation.setText(getString(R.string.loading));
                            Log.d("myTag", "have location");
                        } else {
                            txtLocation.setText(R.string.no_location);
                        }
                    }
            );
        }
    }

    //request location permission
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                // otherwise, show a Toast
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this,
                            R.string.location_permission_denied,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onTaskCompleted(String result) {
        txtLocation.setText(result);
    }

    public void getData() {
        dataTitle = editTextTitle.getText().toString();
        dataLocation = txtLocation.getText().toString();
        dataContent = editTextContent.getText().toString();

        int color = Color.TRANSPARENT;
        Drawable background = layoutOnEdit.getBackground();
        if (background instanceof ColorDrawable) {
            color = ((ColorDrawable) background).getColor();
        }

        tag = Utils.colorIntToHexString(color);

        Calendar calendar = Calendar.getInstance();
        updated_at = String.valueOf(calendar.getTimeInMillis());

        if (datetime != null) {
            calendar.setTime(new Date(datetime));
            reminder = String.valueOf(calendar.getTimeInMillis());
        }

//        saveNote = new Notes(currentNote.getId(), dataTitle, dataContent, tag, "TEXT"
//                , dataLocation, currentNote.getIs_archive(), reminder, currentNote.getCreated_at(), updated_at);
    }


    private void onClickSaveNote() {
        if (dataTitle.equals("")) {
            Toast.makeText(DetailsTextActivity.this, "Please type the title", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("myTag", "onClickSaveNote: " + currentNote.getUpdated_at());
            Log.d("myTag", "onClickSaveNote: " + updated_at);
            //update Note in database
            database.updateNote(new Notes(currentNote.getId(), dataTitle, dataContent, tag, "TEXT",
                    dataLocation, currentNote.getIs_archive(), reminder,
                    currentNote.getCreated_at(), updated_at));
            database.updateContent(new Content(dataContent, 0, currentNote.getId()));

            if (!listFiles.isEmpty()) {
                database.deleteFiles(currentNote.getId());
                for (File file : listFiles) {
                    database.addFile(file, currentNote.getId());
                }
            } else {
                Log.d("myTag", "list file empty");
                database.deleteFiles(currentNote.getId());
            }

            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}