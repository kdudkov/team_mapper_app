package name.dudkov.android.team_mapper;

import android.location.Location;
import name.dudkov.android.team_mapper.data.GpsPoint;

/**
 * Created by madrider on 09.06.14
 */
public class Utils {

    public static String formatCoord(double coord) {
        return formatCoord(coord, Location.FORMAT_DEGREES);
    }

    public static String formatCoord(double coord, int fmt) {
        if (fmt == Location.FORMAT_DEGREES) {
            return String.format("%.5f", coord);
        } else {
            double s = coord;
            int d = (int) s;
            s = (s - d) * 60;
            int m = (int) s;
            s = (s - m) * 60;
            return String.format("%dº%2d'%.2f\"", d, m, s);
        }
    }

    public static String formatPointCoords(GpsPoint point, int format) {
        return String.format("lat: %s, lon: %s",
                formatCoord(point.getLocation().getLatitude(), format),
                formatCoord(point.getLocation().getLatitude(), format)
        );
    }

    public static String formatPointCoords(GpsPoint point) {
        return formatPointCoords(point, Location.FORMAT_DEGREES);
    }
}
