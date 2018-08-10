package notes.rednitrogen.com.rednotes.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

import notes.rednitrogen.com.rednotes.Notes;
import notes.rednitrogen.com.rednotes.R;
import notes.rednitrogen.com.rednotes.database.DatabaseHelper;
import notes.rednitrogen.com.rednotes.database.model.Note;

import static notes.rednitrogen.com.rednotes.widget.WidgetConfig.POSITION_VALUE;
import static notes.rednitrogen.com.rednotes.widget.WidgetConfig.SHARED_PREFS;

/**
 * Implementation of App Widget functionality.
 */
public class RedNotesWidget extends AppWidgetProvider {

    private List<Note> notesList = new ArrayList<>();
    private DatabaseHelper db;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        int position;

        db = new DatabaseHelper(context);
        notesList.addAll(db.getAllNotes());

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

            Intent mainintent = new Intent(context, Notes.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,mainintent,0);

            SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            position = prefs.getInt(POSITION_VALUE+appWidgetId , 0);

            Intent intentUpdate = new Intent(context, RedNotesWidget.class);
            intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int[] ids = appWidgetIds;
            intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            PendingIntent pInt = PendingIntent.getBroadcast(context, 0, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent serviceIntent = new Intent(context, WidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            serviceIntent.putExtra("noteText", notesList.get(position).getNote());
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.red_notes_widget);
            views.setOnClickPendingIntent(R.id.widget_layout,pendingIntent);
            views.setOnClickPendingIntent(R.id.appwidget_refresh,pInt);
            views.setTextViewText(R.id.appwidget_title, notesList.get(position).getTitle());
            views.setRemoteAdapter(R.id.widget_listview, serviceIntent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
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

