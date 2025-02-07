package com.tcube.api.model;

import java.util.Date;

public class DashboardReleaseNotes {

    private String version;
    
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getDor() {
        return dor;
    }

    public void setDor(Date dor) {
        this.dor = dor;
    }

    private Date dor;
}
