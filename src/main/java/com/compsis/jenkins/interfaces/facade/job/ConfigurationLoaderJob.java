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

import com.compsis.jenkins.interfaces.facade.ApplicationConfigFacade;

@Component
@Scope ( "singleton" )
public class ConfigurationLoaderJob implements InitializingBean , Runnable {
    private static final Logger logger = LoggerFactory.getLogger( ConfigurationLoaderJob.class );

    private static boolean RUNNING = false;

    @Autowired
    ApplicationConfigFacade applicationConfigFacade;

    @Override
    public synchronized void run () {
        RUNNING = true;
        try {
            execute();
        } catch ( RuntimeException e ) {
            logger.warn( "Unable check application config file" , e );
        } finally {
            RUNNING = false;
        }
    }

    private void execute () {
        if ( applicationConfigFacade.isFileChanged() ) {
            applicationConfigFacade.reload();
        }
    }

    @Override
    public void afterPropertiesSet () {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool( 1 );
        scheduler.scheduleWithFixedDelay( this , 10 , 5 , TimeUnit.SECONDS );
    }

    public boolean isRunning () {
        return RUNNING;
    }
}