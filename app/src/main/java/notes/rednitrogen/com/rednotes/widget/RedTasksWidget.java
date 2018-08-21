package notes.rednitrogen.com.rednotes.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

import notes.rednitrogen.com.rednotes.R;
import notes.rednitrogen.com.rednotes.database.TaskDBHelper;
import notes.rednitrogen.com.rednotes.database.model.Task;


public class RedTasksWidget extends AppWidgetProvider {

    private List<Task> tasksList = new ArrayList<>();
    private TaskDBHelper mydb;
    private ArrayList<String> tasksData = new ArrayList<>();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        mydb = new TaskDBHelper(context);
        tasksList.addAll(mydb.getUncheckedTasks());
        for(int i=0; i<tasksList.size(); i++){
            tasksData.add(tasksList.get(i).getTask());
        }

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Intent intentUpdate = new Intent(context, RedTasksWidget.class);
            intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pInt = PendingIntent.getBroadcast(context, 0, intentUpdate, 0);


            Intent serviceIntent = new Intent(context, TaskWidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            serviceIntent.putStringArrayListExtra("tasksData", tasksData);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.red_tasks_widget);
            views.setOnClickPendingIntent(R.id.taskwidget_refresh, pInt);
            views.setRemoteAdapter(R.id.task_widget_listview, serviceIntent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        mydb.close();
        tasksList.removeAll(tasksList);
        tasksData.clear();
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

