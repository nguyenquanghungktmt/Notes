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
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.product_notes.ColorDefaultAdapter.SHARED_PREFS;

public class AddTextActivity extends AppCompatActivity implements FetchAddressTask.OnTaskCompleted {
    private static final int SELECT_PHOTO = 1;
    private static final int LAUNCH_SECOND_REMINDER = 2;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private SQLite database;

    private LinearLayout btnSave, btnBack, layoutContent;
    private TextView createAt;
    private EditText editTextTitle, editTextContent;
    private TextView txtLocation;

    private ArrayList<Tags> listTags;
    private ArrayList<File> listFiles;

    private RecyclerView recyclerFile, recyclerTags;
    private FileAdapter fileAdapter;

    private LinearLayout layoutReminder, layoutUpload, layoutGetLocation, layoutArchive, layoutDisable;

    private FusedLocationProviderClient mFusedLocationClient;

    private int noteID;
    private String dataTitle, dataContent, dataLocation, dataTag, dataReminder, dataCreateAt;
    private String datetime = null;
    private Date currentTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);

        database = new SQLite(this);
        listTags = database.getAllTags();
        listFiles = new ArrayList<>();

        noteID = database.getAllNotes().size();

        connectView();

        init();

        //get time if create Note by Day
        Intent receiveIntent = getIntent();
        dataCreateAt = receiveIntent.getStringExtra("addNoteByDay");

        //initialize recycler view file
        listFiles = new ArrayList();
        fileAdapter = new FileAdapter(this, listFiles);
        recyclerFile.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerFile.setAdapter(fileAdapter);


        //set create time
        currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        createAt.setText(fmt.format(currentTime));

        // set data for tag recycler
        recyclerTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        TagsAdapter tagsAdapter = new TagsAdapter(this, listTags);
        recyclerTags.setAdapter(tagsAdapter);
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

        //choose reminder
        layoutReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AddTextActivity.this, ReminderActivity.class);

                intent.putExtra("noteID", noteID);
                intent.putExtra("noteTitle", editTextTitle.getText().toString());
                intent.putExtra("noteContent", editTextContent.getText().toString());

                startActivityForResult(intent, LAUNCH_SECOND_REMINDER);
            }
        });

        //click layout archive
        layoutArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextTitle.getText().toString().equals("")){
                    Toast.makeText(AddTextActivity.this, "Please type the title", Toast.LENGTH_SHORT).show();
                }
                else {

                    Toast.makeText(AddTextActivity.this, "Archive Note", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //click layout disable
        layoutDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //choose upload file
        layoutUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                intent.putExtra("sendNote", saveNote);
                startActivityForResult(intent, SELECT_PHOTO);
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        layoutGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        //click btn back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // click btn save
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSaveNote();
            }
        });


    }

    private void onClickSaveNote() {
        //get data title, content, location
        dataTitle = editTextTitle.getText().toString();
        dataContent = editTextContent.getText().toString();
        dataLocation = txtLocation.getText().toString();

        if (dataTitle.equals("")) {
            Toast.makeText(AddTextActivity.this, "Please type the title", Toast.LENGTH_SHORT).show();
        } else {
            int color = Color.TRANSPARENT;

            Drawable background = layoutContent.getBackground();
            if (background instanceof ColorDrawable) {
                color = ((ColorDrawable) background).getColor();
            }

            dataTag = Utils.colorIntToHexString(color);

            dataReminder = "";
            if (datetime != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(datetime));
                dataReminder = String.valueOf(calendar.getTimeInMillis());
            }

            if (dataCreateAt == null) dataCreateAt = String.valueOf(currentTime.getTime());

            int idNote = database.addNote(new Notes(noteID, dataTitle, dataContent, dataTag, "TEXT",
                    dataLocation, 0, dataReminder, dataCreateAt,
                    dataCreateAt));

            database.addContent(new Content(dataContent, 0, idNote));
            if (!listFiles.isEmpty()) {
                for (int i = 0; i < listFiles.size(); i++) {
                    database.addFile(new File(listFiles.get(i).getUri()), idNote);
                }
            }
            finish();
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
        layoutGetLocation = findViewById(R.id.get_location);
        layoutArchive = findViewById(R.id.layout_archive);
        layoutDisable = findViewById(R.id.layout_disable);
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
                            new FetchAddressTask(AddTextActivity.this,
                                    AddTextActivity.this).execute(location);
                            txtLocation.setText(getString(R.string.loading));
                            Log.d("myTag", "have location");
                        } else {
                            txtLocation.setText(R.string.no_location);
                        }
                    }
            );
        }
    }

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

    private void setFontSize(int titleSize, int contentSize) {
        editTextTitle.setTextSize(titleSize);
        editTextContent.setTextSize(contentSize);
    }

    public void init() {
        layoutArchive.setVisibility(View.GONE);

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
                Toast.makeText(AddTextActivity.this, "Error choose file", Toast.LENGTH_SHORT).show();
            }

        }

        if (requestCode == LAUNCH_SECOND_REMINDER) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");

                datetime = result;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(AddTextActivity.this, "Error set reminder", Toast.LENGTH_SHORT).show();
            }
        }
    }
}