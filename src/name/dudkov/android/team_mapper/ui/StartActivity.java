package name.dudkov.android.team_mapper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import name.dudkov.android.team_mapper.MainApplication;
import name.dudkov.android.team_mapper.R;
import name.dudkov.android.team_mapper.data.Prefs;

public class StartActivity extends Activity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Prefs prefs = MainApplication.getPrefs();
        ((EditText) findViewById(R.id.alias)).setText(prefs.getAlias());
        ((EditText) findViewById(R.id.code)).setText(prefs.getCode());
    }

    public void onClick(View v) {
        Prefs prefs = MainApplication.getPrefs();
        prefs.setCode(((EditText) findViewById(R.id.code)).getText().toString());
        prefs.setAlias(((EditText) findViewById(R.id.alias)).getText().toString());
        startActivity(new Intent(getBaseContext(), MainActivity.class));
        this.finish();
    }
}
