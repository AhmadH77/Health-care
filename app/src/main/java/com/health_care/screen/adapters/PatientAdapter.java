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

import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientHolder> {

    List<UserInfo> patients;
    Activity activity;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    View statusDialogView;

    public PatientAdapter(List<UserInfo> patients, Activity activity) {
        this.patients = patients;
        this.activity = activity;
    }

    @NonNull
    @Override
    public PatientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_item,  parent, false);
        LayoutInflater inflater = activity.getLayoutInflater();
        statusDialogView = LayoutInflater.from(parent.getContext()).inflate(R.layout.change_status_dialog, parent,false);

        PatientHolder holder = new PatientHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PatientHolder holder, int position) {

        holder.patientName.setText(patients.get(position).getName());
        holder.status.setText(patients.get(position).getStatus());
        if(patients.get(position).getStatus().equals("Patient"))
            holder.status.setTextColor(activity.getResources().getColor(android.R.color.holo_red_dark));
        holder.changeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(position);
            }
        });
    }

     void showDialog(int position) {
        //TODO SHOW DEFAULT STATUS



         new AlertDialog.Builder(activity,R.style.MyDialogTheme)
                .setIcon(R.drawable.ic_healing)
                .setTitle("Change Patient's status")
                .setView(statusDialogView)
                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        changeStatus(position);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void changeStatus(int position) {


        radioGroup = (RadioGroup) statusDialogView.findViewById(R.id.radio);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) statusDialogView.findViewById(selectedId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(patients.get(position).getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.i("TAG", "onComplete: success  patients.get(position).getId() "+
                                task.getResult().get("name") + "  " + task.getResult().get("status"));
                        task.getResult().getReference()
                                .update("status", radioButton.getText())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.i("TAG", "onComplete: success  patients.get(position).getId() "+
                                                patients.get(position).getId() + " radioButton.getText() "
                                                + radioButton.getText());

                                        PatientAdapter.this.notifyItemChanged(position);
//                                        notifyDataSetChanged();

                                    }
                                });
                    }
                });


    }

    @Override
    public int getItemCount() {
        return patients.size();
    }
}

class PatientHolder extends RecyclerView.ViewHolder{

    TextView patientName, status;
    ImageButton changeStatusButton;
    public PatientHolder(@NonNull View itemView) {
        super(itemView);
        patientName = itemView.findViewById(R.id.patient_name);
        status = itemView.findViewById(R.id.status);
        changeStatusButton = itemView.findViewById(R.id.change_status_button);

    }
}
