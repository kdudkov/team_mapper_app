package name.dudkov.android.team_mapper;

import android.app.Application;
import android.os.Handler;
import android.preference.PreferenceManager;
import name.dudkov.android.team_mapper.data.Prefs;
import name.dudkov.android.team_mapper.data.State;

/**
 * Created by madrider on 09.06.14
 */
public class MainApplication extends Application {

    private static MainApplication instance;
    private static State state;
    private static Prefs prefs;
    private static Handler uiHandler;

    public static MainApplication getInstance() {
        return instance;
    }

    public static State getState() {
        return state;
    }

    public static Prefs getPrefs() {
        return prefs;
    }

    public static Handler getUiHandler() {
        return uiHandler;
    }

    public static void setUiHandler(Handler uiHandler) {
        MainApplication.uiHandler = uiHandler;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        state = new State();
        prefs = new Prefs(PreferenceManager.getDefaultSharedPreferences(this));
    }
}
