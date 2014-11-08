package name.dudkov.android.team_mapper.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import name.dudkov.android.team_mapper.MainApplication;
import name.dudkov.android.team_mapper.R;
import name.dudkov.android.team_mapper.Utils;
import name.dudkov.android.team_mapper.data.State;
import name.dudkov.android.team_mapper.service.GpsService;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getName();

    private final Handler handler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message message) {
            if (message.what == 1) {
                toast(message.getData().getString("msg"));
            } else {
                updateScreen();
            }
            return true;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.setUiHandler(this.handler);
        setContentView(R.layout.main);
        ((TextView) findViewById(R.id.team_caption)).setText("Unit " + MainApplication.getPrefs().getAlias());
        updateScreen();
        startService(new Intent(this, GpsService.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "resume");
        updateScreen();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "pause");
    }

    @Override
    public void onBackPressed() {
        sendBroadcast(new Intent(GpsService.STOP_ALL));
        backToFirst();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_exit:
                backToFirst();
                return true;
            case R.id.menu_preferences:
                startActivity(new Intent(this, PrefsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void backToFirst() {
        sendBroadcast(new Intent(GpsService.STOP_ALL));
        startActivity(new Intent(getBaseContext(), StartActivity.class));
        this.finish();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void updateScreen() {
        State state = MainApplication.getState();
        ((Button) findViewById(R.id.gps_button)).setText("GPS " + (state.isGpsOn() ? "OFF" : "ON"));
        ((Button) findViewById(R.id.deg_button)).setText(state.getFormat() == Location.FORMAT_DEGREES ? "dms" : "deg");
        String text = "";
        Location loc = state.getLocation();
        if (loc != null) {
            String acc = getString(R.string.acc_label) + String.format(" %.1f", loc.getAccuracy()) + "m " + loc.getProvider();
            text += acc + "\n";
            text += getString(R.string.lat_label) + Utils.formatCoord(loc.getLatitude(), state.getFormat()) + "\n";
            text += getString(R.string.lon_label) + Utils.formatCoord(loc.getLongitude(), state.getFormat()) + "\n";
        }

        if (!state.getServerAnswer().equals("")) {
            text += state.getServerAnswer() + '\n';
        }
        text += "\n";
        text += String.format("%s points", state.getPoints().size());
        ((TextView) findViewById(R.id.text_label)).setText(text);
        Fragment fragment = getFragmentManager().findFragmentById(R.id.points_fragment);
        ((PointListFragment) fragment).update();

    }

    public void toast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    public void onClick(View view) {
        State state = MainApplication.getState();
        switch (view.getId()) {
            case R.id.gps_button:
                state.setGpsOn(!state.isGpsOn());
                ((Button) findViewById(R.id.gps_button)).setText("GPS " + (state.isGpsOn() ? "OFF" : "ON"));
                sendBroadcast(new Intent(GpsService.GPS));
                break;
            case R.id.deg_button:
                String l = "";
                if (state.getFormat() == Location.FORMAT_DEGREES) {
                    state.setFormat(Location.FORMAT_SECONDS);
                } else {
                    state.setFormat(Location.FORMAT_DEGREES);
                }
                updateScreen();
                break;
            case R.id.add_button:
                startActivity(new Intent(getBaseContext(), AddPointActivity.class));
                break;
        }
    }
}