package com.health_care.screen.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.health_care.R;

import java.util.HashMap;

public class WelcomeScreen extends AppCompatActivity {

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    String TAG = "test WelcomeScreen ";
    private LocationRequest locationRequest;
    SharedPreferences preferences;
    SharedPreferences preferences1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        //TODO CHANGE IMAGE

         preferences = getSharedPreferences("location",Context.MODE_PRIVATE);
         preferences1 = getSharedPreferences("user" ,Context.MODE_PRIVATE);
        Log.i(TAG, "onLocationResult: test  " + preferences1.getBoolean("logged",false));

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(50000);
        locationRequest.setFastestInterval(2000);

        getCurrentLocation();
//        Intent intent = new Intent(WelcomeScreen.this , MainHomePatientsActivity.class);
//        startActivity(intent);


    }

    private void getCurrentLocation() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(WelcomeScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(WelcomeScreen.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    Log.i(TAG, "onLocationResult: ");
                                    LocationServices.getFusedLocationProviderClient(WelcomeScreen.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();

                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("latitude", String.valueOf(latitude));
                                        editor.putString("longitude", String.valueOf(longitude));
                                        editor.apply();

                                        Log.i(TAG, "onLocationResult: test  " + preferences1.getBoolean("logged",false));
                                        if(preferences1.getBoolean("logged",false)) {

                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            db.collection("users")
                                                    .document(FirebaseAuth.getInstance().getUid())
                                                    .update("latitude", String.valueOf(latitude),
                                                            "longitude", String.valueOf(longitude))
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {
                                                                if(preferences1.getString("accountType", "").equals("2")) {
                                                                    Intent goToMain = new Intent(WelcomeScreen.this, MainHomePatientsActivity.class);//MainHomeActivity
                                                                    startActivity(goToMain);
                                                                    finish();
                                                                }
                                                                else{
                                                                    Intent goToMain = new Intent(WelcomeScreen.this, MainHomeActivity.class);//MainHomeActivity
                                                                    startActivity(goToMain);
                                                                    finish();
                                                                }
                                                            }
                                                            else{
                                                                Log.i(TAG, "onComplete: failed  ");
                                                                Toast.makeText(WelcomeScreen.this, "Failed" , Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });


                                        }
                                        else{
                                            Intent intent = new Intent(WelcomeScreen.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        Log.i(TAG, "onLocationResult: latitude  " + latitude + " longitude  " + longitude);

                                    }
                                    else
                                        Log.i(TAG, "onLocationResult: error");
                                }
                            }, Looper.getMainLooper());

                }
                else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void turnOnGPS() {

        Log.i(TAG, "turnOnGPS: ");

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(WelcomeScreen.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {
                    Log.i(TAG, "onComplete:ApiException  " + e.getMessage());
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i(TAG, "onComplete: RESOLUTION_REQUIRED");
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(WelcomeScreen.this, 2);
//                                getCurrentLocation();
                            } catch (IntentSender.SendIntentException ex) {
                                Log.i(TAG, "onComplete: RESOLUTION_REQUIRED SendIntentException   "+ ex.getMessage());

                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if (isGPSEnabled()) {

                    getCurrentLocation();

                }else {

                    turnOnGPS();
                }
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentLocation();
            }
        }
    }

}