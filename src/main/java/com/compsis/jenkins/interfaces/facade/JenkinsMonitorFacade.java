package com.compsis.jenkins.interfaces.facade;

public interface JenkinsMonitorFacade {
    void checkJobs ();

    void checkJob ( String jobName );
}