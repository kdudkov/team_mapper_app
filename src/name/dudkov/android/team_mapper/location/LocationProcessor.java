package name.dudkov.android.team_mapper.location;

import android.location.Location;
import android.util.Log;
import name.dudkov.android.team_mapper.data.State;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * Created by madrider on 29.10.14.
 */
public class LocationProcessor {
    private final static String TAG = LocationProcessor.class.getSimpleName();
    private final static long GET_BEST_WINDOW_TIME = TimeUnit.SECONDS.toMillis(60);

    private final State state;
    private Deque<Location> locations = new LinkedList<Location>();

    public LocationProcessor(State state) {
        this.state = state;
    }

    public boolean setLocation(Location location) {
        // remove old
        long now = System.currentTimeMillis();
        while (true) {
            Location loc = locations.peekFirst();
            if (loc == null || now - loc.getTime() < GET_BEST_WINDOW_TIME) {
                break;
            }
            locations.removeFirst();
        }
        // add new
        locations.addLast(location);
        // get best
        Log.d(TAG, "getting best from " + locations.size());
        Location newLocation = null;
        for (Location loc1 : locations) {
            if (newLocation == null || loc1.getAccuracy() <= newLocation.getAccuracy()) {
                newLocation = loc1;
            }
        }
        if (newLocation != null && !newLocation.equals(state.getLocation())) {
            state.setLocation(newLocation);
            return true;
        }
        return false;
    }

}

