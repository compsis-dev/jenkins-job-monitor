package com.compsis.jenkins.application;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan ( "com.compsis.jenkins" )
public class JenkinsMonitorApplication {
    private static final Logger logger = LoggerFactory.getLogger( JenkinsMonitorApplication.class );

    public static void main ( String[] args ) {
        JenkinsMonitorApplication.run( JenkinsMonitorApplication.class , args );
    }

    public static ConfigurableApplicationContext run ( Class < JenkinsMonitorApplication > applicationClass , String[] args ) {
        logger.info( "Starting application..." );
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext( applicationClass );
        Server server = context.getBean( Server.class );

        try {
            server.start();
            server.join();
        } catch ( Exception e ) {
            logger.warn( "Web server stoped" , e );
        } finally {
            server.destroy();
            logger.info( "Web server destroyed" );
        }

        return context;
    }
}