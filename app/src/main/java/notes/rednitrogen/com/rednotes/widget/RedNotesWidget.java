package notes.rednitrogen.com.rednotes.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import io.paperdb.Paper;
import notes.rednitrogen.com.rednotes.Notes;
import notes.rednitrogen.com.rednotes.R;

/**
 * Implementation of App Widget functionality.
 */
public class RedNotesWidget extends AppWidgetProvider {

    static String CLICK_ACTION = "CLICKED";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Intent intent = new Intent(context, RedNotesWidget.class);
        intent.setAction(CLICK_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,0);

        //Init Paper
        Paper.init(context);
        //Read from Paper
        String title = Paper.book().read("title");
        String note = Paper.book().read("note");

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.red_notes_widget);
        views.setTextViewText(R.id.appwidget_title, title);
        views.setTextViewText(R.id.appwidget_text, note);
        views.setOnClickPendingIntent(R.id.widget_layout,pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    // receive intent on touch og widget to open app
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(intent.getAction().equals(CLICK_ACTION)){
            Intent appintent = new Intent(context, Notes.class);
            context.startActivity(appintent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
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

