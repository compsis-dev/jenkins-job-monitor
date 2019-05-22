package com.compsis.jenkins.application;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.compsis.jenkins.interfaces.web.HealthCheckServlet;

@Configuration
public class ApplicationConfig {
    private static final Logger logger = LoggerFactory.getLogger( ApplicationConfig.class );

    private static final short CORE_POOL_SIZE = 2;
    private static final String DEFAULT_PORT = "9000";
    private static final String PORT_PROPERTY = "server.port";

    @Autowired
    HealthCheckServlet healthCheckServlet;

    @Bean
    public ScheduledExecutorService threadPool () {
        logger.debug( "Initializing scheduled thread pool, size {}" , CORE_POOL_SIZE );
        return Executors.newScheduledThreadPool( CORE_POOL_SIZE );
    }

    @Bean
    public Server server () {
        String port = System.getProperty( PORT_PROPERTY , DEFAULT_PORT );
        Server server = new Server( Integer.valueOf( port ) );
        server.setHandler( healthCheckServlet );
        return server;
    }
}