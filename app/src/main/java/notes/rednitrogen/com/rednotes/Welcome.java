package notes.rednitrogen.com.rednotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class Welcome extends AppCompatActivity {

    LinearLayout l1,l2;
    ImageButton btngo;
    Animation uptodown,downtoup;
    SharedPreferences sharedPreferences;
    boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Shared preference to check first time appearance only
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        firstTime = sharedPreferences.getBoolean("firstTime", true);

        if(firstTime){
            btngo = findViewById(R.id.buttongo);
            l1 = findViewById(R.id.l1);
            l2 = findViewById(R.id.l2);
            uptodown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
            downtoup = AnimationUtils.loadAnimation(this,R.anim.downtoup);
            l1.setAnimation(uptodown);
            l2.setAnimation(downtoup);

            btngo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    firstTime = false;
                    editor.putBoolean("firstTime", firstTime);
                    editor.apply();

                    goToMainActivity();
                    overridePendingTransition(R.anim.slidein, R.anim.slideout);
                }
            });
        }
        else {
            goToMainActivity();
        }
    }

    private void goToMainActivity(){
        Intent intent = new Intent(Welcome.this, Notes.class);
        startActivity(intent);
        finish();
    }
}
