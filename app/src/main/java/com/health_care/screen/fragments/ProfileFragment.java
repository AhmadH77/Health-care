package com.health_care.screen.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.health_care.R;
import com.health_care.screen.activities.LoginActivity;
import com.health_care.screen.activities.MainHomePatientsActivity;


public class ProfileFragment extends Fragment {

    ImageView profile_image;
    TextView name, gender, email, account_type, status;
    LinearLayout status_layout,progressBar_layout,log_out;
    ScrollView info_layout;
    ImageButton open_nav;
    String source;

    public ProfileFragment(String source){
        this.source = source;
    }

    //
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        FirebaseApp.initializeApp(getContext());
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initViews(view);
        initAction();
        getUserInfo();

        return view;
    }

    private void initAction() {
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences  preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences preferences1 = getActivity().getSharedPreferences("user" ,Context.MODE_PRIVATE);
                preferences.edit().clear().apply();
                preferences1.edit().clear().apply();

                FirebaseAuth.getInstance().signOut();
                Intent goToLogin = new Intent(getActivity(), LoginActivity.class);
                getActivity().finish();
                getActivity().startActivity(goToLogin);

            }
        });

        open_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainHomePatientsActivity) getActivity()).openDrawer();
            }
        });
    }

    private void getUserInfo() {

        progressBar_layout.setVisibility(View.VISIBLE);
        info_layout.setVisibility(View.GONE);
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        progressBar_layout.setVisibility(View.GONE);
                        info_layout.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Log.d("TAG", document.getId() + " => " + document.getData());
                            email.setText(document.get("email").toString());
                            name.setText(document.get("name").toString());
                            gender.setText(document.get("gender").toString());
                            account_type.setText(document.get("accountType").toString().equals("2") ? "Patient" : "Medical Stuff");
                            if (document.get("accountType").toString().equals("2")){
                                status_layout.setVisibility(View.VISIBLE);
                                status.setText(document.get("status").toString());
                                profile_image.setImageDrawable(getContext().getDrawable(R.drawable.patient));
                            }
                            else
                                profile_image.setImageDrawable(getContext().getDrawable(R.drawable.doctor));

                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    private void initViews(View view) {
        profile_image = view.findViewById(R.id.profile_image);
        name = view.findViewById(R.id.name);
        gender = view.findViewById(R.id.gender);
        email = view.findViewById(R.id.email);
        account_type = view.findViewById(R.id.account_type);
        status = view.findViewById(R.id.status);
        status_layout = view.findViewById(R.id.status_layout);
        progressBar_layout = view.findViewById(R.id.progressBar_layout);
        log_out = view.findViewById(R.id.log_out);
        info_layout = view.findViewById(R.id.info_layout);
        open_nav = view.findViewById(R.id.open_nav);

        if (source.equals("P")){
            open_nav.setVisibility(View.VISIBLE);
        }
        else{
            open_nav.setVisibility(View.GONE);
        }
    }
}