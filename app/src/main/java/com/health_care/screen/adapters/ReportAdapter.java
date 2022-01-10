package com.health_care.screen.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.health_care.R;
import com.health_care.model.Report;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportHolder> {

    List<Report> reports;
    Activity activity;

    public ReportAdapter(List<Report> reports, Activity activity) {
        this.reports = reports;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ReportHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item, parent, false);
        ReportHolder holder = new ReportHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReportHolder holder, int position) {

        holder.report_text.setText(reports.get(position).getReportText());

        holder.delete_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteReport(position);
            }
        });

    }

    private void deleteReport(int position) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("reports")
                .document(reports.get(position).getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.i("TAG", "onComplete: success   ");
                        task.getResult().getReference()
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.i("TAG", "onComplete: success ");

//                                        notifyDataSetChanged();

                                    }
                                });
                    }
                });


    }

    @Override
    public int getItemCount() {
        return reports.size();
    }
}

class ReportHolder extends RecyclerView.ViewHolder {

    TextView report_text;
    ImageButton delete_report;

    public ReportHolder(@NonNull View itemView) {
        super(itemView);
        report_text = itemView.findViewById(R.id.report_text);
        delete_report = itemView.findViewById(R.id.delete_report);

    }
}
