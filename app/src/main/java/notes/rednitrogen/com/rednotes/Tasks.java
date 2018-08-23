package notes.rednitrogen.com.rednotes;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import notes.rednitrogen.com.rednotes.database.TaskDBHelper;
import notes.rednitrogen.com.rednotes.database.model.Task;
import notes.rednitrogen.com.rednotes.utils.MyDividerItemDecoration;
import notes.rednitrogen.com.rednotes.utils.TaskRecyclerItemTouchHelper;

public class Tasks extends AppCompatActivity implements TaskRecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private TasksAdapter tAdapter;
    private List<Task> tasksList = new ArrayList<>();
    private CoordinatorLayout taskCoordinatorLayout;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private RecyclerView taskRecyclerView;
    private TextView noTasksView;

    public static TaskDBHelper mydb;
    private DatePickerDialog dpd;

    private AlertDialog alertDialog = null;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        Toolbar toolbar = findViewById(R.id.toolbar_tasks);
        setSupportActionBar(toolbar);

        taskCoordinatorLayout = findViewById(R.id.coordinator_layout_tasks);
        mDrawerLayout = findViewById(R.id.tasks_drawer);
        taskRecyclerView = findViewById(R.id.recycler_view_tasks);
        noTasksView = findViewById(R.id.empty_tasks_view);

        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, R.string.nav_open, R.string.nav_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mydb = new TaskDBHelper(this);
        tasksList.addAll(mydb.getAllTasks());

        FloatingActionButton fab = findViewById(R.id.fab_tasks);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTaskDialog(false, null, -1);
            }
        });

        tAdapter = new TasksAdapter(this, tasksList);
        RecyclerView.LayoutManager tLayoutManager = new LinearLayoutManager(getApplicationContext());
        taskRecyclerView.setLayoutManager(tLayoutManager);
        taskRecyclerView.setItemAnimator(new DefaultItemAnimator());
        taskRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        taskRecyclerView.setAdapter(tAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new TaskRecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(taskRecyclerView);

        toggleEmptyTasks();
    }

    private void toggleEmptyTasks() {
        if (mydb.getTasksCount() > 0) {
            noTasksView.setVisibility(View.GONE);
        } else {
            noTasksView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        if (viewHolder instanceof TasksAdapter.MyViewHolder) {
            // backup of removed item for undo purpose
            final Task deletedItem = tasksList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            tAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(taskCoordinatorLayout, "Task removed from list!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    tAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.addCallback(new Snackbar.Callback() {

                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    //see Snackbar.Callback docs for event details
                    if (event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_CONSECUTIVE || event == DISMISS_EVENT_MANUAL){
                        deleteTask(deletedItem);
                    }
                }

                @Override
                public void onShown(Snackbar snackbar) {

                }
            });
            snackbar.show();
        }
    }

    private void createTask(String task, String checked, String time) {
        // inserting note in db and getting
        // newly inserted note id
        long id = mydb.insertTask(task, checked, time);

        // get the newly inserted note from db
        Task t = mydb.getTask(id);

        if (t != null) {
            refresh();
        }
    }

    private void updateTask(String task, String time, int position) {
        Task t = tasksList.get(position);
        // updating note text
        t.setTask(task);
        t.setTime(time);

        // updating note in db
        mydb.updateTask(t);

        refresh();
    }

    public void refresh(){
        tasksList.removeAll(tasksList);
        tasksList.addAll(mydb.getAllTasks());
        tAdapter.notifyDataSetChanged();

        toggleEmptyTasks();
    }

    private void deleteTask(Task task) {
        // deleting the note from db
        mydb.deleteTask(task);

        toggleEmptyTasks();
    }

    public void showTaskDialog(final boolean shouldUpdate, final Task task, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.task_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Tasks.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputTask = view.findViewById(R.id.task_name_di);

        final EditText inputTime = view.findViewById(R.id.task_date_di);
        inputTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                dpd = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                                                       @Override
                                                       public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                                           String date = (dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth) + "/" +
                                                                   ((monthOfYear+1) < 10 ? "0" + (monthOfYear+1) : "" + (monthOfYear+1)) + "/" +
                                                                   year;
                                                           inputTime.setText(date);
                                                       }
                                                   },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });


        if (shouldUpdate && task != null) {
            inputTask.setText(task.getTask());
            inputTask.setSelection(inputTask.getText().length());
            String time = task.getTime();
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = fmt.parse(time);
                SimpleDateFormat fmtout = new SimpleDateFormat("dd/MM/yyyy");
                time = fmtout.format(date);
            } catch (ParseException e) {

            }
            inputTime.setText(time);
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();


        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputTask.getText().toString())) {
                    Toast.makeText(Tasks.this, "Enter task!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(inputTime.getText().toString())){
                    Toast.makeText(Tasks.this, "Enter date!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && task != null) {
                    // update note by it's id
                    updateTask(inputTask.getText().toString(), inputTime.getText().toString(), position);
                } else {
                    // create new note
                    createTask(inputTask.getText().toString(), "false", inputTime.getText().toString());
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
