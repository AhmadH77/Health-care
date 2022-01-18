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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.health_care.R;
import com.health_care.screen.activities.MainHomeActivity;
import com.health_care.screen.activities.MainHomePatientsActivity;
import com.health_care.screen.activities.RegisterActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SendReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SendReportFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SendReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SendReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SendReportFragment newInstance(String param1, String param2) {
        SendReportFragment fragment = new SendReportFragment();
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
    ExtendedFloatingActionButton send;
    TextInputLayout reportText;
    ProgressBar progressBar;
    LinearLayout send_report_layout;
    TextView title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_report, container, false);

        initViews(view);
        initActions();


        return view;
    }

    private void initViews(View view) {

        open_nav = view.findViewById(R.id.open_nav);
        send = view.findViewById(R.id.send);
        reportText = view.findViewById(R.id.report_text);
        progressBar = view.findViewById(R.id.progressBar);
        send_report_layout = view.findViewById(R.id.send_report_layout);
        title = view.findViewById(R.id.title);


        title.setText("Send Report");

    }

    private void initActions() {
        open_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainHomePatientsActivity) getActivity()).openDrawer();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate())
                    sendReport();
            }
        });
    }

    private void sendReport() {
        //

        progressBar.setVisibility(View.VISIBLE);
        send_report_layout.setVisibility(View.GONE);
        Map<String, Object> report = new HashMap<>();
        report.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        report.put("reportText", reportText.getEditText().getText().toString());

        // Add a new document with a generated ID
        FirebaseFirestore.getInstance()
                .collection("reports")
                .document()
                .set(report)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        send_report_layout.setVisibility(View.VISIBLE);

                        Log.i("TAG", "onComplete: " + task.isSuccessful());
                        if (task.isSuccessful()){
                            Toast.makeText(getContext(), "Report Sent Successfully.",
                                    Toast.LENGTH_LONG).show();
                            reportText.getEditText().setText("");
                        }
                        else{
                            Log.i("TAG", "onComplete: fail  " + task.getResult());
                            Toast.makeText(getContext(), "Send Report failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private boolean validate() {
        boolean validate = true;
        if(reportText.getEditText().getText().toString().replaceAll(" ","").equals("")) {
            reportText.setError("Report Text is Required");
            validate = false;
        }
        else
            reportText.setError(null);

        return validate;
    }
}