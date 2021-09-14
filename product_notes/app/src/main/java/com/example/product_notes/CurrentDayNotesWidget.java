package com.example.product_notes;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class CurrentDayNotesWidget extends AppWidgetProvider {
    public static final String TOAST_ACTION = "com.example.android.listwidget.TOAST_ACTION";
    public static final String EXTRA_ITEM = "com.example.android.listwidget.EXTRA_ITEM";
    private static final String OPEN_ACTION = "com.example.android.listwidget.OPEN";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("myTag", "Update next time");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, appWidgetId);
            // Here we setup the intent which points to the StackViewService which will
            // provide the views for this collection.
            Intent intent = new Intent(context, ListWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//            intent.putExtra("AppWidgetManager", (Serializable) appWidgetManager);
//            intent.putExtra("AppWidgetIDs", appWidgetIds);

            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.current_day_notes_widget);
            //set views by adapter
            rv.setRemoteAdapter(appWidgetId, R.id.list_view_notes, intent);

            //set Time
            String dateString = new SimpleDateFormat("EEE, MMM dd").format(Calendar.getInstance().getTime());
            rv.setTextViewText(R.id.title_current_day_note_widget, context.getResources().getString(R.string.date_format, dateString));

            // The empty view is displayed when the collection has no items. It should be a sibling
            // of the collection view.
            rv.setEmptyView(R.id.list_view_notes, R.id.empty_view);

            // Here we setup the a pending intent template. Individuals items of a collection
            // cannot setup their own pending intents, instead, the collection as a whole can
            // setup a pending intent template, and the individual items can set a fillInIntent
            // to create unique before on an item to item basis.
//            Intent toastIntent = new Intent(context, CurrentDayNotesWidget.class);
//            toastIntent.setAction(CurrentDayNotesWidget.TOAST_ACTION);
//            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
//            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//            rv.setPendingIntentTemplate(R.id.list_view_notes, toastPendingIntent);


            Intent launchActivity = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchActivity, 0);
            rv.setOnClickPendingIntent(R.id.title_current_day_note_widget, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(TOAST_ACTION)) {
//            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
            Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();


//            Intent intentOpen = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//            // Find an activity to hand the intent and start that activity.
//            if (intentOpen.resolveActivity(context.getPackageManager()) != null) {
//                context.startActivity(intent);
//            } else {
//                Log.d("ImplicitIntents", "Can't handle this intent!");
//            }
        }
        super.onReceive(context, intent);
    }
}