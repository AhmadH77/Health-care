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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.health_care.R;
import com.health_care.model.Report;
import com.health_care.screen.adapters.ReportAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReportsFragment extends Fragment {

    View view;
    RecyclerView reportsList;
    List<Report> reports;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        initViews(view);

        getReports();
        return view;
    }

    private void initViews(View view) {
        reportsList = view.findViewById(R.id.reports_list);

    }


    private void getReports() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        reports = new ArrayList();
        db.collection("reports")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                    reports.add(new Report(document.get("id").toString() ,document.get("reportText").toString() ));
                                    Log.d("TAG", document.get("id").toString() + " user user=> " + document.getData() + "    "  );

                            }

                            ReportAdapter adapter = new ReportAdapter(reports,getActivity(),reportsList);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext() , RecyclerView.VERTICAL, false);

                            reportsList.setHasFixedSize(true);
                            reportsList.setAdapter(adapter);
                            reportsList.setLayoutManager(layoutManager);
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}