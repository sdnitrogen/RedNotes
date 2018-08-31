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
                        Notes.shTaskEditor.putBoolean("isRemind", false);
                        Notes.shTaskEditor.commit();
                    }
                    else {
                        //toggle on
                        Notes.shTaskEditor.putBoolean("isRemind", true);
                        Notes.shTaskEditor.commit();
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
                            Notes.shTaskEditor.putInt("remindTime", 1);
                            Notes.shTaskEditor.commit();
                            break;
                        case "1":
                            Notes.shTaskEditor.putInt("remindTime", 2);
                            Notes.shTaskEditor.commit();
                            break;
                        case "2":
                            Notes.shTaskEditor.putInt("remindTime", 4);
                            Notes.shTaskEditor.commit();
                            break;
                        case "3":
                            Notes.shTaskEditor.putInt("remindTime", 6);
                            Notes.shTaskEditor.commit();
                            break;
                        case "4":
                            Notes.shTaskEditor.putInt("remindTime", 8);
                            Notes.shTaskEditor.commit();
                            break;
                        case "5":
                            Notes.shTaskEditor.putInt("remindTime", 10);
                            Notes.shTaskEditor.commit();
                            break;
                        case "6":
                            Notes.shTaskEditor.putInt("remindTime", 12);
                            Notes.shTaskEditor.commit();
                            break;
                        case "7":
                            Notes.shTaskEditor.putInt("remindTime", 14);
                            Notes.shTaskEditor.commit();
                            break;
                        case "8":
                            Notes.shTaskEditor.putInt("remindTime", 16);
                            Notes.shTaskEditor.commit();
                            break;
                        case "9":
                            Notes.shTaskEditor.putInt("remindTime", 18);
                            Notes.shTaskEditor.commit();
                            break;
                        case "10":
                            Notes.shTaskEditor.putInt("remindTime", 20);
                            Notes.shTaskEditor.commit();
                            break;
                        case "11":
                            Notes.shTaskEditor.putInt("remindTime", 22);
                            Notes.shTaskEditor.commit();
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
