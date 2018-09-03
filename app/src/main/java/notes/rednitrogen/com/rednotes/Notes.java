package notes.rednitrogen.com.rednotes;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.google.android.gms.ads.InterstitialAd;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import notes.rednitrogen.com.rednotes.R;
import notes.rednitrogen.com.rednotes.database.DatabaseHelper;
import notes.rednitrogen.com.rednotes.database.model.Note;
import notes.rednitrogen.com.rednotes.utils.MyDividerItemDecoration;
import notes.rednitrogen.com.rednotes.utils.RecyclerItemTouchHelper;
import notes.rednitrogen.com.rednotes.utils.RecyclerTouchListener;

public class Notes extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private NotesAdapter mAdapter;
    private List<Note> notesList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private RecyclerView recyclerView;
    private TextView noNotesView;

    private MaterialSearchView searchView;
    private String currentQuery;

    private DatabaseHelper db;
    public static SharedPreferences shPrefs, shTaskPrefs;
    public static  SharedPreferences.Editor shEditor;
    public static  SharedPreferences.Editor shTaskEditor;

    private AlertDialog alertDialog = null;

    boolean doubleBackToExitPressedOnce = false;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initChannels(this);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        mDrawerLayout = findViewById(R.id.notes_drawer);
        recyclerView = findViewById(R.id.recycler_view);
        noNotesView = findViewById(R.id.empty_notes_view);

        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, R.string.nav_open, R.string.nav_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpDrawerContent((NavigationView) findViewById(R.id.nv));
        ((NavigationView) findViewById(R.id.nv)).setCheckedItem(R.id.nav_notes);

        db = new DatabaseHelper(this);
        shPrefs = getSharedPreferences("Settings", MODE_PRIVATE);
        shEditor = shPrefs.edit();
        shTaskPrefs = getSharedPreferences("Reminders", MODE_PRIVATE);
        shTaskEditor = shTaskPrefs.edit();

        notesList.addAll(db.getGoodNotes());
        if(shPrefs.getBoolean("isReversed", false)){
            Collections.reverse(notesList);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null, -1);
            }
        });

        if (BuildConfig.FLAVOR.equals("free")){
            mAdView = findViewById(R.id.adView);
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)fab.getLayoutParams();
            params.setMargins(0, 0, getPixelsFromDPs(Notes.this, 16), getPixelsFromDPs(Notes.this, 60)); //substitute parameters for left, top, right, bottom
            fab.setLayoutParams(params);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                }

                @Override
                public void onAdClosed() {
                    Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdLeftApplication() {
                    Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }
            });
            mAdView.loadAd(adRequest);
        }

        mAdapter = new NotesAdapter(this, notesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        toggleEmptyNotes();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                showActionsDialog(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        // get the notification id correspondiing to the one to be edited and dismiss it.
        String identity = getIntent().getStringExtra("identity");
        if(identity != null && identity.equals("notification")){
            int pos = getIntent().getIntExtra("position",0);
            //Log.d("pos", String.valueOf(pos));
            showNoteDialog(true, notesList.get(pos), pos);
            NotificationManagerCompat manager = NotificationManagerCompat.from(this);
            manager.cancel(pos);
        }
    }

    private void processQuery(String query) {
        currentQuery = query;
        List<Note> result = new ArrayList<>();

        for(Note forNote : notesList){
            if(forNote.getNote().toLowerCase().contains(query.toLowerCase()) || forNote.getTitle().toLowerCase().contains(query.toLowerCase())){
                result.add(forNote);
            }
        }
        mAdapter.setNotesList(result);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes, menu);
        searchView = findViewById(R.id.search_view);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                processQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                processQuery(newText);
                return true;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
                searchView.setQuery(currentQuery, false);
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
                currentQuery = "";
                mAdapter.setNotesList(notesList);
            }
        });

        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof NotesAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String title = notesList.get(viewHolder.getAdapterPosition()).getTitle();

            // backup of removed item for undo purpose
            final Note deletedItem = notesList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());
            db.setNoteDel(deletedItem);

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, shPrefs.getInt("trashTime",7));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String delDate = sdf.format(c.getTime());
            deletedItem.setDeletedTime(delDate);
            db.updateNoteDelTime(deletedItem);

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "'"+title+"'" + " removed from list!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.setNoteFree(deletedItem);
                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
        toggleEmptyNotes();
    }

    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private void createNote(String title, String note) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(title, note);

        // get the newly inserted note from db
        Note n = db.getNote(id);

        if (n != null) {
            if(shPrefs.getBoolean("isReversed", false)){
                notesList.add(notesList.size(), n);
            }
            else {
                // adding new note to array list at 0 position
                notesList.add(0, n);
            }
            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyNotes();
        }
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateNote(String title, String note, int position) {
        Note n = notesList.get(position);
        // updating note text
        n.setNote(note);
        n.setTitle(title);

        // updating note in db
        db.updateNote(n);

        // refreshing the list
        notesList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteNote(int position) {
        // deleting the note from db
        db.deleteNote(notesList.get(position));

        // removing the note from the list
        notesList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyNotes();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Share", "Copy Note", "Add to Notification", "Delete Forever"};

        final Note note = notesList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    showNoteDialog(true, note, position);
                }
                else if (which == 1) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT,note.getNote());
                    shareIntent.setType("text/plain");
                    Intent.createChooser(shareIntent,"Share via");
                    startActivity(shareIntent);
                }
                else if(which == 2){
                    String noteText = note.getNote();
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("note", noteText);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(Notes.this, "Note copied to clipboard!", Toast.LENGTH_SHORT).show();
                }
                else if(which == 3){
                    String noteTitle = note.getTitle();
                    String noteText = note.getNote();
                    createNotification(noteTitle, noteText, position);
                }
                else {
                    //confirmation
                    AlertDialog aDialog = new AlertDialog.Builder(Notes.this)
                            .setTitle("Do you really want to delete this note forever?")
                            .setMessage("(You can slide your note to the left to delete normally and it'll be stored in Trash" +
                                    " for few days before auto-deletion. You can restore or delete permanently from there if you wish.)")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    deleteNote(position);
                                }})
                            .setNegativeButton("NO", null).show();
                    TextView textView = aDialog.findViewById(android.R.id.message);
                    textView.setTextSize(15);
                    textView.setTextColor(Color.GRAY);
                }
            }
        });
        builder.show();
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Notes.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputNote = view.findViewById(R.id.note);

        final EditText dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : note.getTitle());


        if (shouldUpdate && note != null) {
            inputNote.setText(note.getNote());
            inputNote.setSelection(inputNote.getText().length());
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
                if (TextUtils.isEmpty(inputNote.getText().toString())) {
                    Toast.makeText(Notes.this, "Enter note!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && note != null) {
                    // update note by it's id
                    updateNote(dialogTitle.getText().toString(), inputNote.getText().toString(), position);
                } else {
                    // create new note
                    createNote(dialogTitle.getText().toString(), inputNote.getText().toString());
                }
            }
        });
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0

        if (db.getNotesCount() > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Add a note to Notification
     */
    public void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("default",
                "Channel name",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Channel description");
        notificationManager.createNotificationChannel(channel);
    }

    private void createNotification(String textTitle, String textContent, int position){

        Intent actionIntent = new Intent(this,Notes.class);
        actionIntent.putExtra("position",position);
        actionIntent.putExtra("identity","notification");
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(this,(int) System.currentTimeMillis(),actionIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setColor(Color.RED)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(textContent))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .addAction(0,"Edit",pIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(position, mBuilder.build());
        //Log.d("position", String.valueOf(position));
    }


    /**
     * To stop memory leak when activity relaunched while
     * the alert dialog box is still open, need to dismiss.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }
        else if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectItemDrawer(MenuItem menuItem){
        Intent intent;
        switch(menuItem.getItemId()){
            case R.id.nav_notes:
                break;
            case R.id.nav_tasks:
                intent = new Intent(this, Tasks.class);
                startActivityForResult(intent,1);
                overridePendingTransition(R.anim.slidein, R.anim.slideout);
                break;
            case R.id.nav_trash:
                if (BuildConfig.FLAVOR.equals("free")){
                    loadInterstitialAd();
                }
                intent = new Intent(this, Trash.class);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.slidein, R.anim.slideout);
                break;
            case R.id.nav_settings:
                if (BuildConfig.FLAVOR.equals("free")){
                    loadInterstitialAd();
                }
                intent = new Intent(this, Settings.class);
                startActivityForResult(intent,1);
                overridePendingTransition(R.anim.slidein, R.anim.slideout);
                break;
            case R.id.nav_help_and_support:
                if (BuildConfig.FLAVOR.equals("free")){
                    loadInterstitialAd();
                }
                intent = new Intent(this, HelpAndSupport.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slidein, R.anim.slideout);
                break;
            default:
                break;
        }
        mDrawerLayout.closeDrawers();
    }

    private void setUpDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setCheckable(false);
                selectItemDrawer(item);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        notesList.removeAll(notesList);
        notesList.addAll(db.getGoodNotes());
        if(shPrefs.getBoolean("isReversed", false)){
            Collections.reverse(notesList);
        }
        mAdapter.notifyDataSetChanged();
        toggleEmptyNotes();
    }

    @Override
    public void onPause() {
        if (BuildConfig.FLAVOR.equals("free")){
            // This method should be called in the parent Activity's onPause() method.
            if (mAdView != null) {
                mAdView.pause();
            }
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (BuildConfig.FLAVOR.equals("free")){
            // This method should be called in the parent Activity's onResume() method.
            if (mAdView != null) {
                mAdView.resume();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (BuildConfig.FLAVOR.equals("free")){
            // This method should be called in the parent Activity's onDestroy() method.
            if (mAdView != null) {
                mAdView.destroy();
            }
        }
        super.onDestroy();
    }

    public static int getPixelsFromDPs(Activity activity, int dps){
        Resources r = activity.getResources();
        int  px = (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps, r.getDisplayMetrics()));
        return px;
    }

    private void loadInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if(mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }
}
