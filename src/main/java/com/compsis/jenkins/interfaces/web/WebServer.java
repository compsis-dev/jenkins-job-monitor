package com.compsis.jenkins.interfaces.web;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebServer implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger( WebServer.class );

    private static final int SERVER_PORT = 8080;

    @Autowired
    HealthCheckServlet healthCheckServlet;

    @Bean
    public Server server () {
        return new Server( SERVER_PORT );
    }

    @Override
    public void afterPropertiesSet () throws Exception {
        Server server = server();
        try {
            server.setHandler( healthCheckServlet );
            server.start();
            server.join();
        } finally {
            server.destroy();
            logger.info( "Server destroyed" );
        }
    }
}