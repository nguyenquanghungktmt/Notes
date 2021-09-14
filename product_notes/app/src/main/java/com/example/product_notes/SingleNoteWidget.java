package com.example.product_notes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class SingleNoteWidget extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Log.d("myTag", "ID = " + appWidgetId);
            SharedPreferences prefs = context.getSharedPreferences(DialogChooseWidgetActivity.SHARED_PREF_FILE, Context.MODE_PRIVATE);

            String content = prefs.getString(DialogChooseWidgetActivity.KEY_NOTE_CONTENT, "Empty Note");
            String title = prefs.getString(DialogChooseWidgetActivity.KEY_NOTE_TITLE, "No Title Note");

            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.single_note_widget);
//            views.setTextViewText(R.id.title_single_note_widget, content);
            views.setTextViewText(R.id.title_single_note_widget, title);
            views.setInt(R.id.title_single_note_widget, "setColorFilter", android.graphics.Color.BLACK);
            views.setCharSequence(R.id.content_single_note_widget, "setText", content);


            Intent launchActivity = new Intent(context, DialogChooseWidgetActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchActivity, 0);
            views.setOnClickPendingIntent(R.id.widget_chose_single_note, pendingIntent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.updateAppWidget(appWidgetId - 1, views);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}