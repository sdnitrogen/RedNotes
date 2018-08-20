package notes.rednitrogen.com.rednotes.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import notes.rednitrogen.com.rednotes.R;

public class TaskWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return null;
    }

    class TaskWidgetItemFactory implements RemoteViewsFactory {
        private Context context;
        private int appWidgetId;
        private String[] taskData = {};

        TaskWidgetItemFactory(Context context , Intent intent){
            this.context = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return taskData.length;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.red_tasks_widget);
            views.setTextViewText(R.id.task_widget_dot, Html.fromHtml("&#8226;"));
            views.setTextViewText(R.id.task_widget_name, taskData[position]);
            return null;
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
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
