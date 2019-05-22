package com.compsis.jenkins.application;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    private static final Logger logger = LoggerFactory.getLogger( ApplicationConfig.class );

    private static final int CORE_POOL_SIZE = 2;

    @Bean
    public ScheduledExecutorService threadPool () {
        logger.debug( "Initializing scheduled thread pool, size {}" , CORE_POOL_SIZE );
        return Executors.newScheduledThreadPool( CORE_POOL_SIZE );
    }
}