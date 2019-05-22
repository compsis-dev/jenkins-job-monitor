package com.compsis.jenkins.interfaces.job;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class FixedDelayJob implements InitializingBean , Runnable {
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private boolean running;

    @Autowired
    ScheduledExecutorService scheduledExecutorService;

    protected abstract void execute ();

    protected abstract int getDelay ();

    protected abstract TimeUnit getUnit ();

    @Override
    public void run () {
        if ( running ) {
            logger.warn( "Job canceled due a running state" );
            return;
        }

        setRunning( true );
        long t0 = System.currentTimeMillis();
        logger.debug( "Executing job..." );

        try {
            execute();
        } catch ( RuntimeException e ) {
            logger.warn( "Job execution failed" , e );
        } finally {
            setRunning( false );
            logger.debug( "Job executed in {}ms" , ( System.currentTimeMillis() - t0 ) );
        }
    }

    @Override
    public void afterPropertiesSet () {
        scheduledExecutorService.scheduleWithFixedDelay( this , 0 , getDelay() , getUnit() );
        logger.info( "Job scheduled with {} {} interval" , getDelay() , getUnit() );
    }

    public void setRunning ( boolean running ) {
        this.running = running;
    }

    public boolean isRunning () {
        return running;
    }
}