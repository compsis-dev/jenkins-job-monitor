package com.compsis.jenkins.interfaces.facade.dto;

public class JenkinsHook {
    private String status;
    private String url;

    public String getStatus () {
        return status;
    }

    public void setStatus ( String status ) {
        this.status = status;
    }

    public String getUrl () {
        return url;
    }

    public void setUrl ( String url ) {
        this.url = url;
    }
}