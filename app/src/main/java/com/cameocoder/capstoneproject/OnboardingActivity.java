package com.cameocoder.capstoneproject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.cameocoder.capstoneproject.sync.WasteSyncAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cameocoder.capstoneproject.Utility.saveLocationToPreferences;

public class OnboardingActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = OnboardingActivity.class.getSimpleName();
    private static final int REQUEST_LOCATION_PERMISSION = 111;
    private static final int PLACE_PICKER_REQUEST = 222;

    private GoogleApiClient mGoogleApiClient;

    @BindView(R.id.button_current_location)
    Button currentLocation;
    @BindView(R.id.button_choose_location)
    Button chooseLocation;

    private double latitude;
    private double longitude;
    private boolean gotPickerResult;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WasteSyncAdapter.ACTION_DATA_UPDATE_FAILED)) {
                @WasteSyncAdapter.SyncStatus int reason = intent.getIntExtra(WasteSyncAdapter.EXTRA_DATA_UPDATE_FAILED, 0);
                handleScheduleError(reason);
            } else {
                handleScheduleSynced();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);
        buildGoogleApiClient();

        IntentFilter filter = new IntentFilter();
        filter.addAction(WasteSyncAdapter.ACTION_DATA_UPDATED);
        filter.addAction(WasteSyncAdapter.ACTION_DATA_UPDATE_FAILED);
        registerReceiver(broadcastReceiver, filter);

        if (!Utility.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.no_network_connection,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                saveLocationToPreferences(this, latitude, longitude);
                gotPickerResult = true;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        // Raising a dialog in onActivityResult will cause an IllegalStateException
        if (gotPickerResult) {
            gotPickerResult = false;
            loadSchedule(this, latitude, longitude);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.d(TAG, "onConnectionSuspended: ");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            // Empty permissions and grantResults are returned when device is rotated
            return;
        }
        if ((requestCode == REQUEST_LOCATION_PERMISSION) && (grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            // permission granted we can go ahead and try to get the location
            handleCurrentLocationClick();
        } else {
            // permission denied
            Toast.makeText(this, "Missing Permission needed in order to determine current location", Toast.LENGTH_LONG).show();
            //           currentLocation.setEnabled(false);
        }
    }

    @OnClick(R.id.button_current_location)
    public void handleCurrentLocationClick() {
        Log.d(TAG, "handleCurrentLocationClick: ");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            getLocation();
        }
    }

    @OnClick(R.id.button_choose_location)
    public void handleChooseLocationClick() {
        Log.d(TAG, "handleChooseLocationClick: ");
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void getLocation() {
        boolean permission = true;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permission = false;
        }
        if (permission) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastLocation != null) {
                saveLocationToPreferences(this, lastLocation.getLatitude(), lastLocation.getLongitude());
                Log.d(TAG, "getLocation: " + lastLocation.getLatitude() + "," + lastLocation.getLongitude());
                loadSchedule(this, lastLocation.getLatitude(), lastLocation.getLongitude());
            } else {
                Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void loadSchedule(Context context, double latitude, double longitude) {
        Log.d(TAG, "loadSchedule: ");
        showProgressDialog();
        WasteSyncAdapter.syncPlace(context, latitude, longitude);
    }

    private void handleScheduleSynced() {
        hideProgressDialog();
        Intent i = getIntent(); // gets the intent that called this intent
        setResult(RESULT_OK, i);
        finish();
    }

    private void handleScheduleError(@WasteSyncAdapter.SyncStatus int reason) {
        hideProgressDialog();
        int message = R.string.sync_unknown_error;
        if (reason == WasteSyncAdapter.SYNC_STATUS_INVALID) {
            message = R.string.sync_no_schedule;
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void showProgressDialog() {
        ProgressDialogFragment fragment = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG);
        if (fragment == null) {
            fragment = new ProgressDialogFragment();
            fragment.setCancelable(false);
            fragment.show(getSupportFragmentManager(), ProgressDialogFragment.TAG);
        }
    }

    public void hideProgressDialog() {
        ProgressDialogFragment fragment = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG);
        if (fragment != null) {
            fragment.dismiss();
        }
    }

    public static class ProgressDialogFragment extends DialogFragment {
        public static final String TAG = "ProgressDialogFragment";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setCancelable(false);
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            ProgressDialog dialog = new ProgressDialog(getActivity(), getTheme());
            dialog.setTitle("Loading schedule");
            dialog.setMessage("Please wait while waste schedule is loaded");
            dialog.setIndeterminate(true);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            return dialog;
        }
    }
}
