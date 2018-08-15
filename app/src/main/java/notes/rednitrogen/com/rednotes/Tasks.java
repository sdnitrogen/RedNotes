package notes.rednitrogen.com.rednotes;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.List;

import notes.rednitrogen.com.rednotes.database.TaskDBHelper;
import notes.rednitrogen.com.rednotes.database.model.Task;

public class Tasks extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TasksAdapter tAdapter;
    private List<Task> tasksList = new ArrayList<>();
    private CoordinatorLayout taskCoordinatorLayout;
    private RecyclerView taskRecyclerView;
    private TextView noTasksView;

    private TaskDBHelper mydb;

    private AlertDialog alertDialog = null;

    DatePickerDialog dpd;
    int startYear = 0, startMonth = 0, startDay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        Toolbar toolbar = findViewById(R.id.toolbar_tasks);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab_tasks);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        toggleEmptyTasks();
    }

    private void toggleEmptyTasks() {
        if (mydb.getTasksCount() > 0) {
            noTasksView.setVisibility(View.GONE);
        } else {
            noTasksView.setVisibility(View.VISIBLE);
        }
    }

    public void showStartDatePicker(View v) {
        dpd = DatePickerDialog.newInstance(this, startYear, startMonth, startDay);
        dpd.setOnDateSetListener(this);
        dpd.show(getFragmentManager(), "startDatepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        startYear = year;
        startMonth = monthOfYear;
        startDay = dayOfMonth;
        int monthAddOne = startMonth + 1;
        String date = (startDay < 10 ? "0" + startDay : "" + startDay) + "/" +
                (monthAddOne < 10 ? "0" + monthAddOne : "" + monthAddOne) + "/" +
                startYear;
        EditText task_date = findViewById(R.id.task_date_di);
        task_date.setText(date);
    }
}
