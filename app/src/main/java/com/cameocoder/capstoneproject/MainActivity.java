package com.cameocoder.capstoneproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cameocoder.capstoneproject.sync.WasteSyncAdapter;

import static com.cameocoder.capstoneproject.Utility.isNetworkAvailable;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.no_network_connection,
                    Toast.LENGTH_LONG).show();
        }

        WasteSyncAdapter.initializeSyncAdapter(this);

        if (savedInstanceState == null) {
            String zoneName = Utility.getZoneNameFromPreferences(this);
            if (TextUtils.isEmpty(zoneName)) {
                startActivityForResult(new Intent(this, OnboardingActivity.class), REQUEST_LOCATION);
            } else {
                WasteSyncAdapter.syncPickUpDays(this, zoneName);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOCATION) {
            if (resultCode == RESULT_OK) {
                String placeId = Utility.getPlaceIdFromPreferences(this);
                WasteSyncAdapter.syncSchedule(this, placeId);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem location = menu.findItem(R.id.action_location);
        location.setEnabled(isNetworkAvailable(this));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_location) {
            startActivityForResult(new Intent(this, OnboardingActivity.class), REQUEST_LOCATION);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
