package notes.rednitrogen.com.rednotes.database.model;

public class Task{

        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TASK = "task";
        public static final String COLUMN_CHECKED = "false";
        public static final String COLUMN_TIME = "time";

        private int id;
        private String task;
        private String checked;
        private String time;


        // Create table SQL query
        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_TASK + " TEXT,"
                        + COLUMN_CHECKED + " TEXT,"
                        + COLUMN_TIME + " DATETIME DEFAULT"
                        + ")";

        public Task() {
        }

        public Task(int id, String task, String checked, String time) {
            this.id = id;
            this.task = task;
            this.checked = checked;
            this.time = time;
        }

        public int getId() {
            return id;
        }

        public String getTask() {
            return task;
        }

        public void setTask(String task) {
            this.task = task;
        }

        public String getChecked() {
            return checked;
        }

        public void setChecked(String checked) {
            this.checked = checked;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTime() {
        return time;
    }

        public void setTime(String time) {
        this.time = time;
    }
}
