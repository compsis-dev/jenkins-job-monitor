package com.compsis.jenkins.interfaces.job;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.compsis.jenkins.interfaces.facade.ApplicationConfigFacade;

@Component
@Scope ( "singleton" )
public class ConfigurationLoaderJob extends FixedDelayJob {
    private static final Logger logger = LoggerFactory.getLogger( ConfigurationLoaderJob.class );

    private static final int DELAY_SECONDS = 15;

    @Autowired
    ApplicationConfigFacade applicationConfigFacade;

    public void execute () {
        if ( ! applicationConfigFacade.hasChanged() ) {
            logger.debug( "File not changed" );
            return;
        }

        logger.debug( "File changed, reloading..." );
        applicationConfigFacade.reload();
    }

    @Override
    protected int getDelay () {
        return DELAY_SECONDS;
    }

    @Override
    protected TimeUnit getUnit () {
        return TimeUnit.SECONDS;
    }
}