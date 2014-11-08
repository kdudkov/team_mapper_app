package name.dudkov.android.team_mapper.data;


import android.location.Location;

public class GpsPoint {
    private final String name;
    private final Location location;
    private final PointType type;

    public GpsPoint(PointType type, String name, double lon, double lat) {
        this.type = type;
        this.name = name;
        this.location = new Location("");
        this.location.setLatitude(lat);
        this.location.setLongitude(lon);
    }

    public GpsPoint(String name, double lon, double lat) {
        this(PointType.POINT, name, lon, lat);
    }

    public GpsPoint(PointType type, String name, Location loc) {
        this.type = type;
        this.name = name;
        this.location = loc;
    }

    public GpsPoint(String name, Location loc) {
        this(PointType.POINT, name, loc);
    }

    public float distTo(Location loc) {
        return this.location.distanceTo(loc);
    }

    public String toString() {
        return String.format("%s %s", this.name, this.ll_d());
    }

    public String ll_d() {
        return String.format("%.6f lat %.6f lon", this.location.getLatitude(), this.location.getLongitude());
    }

    public PointType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GpsPoint gpsPoint = (GpsPoint) o;

        if (type != gpsPoint.type) return false;
        if (!name.equals(gpsPoint.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
