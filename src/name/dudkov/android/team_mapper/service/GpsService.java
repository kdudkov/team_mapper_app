package name.dudkov.android.team_mapper.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import name.dudkov.android.team_mapper.MainApplication;
import name.dudkov.android.team_mapper.R;
import name.dudkov.android.team_mapper.data.GpsPoint;
import name.dudkov.android.team_mapper.location.LocationProcessor;
import name.dudkov.android.team_mapper.ui.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * User: madrider
 * Date: 03.09.12
 */
public class GpsService extends Service implements LocationListener {
    public static final String STOP_ALL = "name.dudkov.android.tean_napper.stop_all";
    public static final String GPS = "name.dudkov.android.tean_napper.gps";
    private static final String TAG = GpsService.class.getName();
    private static final int NOTIFICATION_ID = 1;
    private static final int WHAT_SEND = 1;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "broadcast received: " + action);
            if (action.equals(GPS)) {
                setUpLocationListener();
            }
            if (action.equals(STOP_ALL)) {
                Log.i(TAG, "service stopped");
                stopSelf();
            }
        }
    };
    private LocationManager lm;
    private Future future;
    private long interval = TimeUnit.MINUTES.toMillis(2);

    private final Handler h = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message message) {
            if (message.what == WHAT_SEND) {
                Log.i(TAG, "alarm");
                h.removeMessages(WHAT_SEND);
                try {
                    doSend();
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                } finally {
                    h.sendEmptyMessageDelayed(WHAT_SEND, interval);
                }
                return true;
            }
            return false;
        }
    });

    @Override
    public void onCreate() {
        super.onCreate();
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        setUpLocationListener();
        IntentFilter filter = new IntentFilter();
        filter.addAction(STOP_ALL);
        filter.addAction(GPS);
        registerReceiver(myReceiver, filter);
        Log.i(TAG, "service started");
        setFg();
        sendNow();
    }

    @Override
    public int onStartCommand(android.content.Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lm.removeUpdates(this);
        executorService.shutdown();
        try {
            unregisterReceiver(myReceiver);
        } catch (Exception ignored) {
        }
        Log.i(TAG, "service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onLocationChanged(Location location) {
        if (new LocationProcessor(MainApplication.getState()).setLocation(location)) {
            MainApplication.getUiHandler().sendEmptyMessage(0);
            setFg();
        }
    }

    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    public void onProviderEnabled(String s) {
    }

    public void onProviderDisabled(String s) {
    }

    public void setUpLocationListener() {
        try {
            lm.removeUpdates(this);
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        long timeout = TimeUnit.SECONDS.toMillis(1);
        List<String> providers = new ArrayList<String>(Arrays.asList(LocationManager.PASSIVE_PROVIDER, LocationManager.NETWORK_PROVIDER));
        if (MainApplication.getState().isGpsOn()) {
            providers.add(LocationManager.GPS_PROVIDER);
        }
        for (String provider : providers) {
            try {
                lm.requestLocationUpdates(provider, timeout, 0, this);
            } catch (Exception e) {
                Log.e(TAG, "cannot register provider " + provider, e);
            }
        }
    }

    public void setFg() {
        Notification notification = new Notification(R.drawable.radar, null, System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        StringBuilder sb = new StringBuilder();
        sb.append("team:");
        sb.append(MainApplication.getPrefs().getAlias());
        GpsPoint p = MainApplication.getState().getNearestPoint();
        if (p != null) {
            sb.append(", nearest:");
            sb.append(p.getName());
            sb.append(",");
            sb.append(String.format("%.1fm", p.distTo(MainApplication.getState().getLocation())));
        }
        notification.setLatestEventInfo(this, "Team_mapper", sb.toString(), pendingIntent);
        startForeground(NOTIFICATION_ID, notification);
    }

    private void sendNow() {
        h.sendEmptyMessage(WHAT_SEND);
    }

    private void showToast(final String text) {
        final Handler uiHandler = MainApplication.getUiHandler();
        Message msg = new Message();
        msg.what = 1;
        Bundle data = new Bundle();
        data.putString("msg", text);
        msg.setData(data);
        uiHandler.sendMessage(msg);
    }

    private void doSend() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || ! networkInfo.isConnected()) {
                MainApplication.getState().setServerAnswer("no network");
                showToast("no network");
                Log.i(TAG, "no network");
                return;
            }
        }
        if (future == null || future.isDone()) {
            future = executorService.submit(new Sender(MainApplication.getState(), new Runnable() {
                @Override
                public void run() {
                    final Handler uiHandler = MainApplication.getUiHandler();
                    uiHandler.sendEmptyMessage(0);
                    if (! MainApplication.getState().getServerAnswer().isEmpty()) {
                        showToast("server : " + MainApplication.getState().getServerAnswer());
                    }
                }
            }));
        } else {
            Log.i(TAG, "busy");
        }
    }
}
