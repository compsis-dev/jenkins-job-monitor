package com.compsis.jenkins.interfaces.facade.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.compsis.jenkins.interfaces.facade.ApplicationConfigFacade;
import com.compsis.jenkins.interfaces.facade.JenkinsJobMonitorFacade;
import com.compsis.jenkins.interfaces.facade.dto.JenkinsHook;
import com.compsis.jenkins.interfaces.facade.dto.JenkinsMonitorConfig.JenkinsConfig;

@Service
public class JenkinsJobMonitorFacadeImpl implements JenkinsJobMonitorFacade {
    private static final Logger logger = LoggerFactory.getLogger( JenkinsJobMonitorFacadeImpl.class );

    private static final Map < String , String > JOB_STATUS_CACHE = new HashMap < String , String >();

    @Autowired
    JenkinsHttpClient httpClient;
    @Autowired
    ApplicationConfigFacade applicationConfigFacade;

    @Override
    public void checkJobs () {
        JenkinsConfig jenkins = applicationConfigFacade.getConfig().getJenkins();
        List < JenkinsHook > hooks = jenkins.getHooks();

        jenkins.getJobs().parallelStream() //
                .forEach( jobName -> checkJob( hooks , jobName ) );
    }

    public void checkJob ( List < JenkinsHook > hooks , String jobName ) {
        logger.debug( "Checking job {}" , jobName );
        String newStatus = httpClient.getStatus( jobName );
        if ( newStatus == null ) {
            logger.warn( "Unable to request {} job status" , newStatus );
            return;
        }

        String oldStatus = JOB_STATUS_CACHE.get( jobName );
        if ( oldStatus != null && oldStatus.equals( newStatus ) ) {
            logger.debug( "{} job status don't changed" , jobName );
            return;
        }

        logger.info( "{} job status changed: {}" , jobName , newStatus );
        JOB_STATUS_CACHE.put( jobName , newStatus );

        hooks.parallelStream().forEach( hook -> {
            if ( hook.getStatus().equals( newStatus ) ) {
                httpClient.callHookUrl( hook.getUrl() );
            }
        } );
    }
}