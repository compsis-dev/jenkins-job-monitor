package com.compsis.jenkins.application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import com.compsis.jenkins.interfaces.facade.dto.JenkinsMonitorConfig;

import io.javalin.Javalin;

@Component
public class JenkinsMonitorApplication {
    private static final Logger logger = LoggerFactory.getLogger( JenkinsMonitorApplication.class );

    private static final int SERVER_PORT = 8080;

    private static final String APPLICATION_FILE = "config/application.yml";

    public static void main ( String[] args ) {
        final long t0 = System.currentTimeMillis();
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.scan( "com.compsis.jenkins" );
        applicationContext.refresh();
        applicationContext.close();
        logger.info( "Spring initilized in {}ms" , ( System.currentTimeMillis() - t0 ) );
    }

    @Bean
    @Scope ( "singleton" )
    public Javalin run () throws FileNotFoundException {
        Javalin app = Javalin.create().start( SERVER_PORT );
        app.get( "/" , ctx -> ctx.result( "up" ) );
        return app;
    }

    @Bean
    @Scope ( "singleton" )
    private JenkinsMonitorConfig jenkinsMonitorConfig () throws FileNotFoundException {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties( true );
        Constructor constructor = new Constructor( JenkinsMonitorConfig.class );
        Yaml yaml = new Yaml( constructor , representer );

        InputStream inputStream = new FileInputStream( APPLICATION_FILE );
        return yaml.loadAs( inputStream , JenkinsMonitorConfig.class );
    }
}