package com.example.product_notes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DialogChooseWidgetActivity extends AppCompatActivity {
    public static final String SHARED_PREF_FILE = "com.example.product_notes.prefs";
    public static final String KEY_NOTE_TITLE = "com.example.product_notes.keyNoteTitle";
    public static final String KEY_NOTE_CONTENT = "com.example.product_notes.keyNoteContent";
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private EditText editTextButton;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_widget_dialog);

        context = getApplicationContext();

        Intent configIntent = getIntent();
        Bundle extras = configIntent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_CANCELED, resultValue);
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        this.setFinishOnTouchOutside(true);
        TextView mTitleNoteDialog = findViewById(R.id.dialog_title_note);

        SQLite db = new SQLite(this);
        ArrayList<Notes> mNotes = db.getAllNotes();

        ListView listView = findViewById(R.id.dialog_note_list);
        listView.setAdapter(new CustomListViewDialogAdapter(this, mNotes));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Notes currentNote = (Notes) listView.getItemAtPosition(position);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

                Intent intent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.single_note_widget);

                views.setOnClickPendingIntent(R.id.content_single_note_widget, pendingIntent);
                views.setOnClickPendingIntent(R.id.title_single_note_widget, pendingIntent);

                String content = currentNote.getContent();
                String title = currentNote.getTitle();

                views.setCharSequence(R.id.content_single_note_widget, "setText", content);
                views.setTextViewText(R.id.title_single_note_widget, title);
                appWidgetManager.updateAppWidget(appWidgetId, views);
                appWidgetManager.updateAppWidget(appWidgetId - 1, views);

                SharedPreferences prefs = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(KEY_NOTE_TITLE, content);
                editor.putString(KEY_NOTE_CONTENT, title);

                editor.apply();
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();



                Toast.makeText(getApplicationContext(), "Selected :" + currentNote.getTitle(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

}