package name.dudkov.android.team_mapper.ui;

import android.app.Activity;
import name.dudkov.android.team_mapper.MainApplication;
import name.dudkov.android.team_mapper.data.State;

/**
 * User: madrider
 * Date: 31.05.13 16:06
 */
public class AddPointActivity extends Activity {
    private static final String TAG = AddPointActivity.class.getCanonicalName();
    State state;

    @Override
    protected void onStart() {
        super.onStart();
        state = ((MainApplication) getApplicationContext()).getState();
    }

    @Override
    public void onBackPressed() {
        //startActivity(new Intent(this, StartActivity.class));
        this.finish();
    }


}
