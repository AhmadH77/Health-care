package com.health_care.screen.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.health_care.R;
import com.health_care.model.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NearByPatientAdapter extends RecyclerView.Adapter<NearByPatientHolder> {

    ArrayList<UserInfo> nearbyPatients;
    Activity activity;


    public NearByPatientAdapter(ArrayList<UserInfo> nearbyPatients, Activity activity) {
        this.nearbyPatients = nearbyPatients;
        this.activity = activity;
    }

    @NonNull
    @Override
    public NearByPatientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_patient_item,  parent, false);

        NearByPatientHolder holder = new NearByPatientHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NearByPatientHolder holder, int position) {

        holder.patientName.setText(nearbyPatients.get(holder.getAdapterPosition()).getName());
        holder.status.setText(nearbyPatients.get(holder.getAdapterPosition()).getStatus());
        if(nearbyPatients.get(holder.getAdapterPosition()).getStatus().equals("Patient"))
            holder.status.setTextColor(activity.getResources().getColor(android.R.color.holo_red_dark));
        else
            holder.status.setTextColor(activity.getResources().getColor(R.color.teal_500));
        holder.distance.setText(nearbyPatients.get(holder.getAdapterPosition()).getDistance() + " m");
    }


    @Override
    public int getItemCount() {
        return nearbyPatients.size();
    }
}

class NearByPatientHolder extends RecyclerView.ViewHolder{

    TextView distance , patientName, status;
    public NearByPatientHolder(@NonNull View itemView) {
        super(itemView);
        distance = itemView.findViewById(R.id.distance);
        patientName = itemView.findViewById(R.id.patient_name);
        status = itemView.findViewById(R.id.patient_status);

    }
}
