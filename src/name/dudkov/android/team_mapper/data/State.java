package name.dudkov.android.team_mapper.data;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class State {
    private long serverSendDelay = TimeUnit.MINUTES.toMillis(5);
    private int format = Location.FORMAT_DEGREES;
    private boolean gpsOn = false;
    private Location location;
    private long lastServerConnect;
    private String serverAnswer = "";
    private List<GpsPoint> points = new ArrayList<GpsPoint>();


    public synchronized long getLastServerConnect() {
        return lastServerConnect;
    }

    public synchronized void setLastServerConnect(long lastServerConnect) {
        this.lastServerConnect = lastServerConnect;
    }

    public synchronized Location getLocation() {
        return location;
    }

    public synchronized void setLocation(Location location) {
        this.location = location;
    }

    public synchronized List<GpsPoint> getPoints() {
        return points;
    }

    public synchronized void setPoints(List<GpsPoint> points) {
        this.points = points;
    }

    public synchronized void addPoint(GpsPoint p) {
        this.points.add(p);
    }

    public synchronized long getServerSendDelay() {
        return serverSendDelay;
    }

    public synchronized void setServerSendDelay(long serverSendDelay) {
        this.serverSendDelay = serverSendDelay;
    }

    public synchronized String getServerAnswer() {
        return serverAnswer;
    }

    public synchronized void setServerAnswer(String serverAnswer) {
        this.serverAnswer = serverAnswer;
    }

    public synchronized int getFormat() {
        return format;
    }

    public synchronized void setFormat(int format) {
        this.format = format;
    }

    public synchronized boolean isGpsOn() {
        return gpsOn;
    }

    public synchronized void setGpsOn(boolean gpsOn) {
        this.gpsOn = gpsOn;
    }

    public synchronized GpsPoint getNearestPoint() {
        if (location != null && !points.isEmpty()) {
            float dist = 999999999;
            GpsPoint p1 = null;
            for (GpsPoint p : points) {
                if (p.distTo(location) < dist) {
                    dist = p.distTo(location);
                    p1 = p;
                }
            }
            return p1;
        }
        return null;
    }
}
