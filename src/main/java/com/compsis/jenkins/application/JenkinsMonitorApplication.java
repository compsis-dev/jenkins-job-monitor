package com.compsis.jenkins.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import io.javalin.Javalin;

@Configuration
public class JenkinsMonitorApplication implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger( JenkinsMonitorApplication.class );

    private static long STARTED_MILLIS;
    private static final int SERVER_PORT = 8080;

    @Autowired
    DefaultListableBeanFactory beanFactory;

    public static void main ( String[] args ) {
        JenkinsMonitorApplication.run( JenkinsMonitorApplication.class , args );
    }

    public static ConfigurableApplicationContext run ( Class < JenkinsMonitorApplication > applicationClass , String[] args ) {
        STARTED_MILLIS = System.currentTimeMillis();
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.scan( "com.compsis.jenkins" );
        applicationContext.refresh();
        applicationContext.close();
        return applicationContext;
    }

    @Override
    public void afterPropertiesSet () {
        Javalin application = Javalin.create();
        application.disableStartupBanner();
        application.start( SERVER_PORT );
        application.get( "/" , ctx -> ctx.result( "up" ) );
        beanFactory.registerSingleton( "application" , Javalin.class );
        logger.info( "Application started in {}ms" , ( System.currentTimeMillis() - STARTED_MILLIS ) );
    }
}