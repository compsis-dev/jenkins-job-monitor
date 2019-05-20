package com.compsis.jenkins.interfaces.facade.job;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.compsis.jenkins.interfaces.facade.JenkinsJobMonitorFacade;

@Component
@Scope ( "singleton" )
public class JenkinsMonitorJob implements InitializingBean , Runnable {
    private static final Logger logger = LoggerFactory.getLogger( JenkinsMonitorJob.class );

    private static boolean RUNNING = false;

    @Autowired
    JenkinsJobMonitorFacade facade;

    @Override
    public synchronized void run () {
        RUNNING = true;

        try {
            facade.checkJobs();
        } catch ( RuntimeException e ) {
            logger.warn( "Execution failed" , e );
        } finally {
            RUNNING = false;
        }
    }

    @Override
    public void afterPropertiesSet () {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool( 1 );
        scheduler.scheduleWithFixedDelay( this , 0 , 10 , TimeUnit.SECONDS );
    }

    public Boolean isRunning () {
        return RUNNING;
    }
}