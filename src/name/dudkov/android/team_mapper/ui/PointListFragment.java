package name.dudkov.android.team_mapper.ui;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ListFragment;
import android.os.Build;
import android.os.Bundle;
import name.dudkov.android.team_mapper.MainApplication;
import name.dudkov.android.team_mapper.R;

/**
 * Created by madrider on 08.11.14.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PointListFragment extends ListFragment {

    private PointsAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new PointsAdapter(getActivity(), R.layout.point_list_item, MainApplication.getState());
        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void update() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

}
