package notes.rednitrogen.com.rednotes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import notes.rednitrogen.com.rednotes.R;
import notes.rednitrogen.com.rednotes.database.model.Task;


public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.MyViewHolder> {

    private Context context;
    private List<Task> tasksList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView task;
        public TextView time;
        public CheckBox checkBox;
        public RelativeLayout viewBackgroundTask, viewForegroundTask;
        public LinearLayout center;

        public MyViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.task_name);
            time = view.findViewById(R.id.task_date);
            checkBox = view.findViewById(R.id.task_check);
            viewBackgroundTask = view.findViewById(R.id.taskview_background);
            viewForegroundTask = view.findViewById(R.id.taskview_foreground);
            center = view.findViewById(R.id.centerContent);
        }
    }


    public TasksAdapter(Context context, List<Task> tasksList) {
        this.context = context;
        this.tasksList = tasksList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Task task = tasksList.get(position);

        holder.task.setText(task.getTask());
        holder.time.setText(formatDate(task.getTime()));
        holder.checkBox.setChecked(Boolean.valueOf(task.getChecked()));

        holder.center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Tasks)context).showTaskDialog(true, tasksList.get(position), position);
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(holder.checkBox.isChecked()){
                    holder.task.setPaintFlags(holder.task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else{
                    holder.task.setPaintFlags(0);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d ''yy");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }

    public void removeItem(int position) {
        tasksList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Task task, int position) {
        tasksList.add(position, task);
        // notify item added by position
        notifyItemInserted(position);
    }

    public void setTasksList(List<Task> tasksList){
        this.tasksList = tasksList;
        notifyDataSetChanged();
    }
}
