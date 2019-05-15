package com.compsis.jenkins.interfaces.facade.internal;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.compsis.jenkins.interfaces.facade.dto.JenkinsMonitorConfig;
import com.compsis.jenkins.interfaces.facade.dto.JenkinsMonitorConfig.JenkinsConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
public class JenkinsHttpClient {
    private static final Logger logger = LoggerFactory.getLogger( JenkinsHttpClient.class );

    @Autowired
    JenkinsMonitorConfig config;

    Gson gson;
    HttpClient client;

    public JenkinsHttpClient () {
        client = HttpClient.newHttpClient();
        gson = new GsonBuilder().create();
    }

    public String getStatus ( String jobName ) {
        JenkinsConfig jenkinsConfig = config.getJenkins();

        StringBuilder jobStatusUri = new StringBuilder( jenkinsConfig.getUrl() );
        for ( String jobPath : jobName.split( "/" ) ) {
            jobStatusUri.append( "/job/" ).append( jobPath );
        }
        jobStatusUri.append( "/lastBuild/api/json" );

        HttpRequest request = HttpRequest.newBuilder() //
                .uri( URI.create( jobStatusUri.toString() ) ) //
                .timeout( Duration.ofMinutes( 1 ) ) //
                .header( "Authorization" , getAuthorization() ) //
                .GET().build();

        try {
            String body = client.send( request , BodyHandlers.ofString() ).body();
            return ( String ) gson.fromJson( body , Map.class ).get( "result" );
        } catch ( IOException | InterruptedException e ) {
            logger.warn( "Exception checking {} job status, URL: {}" , jobName , jobStatusUri.toString() , e );
            throw new RuntimeException( e );
        }
    }

    public CompletableFuture < HttpResponse < Void > > callHookUrl ( String uri ) {
        HttpRequest request = HttpRequest.newBuilder() //
                .uri( URI.create( uri ) ) //
                .timeout( Duration.ofMinutes( 1 ) ) //
                .GET().build();

        logger.info( "Calling hook: {}" , uri );
        return client.sendAsync( request , BodyHandlers.discarding() );
    }

    public String getAuthorization () {
        Encoder encoder = Base64.getEncoder();
        JenkinsConfig jenkinsConfig = config.getJenkins();
        String authorization = encoder.encodeToString( //
                String.join( ":" , jenkinsConfig.getUsername() , jenkinsConfig.getPassword() ).getBytes() //
        );
        return new StringBuilder( "Basic " ) //
                .append( authorization ) //
                .toString();
    }
}