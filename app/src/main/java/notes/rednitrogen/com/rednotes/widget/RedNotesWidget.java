package notes.rednitrogen.com.rednotes.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import io.paperdb.Paper;
import notes.rednitrogen.com.rednotes.Notes;
import notes.rednitrogen.com.rednotes.R;

import static notes.rednitrogen.com.rednotes.widget.WidgetConfig.NOTE_VALUE;
import static notes.rednitrogen.com.rednotes.widget.WidgetConfig.POSITION_VALUE;
import static notes.rednitrogen.com.rednotes.widget.WidgetConfig.SHARED_PREFS;
import static notes.rednitrogen.com.rednotes.widget.WidgetConfig.TITLE_VALUE;

/**
 * Implementation of App Widget functionality.
 */
public class RedNotesWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

            Intent intent = new Intent(context, Notes.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

            SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            String title = prefs.getString(TITLE_VALUE+appWidgetId , "New note");
            String note = prefs.getString(NOTE_VALUE+appWidgetId , "Note text");
            int position = prefs.getInt(POSITION_VALUE+appWidgetId , 0);

            Intent serviceIntent = new Intent(context, WidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.red_notes_widget);
            views.setOnClickPendingIntent(R.id.widget_layout,pendingIntent);
            views.setTextViewText(R.id.appwidget_title, title);
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

