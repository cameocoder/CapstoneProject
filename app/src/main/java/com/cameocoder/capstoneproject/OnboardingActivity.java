package com.cameocoder.capstoneproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
    boolean didSync = false;

    @BindView(R.id.button_current_location)
    Button currentLocation;
    @BindView(R.id.button_choose_location)
    Button chooseLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);

        buildGoogleApiClient();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                saveLocationToPreferences(this, place.getLatLng().latitude, place.getLatLng().longitude);
                Log.d(TAG, "getLocation: " + place.getLatLng().latitude + "," + place.getLatLng().longitude);
                WasteSyncAdapter.syncPlace(this, place.getLatLng().latitude, place.getLatLng().longitude);

                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                Intent i = getIntent(); //gets the intent that called this intent
                setResult(RESULT_OK, i);
                finish();
            }
        }
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
//        getLocation();
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            getLocation();
//        Bundle b = new Bundle();
//        b.putString(Utility.PREF_PLACE_ID, value);
            Intent i = getIntent(); //gets the intent that called this intent
//        i.putExtras(b);
            setResult(RESULT_OK, i);
            finish();
        }
    }

    @OnClick(R.id.button_choose_location)
    public void handleChooseLocationClick() {
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
                WasteSyncAdapter.syncPlace(this, lastLocation.getLatitude(), lastLocation.getLongitude());
            } else {
                Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
            }
        }
    }


}
