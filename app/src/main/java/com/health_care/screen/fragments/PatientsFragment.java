package com.health_care.screen.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.health_care.R;
import com.health_care.model.UserInfo;
import com.health_care.screen.adapters.PatientAdapter;

import java.util.ArrayList;
import java.util.List;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link PatientsFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class PatientsFragment extends Fragment {

    View view;
    RecyclerView patientsList;
    List<UserInfo> patients;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_patients, container, false);
        Toolbar toolbar = view.findViewById(R.id.patients_tool_bar);
        getActivity().setActionBar(toolbar);
        initViews(view);

        getAllPatients();

        return view;

    }

    private void getAllPatients() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        patients = new ArrayList();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("accountType").toString().equals("2")) {

                                    patients.add(new UserInfo(document.getId() ,document.get("name").toString(),
                                            document.get("email").toString(), document.get("gender").toString(),
                                            document.get("password").toString(), Integer.parseInt(document.get("accountType").toString()),
                                            document.get("status").toString() ,"0"));
                                    Log.d("TAG", document.getId() + " user user=> " + document.getData() + "    "  );

                                }
                            }

                            PatientAdapter adapter = new PatientAdapter(patients,getActivity());
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext() , RecyclerView.VERTICAL, false);

                            patientsList.setHasFixedSize(true);
                            patientsList.setAdapter(adapter);
                            patientsList.setLayoutManager(layoutManager);
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void initViews(View view) {

        patientsList = view.findViewById(R.id.patient_list);



    }
}