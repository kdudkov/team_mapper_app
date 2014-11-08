package name.dudkov.android.team_mapper.data;

import android.content.SharedPreferences;

/**
 * Created by madrider on 29.10.14.
 */
public class Prefs {

    private final SharedPreferences sharedPreferences;

    public Prefs(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public String getAlias() {
        return sharedPreferences.getString("alias", "no_name");
    }

    public void setAlias(String val) {
        saveString("alias", val);
    }

    public String getCode() {
        return sharedPreferences.getString("code", "");
    }

    public void setCode(String val) {
        saveString("code", val);
    }

    private void saveString(String key, String val) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, val);
        edit.apply();
    }

}
