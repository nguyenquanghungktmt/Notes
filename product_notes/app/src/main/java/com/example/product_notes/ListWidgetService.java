package com.example.product_notes;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{
        private int count = 0;
        private ArrayList<NoteWidgetItem> widgetItems = new ArrayList<NoteWidgetItem>();
        private Context context;
        private int appWidgetId;
//        private AppWidgetManager appWidgetManager;
//        private int[] appWidgetIds;

        public ListRemoteViewsFactory(Context applicationContext, Intent intent) {
            context = applicationContext;
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
//            appWidgetManager = (AppWidgetManager) intent.getSerializableExtra("AppWidgetManager");
//            appWidgetIds = intent.getIntArrayExtra("AppWidgetIDs");
        }

        @Override
        public void onCreate() {
            //load data from database
            SQLite db = new SQLite(context);
            ArrayList<Notes> mNotes = db.getAllNotes();
//            Log.d("myTag", "remind: " + mNotes.get(0).getReminder());

            for (Notes note : mNotes){
                widgetItems.add(new NoteWidgetItem(note.getTitle()));
            }
            count = widgetItems.size();

            Log.d("myTag", "size db = " + count);

//            for (int i = 0; i < count; i++){
//                widgetItems.add(new NoteWidgetItem("Note " + i));
//                Log.d("myTag", widgetItems.get(i).title);
//            }
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void onDataSetChanged() {
//            if (appWidgetManager != null)
//                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, appWidgetId);
            SQLite db = new SQLite(context);
            ArrayList<Notes> mNotes = db.getAllNotes();
//            Log.d("myTag", "remind: " + mNotes.get(0).getReminder());

            widgetItems.clear();
            for (Notes note : mNotes){
                widgetItems.add(new NoteWidgetItem(note.getTitle()));
            }
            count = widgetItems.size();
            Log.d("myTag", "change db size = " + count);
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return widgetItems.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.item_widget_note);
            rv.setTextViewText(R.id.item_widget_note, widgetItems.get(position).getTitle());

            Bundle extras = new Bundle();
            extras.putInt(CurrentDayNotesWidget.EXTRA_ITEM, position);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            rv.setOnClickFillInIntent(R.id.item_widget_note, fillInIntent);

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
