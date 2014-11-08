package name.dudkov.android.team_mapper.service;

import android.location.Location;
import android.util.Log;
import name.dudkov.android.team_mapper.MainApplication;
import name.dudkov.android.team_mapper.data.GpsPoint;
import name.dudkov.android.team_mapper.data.PointType;
import name.dudkov.android.team_mapper.data.Prefs;
import name.dudkov.android.team_mapper.data.State;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by madrider on 29.10.14.
 */
public class Sender implements Runnable {
    private final static String TAG = Sender.class.getSimpleName();
    private final static String SERVER_URL = "http://team-mapper.appspot.com/c/";

    private final State state;
    private final Runnable callback;

    public Sender(State state, Runnable callback) {
        this.state = state;
        this.callback = callback;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            Log.e(TAG, "", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "", e);
            }
        }
        return sb.toString();
    }

    @Override
    public void run() {
        Log.i(TAG, "start sender");
        boolean ok = true;
        String result = null;
        HttpURLConnection conn = null;
        state.setServerAnswer("");
        try {
            URL url = new URL(SERVER_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Host", url.getHost());
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(composeMessage());
            out.close();
            result = convertStreamToString(conn.getInputStream());
            ok = parseMessage(result);

        } catch (JSONException e) {
            Log.e(TAG, "json decode exception");
            state.setServerAnswer("json decode exception");
            Log.e(TAG, result);
        } catch (IOException e) {
            Log.e(TAG, "connect timeout: " + e.getMessage());
            state.setServerAnswer(e.toString());
            ok = false;
        } catch (Throwable e) {
            Log.e(TAG, "exception", e);
            state.setServerAnswer(e.toString());
            ok = false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        if (ok) {
            Log.i(TAG, "position sent");
            state.setLastServerConnect(System.currentTimeMillis());
        }
        if (callback != null) {
            callback.run();
        }
    }

    private String composeMessage() throws JSONException {
        JSONObject res = new JSONObject();
        //res.put("imei", state.getDeviceId());
        final Prefs prefs = MainApplication.getPrefs();
        res.put("name", prefs.getAlias());
        res.put("code", prefs.getCode());
        Location loc = state.getLocation();
        res.put("lat", loc != null ? loc.getLatitude() : null);
        res.put("lon", loc != null ? loc.getLongitude() : null);
        res.put("acc", loc != null ? loc.getAccuracy() : null);
        return res.toString();
    }

    private boolean parseMessage(String message) throws JSONException {
        JSONObject entries;
        try {
            entries = new JSONObject(message);
            state.setServerAnswer("");
            // got error
            if (entries.has("error") && !entries.getString("error").equals("")) {
                String err = entries.getString("error");
                Log.e(TAG, "error: " + err);
                state.setServerAnswer(err);
            }
            ArrayList<GpsPoint> pp = new ArrayList<GpsPoint>();

            if (entries.has("points")) {
                pp.addAll(getPoints(entries.getJSONArray("points"), PointType.POINT));
            }
            if (entries.has("units")) {
                pp.addAll(getPoints(entries.getJSONArray("units"), PointType.UNIT));
            }
            Log.i(TAG, "got " + pp.size() + " points");
            state.setPoints(pp);
            return true;
        } catch (JSONException ex) {
            Log.e(TAG, "exception", ex);
            state.setServerAnswer(ex.getMessage());
            return false;
        }
    }

    private List<GpsPoint> getPoints(JSONArray points, PointType type) throws JSONException {
        List<GpsPoint> result = new ArrayList<GpsPoint>();
        for (int i = 0; i < points.length(); i++) {
            JSONObject p = points.getJSONObject(i);
            if (p.has("name") && p.has("lat") && p.has("lon")) {
                String name = p.getString("name");
                double lat = p.getDouble("lat");
                double lon = p.getDouble("lon");
                if (!(name.equals(MainApplication.getPrefs().getAlias()) && type.equals(PointType.UNIT))) {
                    result.add(new GpsPoint(type, name, lon, lat));
                }
            }
        }
        return result;
    }
}

