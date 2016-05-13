package fr.sdis83.remocra;


import android.app.ActionBar;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by jpt on 30/09/13.
 */
public class HelpActivity extends FragmentActivity {

    private ScheduledExecutorService scheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        Context ctx = getApplicationContext();
        String version;
        try {
            version = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "inconnue";
        }
        TextView textViewInfo = (TextView)findViewById(R.id.version);
        textViewInfo.setText("Version " + version);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        Location location = ((RemocraApp) getApplicationContext()).getCurrentLocation();
                        final StringBuilder msg = new StringBuilder("lat ");
                        msg.append(location.getLatitude());
                        msg.append(" ; lon ");
                        msg.append(location.getLongitude());
                        msg.append(" (").append(location.getProvider()).append(" ").append(location.getAccuracy()).append(") ");
                        ((TextView) findViewById(R.id.message)).setText(msg.toString());
                    }
                }, 0, 3, TimeUnit.SECONDS);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}
