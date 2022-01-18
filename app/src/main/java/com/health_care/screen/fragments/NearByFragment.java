package com.health_care.screen.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.health_care.R;
import com.health_care.model.UserInfo;
import com.health_care.screen.activities.LoginActivity;
import com.health_care.screen.activities.MainHomeActivity;
import com.health_care.screen.activities.MainHomePatientsActivity;
import com.health_care.screen.activities.WelcomeScreen;
import com.health_care.screen.adapters.NearByPatientAdapter;
import com.health_care.screen.adapters.PatientAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NearByFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearByFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NearByFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NearByFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NearByFragment newInstance(String param1, String param2) {
        NearByFragment fragment = new NearByFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    ImageButton open_nav;
    TextView title,test;
    RecyclerView nearbyPatientsList;
    ProgressBar progressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    ArrayList<UserInfo> nearbyPatients;
    private LocationRequest locationRequest;
    SharedPreferences preferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_near_by, container, false);

        preferences = getActivity().getSharedPreferences("location",Context.MODE_PRIVATE);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(50000);
        locationRequest.setFastestInterval(2000);

        initViews(view);
        initActions();
        getNearByPatients();

        return view;
    }

    private void getNearByPatients() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        nearbyPatients = new ArrayList();
        Location userLocation = new Location("userLocation");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    progressBar.setVisibility(View.VISIBLE);
                    nearbyPatientsList.setVisibility(View.GONE);
                    LocationServices.getFusedLocationProviderClient(getActivity())
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    mSwipeRefreshLayout.setRefreshing(false);
                                    Log.i("TAG", "onLocationResult: ");
                                    LocationServices.getFusedLocationProviderClient(getActivity())
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();

                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("latitude", String.valueOf(latitude));
                                        editor.putString("longitude", String.valueOf(longitude));
                                        editor.apply();

                                        userLocation.setLatitude(latitude);
                                        userLocation.setLongitude(longitude);
                                        updateCurrentUserLocation(latitude,longitude);
                                        Log.i("TAG", "onLocationResult: latitude  " + latitude + " longitude  " + longitude);

                                        db.collection("users")
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                                Log.i("TAG", "onComplete:    document.getId() " + document.getId()
                                                                        + " FirebaseAuth.getInstance().getUid()  " + FirebaseAuth.getInstance().getUid());
                                                                if (document.get("accountType").toString().equals("2")
                                                                        && !document.getId().equals(FirebaseAuth.getInstance().getUid())
                                                                        && document.get("status").toString().equals("Patient")) {

                                                                    Location location = new Location("location");
                                                                    location.setLatitude(Double.parseDouble(document.get("latitude").toString()));
                                                                    location.setLongitude(Double.parseDouble(document.get("longitude").toString()));
                                                                    Log.i("TAG", "onComplete:" + document.get("status").toString() + " test dis  " + document.get("name")
                                                                            + " u1u " + userLocation.getLatitude() + " u1u " +userLocation.getLongitude()
                                                                            + " u2u " + location.getLatitude()  + " u2u " + location.getLongitude()
                                                                            + "  " + userLocation.distanceTo(location));

                                                                    Float distance = userLocation.distanceTo(location);
                                                                    if (distance <= 2000) {
                                                                        Log.i("TAG", "onComplete: near");
                                                                        nearbyPatients.add(new UserInfo(document.getId() ,document.get("name").toString(),
                                                                                document.get("email").toString(), document.get("gender").toString(),
                                                                                document.get("password").toString(), Integer.parseInt(document.get("accountType").toString()),
                                                                                document.get("status").toString(),String.valueOf(distance.shortValue())));
                                                                    }
                                                                    Log.d("TAG", document.getId() + " user user=> " + document.getData() + "    "  );

                                                                }
                                                            }

                                                            NearByPatientAdapter adapter = new NearByPatientAdapter(nearbyPatients,getActivity());
                                                            GridLayoutManager layoutManager = new GridLayoutManager(getContext() , 2);

                                                            nearbyPatientsList.setHasFixedSize(true);
                                                            nearbyPatientsList.setAdapter(adapter);
                                                            nearbyPatientsList.setLayoutManager(layoutManager);
                                                            progressBar.setVisibility(View.GONE);
                                                            nearbyPatientsList.setVisibility(View.VISIBLE);
                                                        } else {
                                                            progressBar.setVisibility(View.GONE);
                                                            nearbyPatientsList.setVisibility(View.VISIBLE);
                                                            Log.d("TAG", "Error getting documents: ", task.getException());
                                                        }
                                                    }
                                                });
                                    }
                                    else {
                                        progressBar.setVisibility(View.GONE);
                                        nearbyPatientsList.setVisibility(View.VISIBLE);
                                        Log.i("TAG", "onLocationResult: error");
                                    }
                                }
                            }, Looper.getMainLooper());

                }
                else {
                    turnOnGPS();
                }

            } else {
                mSwipeRefreshLayout.setRefreshing(false);
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        mSwipeRefreshLayout.setRefreshing(false);


//        Location userLocation = new Location("userLocation");
//        SharedPreferences preferences = getActivity().getSharedPreferences("location",Context.MODE_PRIVATE);
//        Log.i("ams", "getNearByPatients: " + preferences.getString("latitude","a"));
//
//        userLocation.setLatitude(Double.parseDouble(preferences.getString("latitude","0")));
//        userLocation.setLongitude(Double.parseDouble(preferences.getString("longitude","0")));


    }

    private void updateCurrentUserLocation(double latitude , double longitude ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .update("latitude", String.valueOf(latitude),
                        "longitude", String.valueOf(longitude))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i("TAG", "onComplete: update location isSuccessful " + task.isSuccessful());
                    }
                });
    }

    private void initActions() {
        open_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainHomePatientsActivity) getActivity()).openDrawer();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNearByPatients();
            }
        });
    }

    private void initViews(View view) {
        open_nav = view.findViewById(R.id.open_nav);
        title = view.findViewById(R.id.title);
        test = view.findViewById(R.id.test);
        nearbyPatientsList = view.findViewById(R.id.nearbyPatientsList);
        progressBar = view.findViewById(R.id.progressBar);
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh_items);

        title.setText("NearBy Patients");


    }

    private void turnOnGPS() {

        Log.i("TAG", "turnOnGPS: ");

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                mSwipeRefreshLayout.setRefreshing(false);
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(getContext(), "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Log.i("TAG", "onComplete:ApiException  " + e.getMessage());
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i("TAG", "onComplete: RESOLUTION_REQUIRED");
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(getActivity(), 2);
//                                getCurrentLocation();
                            } catch (IntentSender.SendIntentException ex) {
                                Log.i("TAG", "onComplete: RESOLUTION_REQUIRED SendIntentException   "+ ex.getMessage());

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
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

}