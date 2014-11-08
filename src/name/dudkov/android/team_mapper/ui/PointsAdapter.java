package name.dudkov.android.team_mapper.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import name.dudkov.android.team_mapper.R;
import name.dudkov.android.team_mapper.data.GpsPoint;
import name.dudkov.android.team_mapper.data.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static name.dudkov.android.team_mapper.Utils.formatPointCoords;

/**
 * User: madrider
 * Date: 24.08.12
 */
public class PointsAdapter extends ArrayAdapter<GpsPoint> {

    private final State state;
    private List<GpsPoint> items;
    private int textViewId;

    public PointsAdapter(Context context, int textViewResourceId, final State state) {
        super(context, textViewResourceId, state.getPoints());
        this.state = state;
        this.items = new ArrayList<GpsPoint>(state.getPoints());
        sortItems();
        this.textViewId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(textViewId, null);
        }
        GpsPoint p = this.items.get(position);
        TextView pointName = (TextView) v.findViewById(R.id.list_point_name);
        pointName.setText(String.format("%s %s", p.getType(), p.getName()));
        String coord = formatPointCoords(p, state.getFormat());
        ((TextView) v.findViewById(R.id.list_coord)).setText(coord);
        String dist = "";
        if (state.getLocation() != null) {
            float d = state.getLocation().distanceTo(p.getLocation());
            float b = state.getLocation().bearingTo(p.getLocation());
            if (b < 0) {
                b = b + 360;
            }
            dist = String.format("%.0f m %.0fº", d, b);
            if (d < 50) {
                pointName.setTextColor(Color.parseColor("#cc0000"));
            } else if (d < 100) {
                pointName.setTextColor(Color.parseColor("#cccc00"));
            }
        }
        ((TextView) v.findViewById(R.id.list_dist)).setText(dist);
        return v;
    }

    public void sortItems() {
        if (state.getLocation() != null) {
            Collections.sort(this.items, new Comparator<GpsPoint>() {
                public int compare(GpsPoint gpsPoint, GpsPoint gpsPoint1) {
                    float d1 = state.getLocation().distanceTo(gpsPoint.getLocation());
                    float d2 = state.getLocation().distanceTo(gpsPoint1.getLocation());
                    return Float.compare(d1, d2);
                }
            });
        }
    }

    @Override
    public void notifyDataSetChanged() {
        sortItems();
        super.notifyDataSetChanged();
    }
}
