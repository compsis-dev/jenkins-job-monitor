package com.compsis.jenkins.interfaces.facade.internal;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.compsis.jenkins.interfaces.facade.JenkinsMonitorFacade;
import com.compsis.jenkins.interfaces.facade.dto.JenkinsMonitorConfig;
import com.compsis.jenkins.interfaces.facade.dto.JenkinsMonitorConfig.JenkinsConfig;

@Service
public class JenkinsMonitorFacadeImpl implements JenkinsMonitorFacade {
    private static final Logger logger = LoggerFactory.getLogger( JenkinsMonitorFacadeImpl.class );

    private static final Map < String , String > JOB_STATUS_CACHE = new HashMap < String , String >();

    @Autowired
    JenkinsMonitorConfig config;
    @Autowired
    JenkinsHttpClient httpClient;

    @Override
    public void checkJobs () {
        JenkinsConfig jenkins = config.getJenkins();
        jenkins.getJobs().parallelStream() //
                .forEach( jobName -> checkJob( jobName ) );
    }

    @Override
    public void checkJob ( String jobName ) {
        logger.debug( "Checking job {}" , jobName );
        String newStatus = httpClient.getStatus( jobName );
        if ( newStatus == null ) {
            logger.warn( "Unable to request job status" );
            return;
        }

        String oldStatus = JOB_STATUS_CACHE.get( jobName );
        if ( oldStatus != null && oldStatus.equals( newStatus ) ) {
            logger.debug( "Job status don't changed" );
            return;
        }

        JOB_STATUS_CACHE.put( jobName , newStatus );
        config.getJenkins().getHooks().parallelStream().forEach( hook -> {
            if ( hook.getStatus().equals( newStatus ) ) {
                httpClient.callHookUrl( hook.getUrl() );
            }
        } );
        logger.debug( "Job {} status: {}" , jobName , newStatus );
    }
}