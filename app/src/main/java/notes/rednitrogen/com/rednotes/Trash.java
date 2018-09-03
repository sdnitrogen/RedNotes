package notes.rednitrogen.com.rednotes;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import notes.rednitrogen.com.rednotes.database.DatabaseHelper;
import notes.rednitrogen.com.rednotes.database.model.Note;
import notes.rednitrogen.com.rednotes.utils.MyDividerItemDecoration;
import notes.rednitrogen.com.rednotes.utils.RecyclerTouchListener;

public class Trash extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;

    private TrashAdapter mAdapter;
    private List<Note> deletedNotesList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noNotesView;

    private DatabaseHelper db;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);
        Toolbar toolbar = findViewById(R.id.toolbar_trash);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        coordinatorLayout = findViewById(R.id.coordinator_layout_trash);
        recyclerView = findViewById(R.id.trash_recycler_view);
        noNotesView = findViewById(R.id.empty_trash_view);

        db = new DatabaseHelper(this);

        deletedNotesList.addAll(db.getDeletedNotes());
        for(int i = 0; i < deletedNotesList.size(); i++){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date delDate = sdf.parse(deletedNotesList.get(i).getDeletedTime());
                if (System.currentTimeMillis() > delDate.getTime()) {
                    permaDeleteNote(i);
                }
            } catch (ParseException e) {

            }
        }

        FloatingActionButton fab = findViewById(R.id.fab_trash);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //confirmation
                new AlertDialog.Builder(Trash.this)
                        .setTitle("Do you really want to empty Trash?")
                        .setMessage("This will permanently delete all notes in Trash and this action is irreversible")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                int count = recyclerView.getChildCount();
                                for (int i = 0; i < count; i++){
                                    permaDeleteNote(0);
                                }
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        if (BuildConfig.FLAVOR.equals("free")){
            mAdView = findViewById(R.id.adView);
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)fab.getLayoutParams();
            params.setMargins(0, 0, getPixelsFromDPs(Trash.this, 16), getPixelsFromDPs(Trash.this, 60)); //substitute parameters for left, top, right, bottom
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

        mAdapter = new TrashAdapter(this, deletedNotesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyTrash();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                // delete or restore function
                showActionDialog(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void showActionDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Restore Note", "Delete Note forever"};

        final Note note = deletedNotesList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    db.setNoteFree(deletedNotesList.get(position));
                    deletedNotesList.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    toggleEmptyTrash();
                }
                else {
                    permaDeleteNote(position);
                }
            }
        });
        builder.show();
    }

    private void permaDeleteNote(int position) {
        // deleting the note from db
        db.deleteNote(deletedNotesList.get(position));

        // removing the note from the list
        deletedNotesList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyTrash();
    }

    private void toggleEmptyTrash() {
        // you can check notesList.size() > 0

        if (db.getDeletedNotesCount() > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (BuildConfig.FLAVOR.equals("free")){
            loadInterstitialAd();
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.slidein_left, R.anim.slideout_left);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
}
