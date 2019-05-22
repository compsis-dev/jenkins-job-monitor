package com.compsis.jenkins.interfaces.facade;

import com.compsis.jenkins.interfaces.facade.dto.JenkinsMonitorConfig;

public interface ApplicationConfigFacade {
    JenkinsMonitorConfig getConfig ();

    boolean hasChanged ();

    void reload ();
}