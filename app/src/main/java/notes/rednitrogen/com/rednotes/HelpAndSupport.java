package notes.rednitrogen.com.rednotes;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scottyab.rootbeer.RootBeer;

public class HelpAndSupport extends AppCompatActivity {
    private String versionName = BuildConfig.VERSION_NAME;
    private String mailSubject = "Generic";
    private LinearLayout privacyPolicyHead;
    private LinearLayout privacyPolicyBody;
    private LinearLayout tncHead;
    private LinearLayout tncBody;
    private LinearLayout faqHead;
    private LinearLayout faqBody;
    private String rootStatus = "Unrooted";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_support);
        Toolbar toolbar = findViewById(R.id.toolbar_help);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        privacyPolicyHead = findViewById(R.id.privacy_policy_head);
        privacyPolicyBody = findViewById(R.id.privacy_policy_body);

        privacyPolicyHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(privacyPolicyBody.getVisibility() == View.GONE){
                    privacyPolicyBody.setVisibility(View.VISIBLE);
                }
                else {
                    privacyPolicyBody.setVisibility(View.GONE);
                }
            }
        });

        tncHead = findViewById(R.id.tnc_head);
        tncBody = findViewById(R.id.tnc_body);

        tncHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tncBody.getVisibility() == View.GONE){
                    tncBody.setVisibility(View.VISIBLE);
                }
                else {
                    tncBody.setVisibility(View.GONE);
                }
            }
        });

        faqHead = findViewById(R.id.faq_head);
        faqBody = findViewById(R.id.faq_body);

        faqHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(faqBody.getVisibility() == View.GONE){
                    faqBody.setVisibility(View.VISIBLE);
                }
                else {
                    faqBody.setVisibility(View.GONE);
                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab_help);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence choices[] = new CharSequence[]{"Bug Report", "Feature Request", "Feedback", "Other Queries"};
                AlertDialog.Builder builder = new AlertDialog.Builder(HelpAndSupport.this);
                builder.setTitle("Select Mail Subject");
                builder.setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            mailSubject = "Bug Report";
                            sendFeedback();
                        }
                        else if (which == 1) {
                            mailSubject = "Feature Request";
                            sendFeedback();
                        }
                        else if(which == 2){
                            mailSubject = "Feedback";
                            sendFeedback();
                        }
                        else {
                            mailSubject = "Query";
                            sendFeedback();
                        }
                    }
                });
                builder.show();
            }
        });

        ((TextView)findViewById(R.id.about_linked)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView)findViewById(R.id.versionName)).setText(versionName);
        findViewById(R.id.rateApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slidein_left, R.anim.slideout_left);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void sendFeedback(){
        rootCheck();
        String body = null;
        try {
            body = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND
                    + "\n Root Status: " + rootStatus +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"rednitrogen@protonmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(intent, "Choose email client"));
    }

    private void rootCheck(){
        RootBeer rootBeer = new RootBeer(this);
        if (rootBeer.isRootedWithoutBusyBoxCheck()) {
            //we found indication of root
            rootStatus = "Rooted";
        } else {
            //we didn't find indication of root
            rootStatus = "Unrooted";
        }
    }
}
