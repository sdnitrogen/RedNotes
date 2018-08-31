package notes.rednitrogen.com.rednotes;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(R.id.settingsContent, new MainPreferenceFragment()).commit();
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);
            findPreference("key_bottomnote").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (((SwitchPreference)preference).isChecked()){
                        //toggle off
                        Notes.shEditor.putBoolean("isReversed", false);
                        Notes.shEditor.commit();
                    }
                    else {
                        //toggle on
                        Notes.shEditor.putBoolean("isReversed", true);
                        Notes.shEditor.commit();
                    }
                    return true;
                }
            });

            findPreference("key_trashtime").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String textValue = newValue.toString();
                    switch (textValue){
                        case "0":
                            Notes.shEditor.putInt("trashTime", 7);
                            Notes.shEditor.commit();
                            break;
                        case "1":
                            Notes.shEditor.putInt("trashTime", 14);
                            Notes.shEditor.commit();
                            break;
                        case "2":
                            Notes.shEditor.putInt("trashTime", 30);
                            Notes.shEditor.commit();
                            break;
                        case "3":
                            Notes.shEditor.putInt("trashTime", 180);
                            Notes.shEditor.commit();
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });

            findPreference("key_reminder").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (((SwitchPreference)preference).isChecked()){
                        //toggle off
                        Tasks.shTaskEditor.putBoolean("isRemind", false);
                        Tasks.shTaskEditor.commit();
                    }
                    else {
                        //toggle on
                        Tasks.shTaskEditor.putBoolean("isRemind", true);
                        Tasks.shTaskEditor.commit();
                    }
                    return true;
                }
            });

            findPreference("key_remindertime").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String textValue = newValue.toString();
                    switch (textValue){
                        case "0":
                            Tasks.shTaskEditor.putString("remindTime", "00");
                            Tasks.shTaskEditor.commit();
                            break;
                        case "1":
                            Tasks.shTaskEditor.putString("remindTime", "02");
                            Tasks.shTaskEditor.commit();
                            break;
                        case "2":
                            Tasks.shTaskEditor.putString("remindTime", "04");
                            Tasks.shTaskEditor.commit();
                            break;
                        case "3":
                            Tasks.shTaskEditor.putString("remindTime", "06");
                            Tasks.shTaskEditor.commit();
                            break;
                        case "4":
                            Tasks.shTaskEditor.putString("remindTime", "08");
                            Tasks.shTaskEditor.commit();
                            break;
                        case "5":
                            Tasks.shTaskEditor.putString("remindTime", "10");
                            Tasks.shTaskEditor.commit();
                            break;
                        case "6":
                            Tasks.shTaskEditor.putString("remindTime", "12");
                            Tasks.shTaskEditor.commit();
                            break;
                        case "7":
                            Tasks.shTaskEditor.putString("remindTime", "14");
                            Tasks.shTaskEditor.commit();
                            break;
                        case "8":
                            Tasks.shTaskEditor.putString("remindTime", "16");
                            Tasks.shTaskEditor.commit();
                            break;
                        case "9":
                            Tasks.shTaskEditor.putString("remindTime", "18");
                            Tasks.shTaskEditor.commit();
                            break;
                        case "10":
                            Tasks.shTaskEditor.putString("remindTime", "20");
                            Tasks.shTaskEditor.commit();
                            break;
                        case "11":
                            Tasks.shTaskEditor.putString("remindTime", "22");
                            Tasks.shTaskEditor.commit();
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
        }
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
}
