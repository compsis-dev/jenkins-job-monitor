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
    private static final int DELAY_MINUTES = 2;

    @Autowired
    JenkinsJobMonitorFacade jobFacade;

    @Override
    public synchronized void run () {
        RUNNING = true;
        try {
            jobFacade.checkJobs();
        } catch ( RuntimeException e ) {
            logger.warn( "Job execution failed" , e );
        } finally {
            RUNNING = false;
        }
    }

    @Override
    public void afterPropertiesSet () {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool( 1 );
        scheduler.scheduleWithFixedDelay( this , 0 , DELAY_MINUTES , TimeUnit.MINUTES );
        logger.info( "Job scheduled with {} minutes interval" , DELAY_MINUTES );
    }

    public Boolean isRunning () {
        return RUNNING;
    }
}