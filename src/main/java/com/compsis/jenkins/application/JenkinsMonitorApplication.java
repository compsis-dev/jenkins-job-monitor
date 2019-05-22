package com.compsis.jenkins.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class JenkinsMonitorApplication {
    private static final Logger logger = LoggerFactory.getLogger( JenkinsMonitorApplication.class );

    public static void main ( String[] args ) {
        JenkinsMonitorApplication.run( JenkinsMonitorApplication.class , args );
    }

    public static ConfigurableApplicationContext run ( Class < JenkinsMonitorApplication > applicationClass , String[] args ) {
        logger.info( "Initializing application..." );
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.scan( "com.compsis.jenkins" );
        applicationContext.refresh();
        applicationContext.close();
        return applicationContext;
    }
}