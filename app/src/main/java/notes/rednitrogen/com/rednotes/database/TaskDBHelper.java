package notes.rednitrogen.com.rednotes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import notes.rednitrogen.com.rednotes.database.model.Task;


public class TaskDBHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "tasks_db";


    public TaskDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Task.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Task.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertTask(String task, String checked, String time) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them

        try {
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
            Date date = fmt.parse(time+" 00:00:00");
            SimpleDateFormat fmtout = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            time = fmtout.format(date);
        } catch (ParseException e) {

        }

        values.put(Task.COLUMN_TASK, task);
        values.put(Task.COLUMN_CHECKED, checked);
        values.put(Task.COLUMN_TIME, time);

        // insert row
        long id = db.insert(Task.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Task getTask(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Task.TABLE_NAME,
                new String[]{Task.COLUMN_ID, Task.COLUMN_TASK, Task.COLUMN_CHECKED, Task.COLUMN_TIME},
                Task.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Task task = new Task(
                cursor.getInt(cursor.getColumnIndex(Task.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Task.COLUMN_TASK)),
                cursor.getString(cursor.getColumnIndex(Task.COLUMN_CHECKED)),
                cursor.getString(cursor.getColumnIndex(Task.COLUMN_TIME)));

        // close the db connection
        cursor.close();

        return task;
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Task.TABLE_NAME + " ORDER BY " +
                Task.COLUMN_TIME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getInt(cursor.getColumnIndex(Task.COLUMN_ID)));
                task.setTask(cursor.getString(cursor.getColumnIndex(Task.COLUMN_TASK)));
                task.setChecked(cursor.getString(cursor.getColumnIndex(Task.COLUMN_CHECKED)));
                task.setTime(cursor.getString(cursor.getColumnIndex(Task.COLUMN_TIME)));

                tasks.add(task);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return tasks;
    }

    public int getTasksCount() {
        String countQuery = "SELECT  * FROM " + Task.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        String time = task.getTime();
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
            Date date = fmt.parse(time+" 00:00:00");
            SimpleDateFormat fmtout = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            time = fmtout.format(date);
        } catch (ParseException e) {

        }

        ContentValues values = new ContentValues();
        values.put(Task.COLUMN_TASK, task.getTask());
        values.put(Task.COLUMN_TIME, time);

        // updating row
        return db.update(Task.TABLE_NAME, values, Task.COLUMN_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
    }

    // delete note
    public void deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Task.TABLE_NAME, Task.COLUMN_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
        db.close();
    }
}