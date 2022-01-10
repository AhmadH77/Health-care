package com.health_care.model;

public class Report {
    String id , reportText;

    public Report(String id, String reportText){
        this.id = id;
        this.reportText = reportText;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setReportText(String reportText) {
        this.reportText = reportText;
    }

    public String getReportText() {
        return reportText;
    }
}
