package com.health_care.screen.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.health_care.R;
import com.health_care.screen.fragments.PatientsFragment;
import com.health_care.screen.fragments.ProfileFragment;
import com.health_care.screen.fragments.ReportsFragment;

public class MainHomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.patients);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        PatientsFragment patientsFragment = new PatientsFragment();
        ReportsFragment reportsFragment = new ReportsFragment();
        ProfileFragment profileFragment = new ProfileFragment("MS");

        switch (item.getItemId()) {
            case R.id.patients:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, patientsFragment).commit();
                return true;

            case R.id.reports:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, reportsFragment).commit();
                return true;

            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, profileFragment).commit();
                return true;
        }
        return false;
    }
}