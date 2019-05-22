package com.compsis.jenkins.interfaces.job;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.compsis.jenkins.interfaces.facade.JenkinsJobMonitorFacade;

@Component
@Scope ( "singleton" )
public class JenkinsMonitorJob extends FixedDelayJob {
    private static final int DELAY_MINUTES = 2;

    @Autowired
    JenkinsJobMonitorFacade jobFacade;

    @Override
    protected void execute () {
        jobFacade.checkJobs();
    }

    @Override
    protected int getDelay () {
        return DELAY_MINUTES;
    }

    @Override
    protected TimeUnit getUnit () {
        return TimeUnit.MINUTES;
    }
}