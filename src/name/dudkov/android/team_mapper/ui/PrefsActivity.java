package name.dudkov.android.team_mapper.ui;


import android.os.Bundle;
import android.preference.PreferenceActivity;
import name.dudkov.android.team_mapper.R;

/**
 * User: madrider
 * Date: 31.05.13 16:47
 */
public class PrefsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }

}
