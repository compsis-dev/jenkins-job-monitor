package com.compsis.jenkins.interfaces.facade.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import com.compsis.jenkins.interfaces.facade.ApplicationConfigFacade;
import com.compsis.jenkins.interfaces.facade.dto.JenkinsMonitorConfig;

@Service
public class ApplicationConfigFacadeImpl implements ApplicationConfigFacade {
    private static final Logger logger = LoggerFactory.getLogger( ApplicationConfigFacadeImpl.class );

    private static final String APPLICATION_FILE = "application.yml";
    private static final String CHECKSUM_ALGORITHM = "SHA-1";

    private JenkinsMonitorConfig config;

    @Bean
    public Yaml jenkinsMonitorConfigYaml () {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties( true );
        Constructor constructor = new Constructor( JenkinsMonitorConfig.class );
        return new Yaml( constructor , representer );
    }

    @Override
    public JenkinsMonitorConfig getConfig () {
        if ( config != null ) {
            return config;
        }
        config = loadConfig();
        return config;
    }

    private JenkinsMonitorConfig loadConfig () {
        try {
            File applicationFile = findApplicationFile();
            String yaml = readAsString( applicationFile );
            JenkinsMonitorConfig config = loadYamlAsObject( yaml , digestChecksum( yaml ) );
            logger.info( "Application config loaded (checksum: {})" , config.getChecksum() );
            return config;
        } catch ( FileNotFoundException e ) {
            throw new RuntimeException( "Unable to load application config" , e );
        }
    }

    @Override
    public boolean hasChanged () {
        if ( config == null ) {
            return false;
        }

        try {
            File applicationFile = findApplicationFile();
            String checksum = digestChecksum( readAsString( applicationFile ) );
            return ! getConfig().getChecksum().equals( checksum );
        } catch ( FileNotFoundException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void reload () {
        logger.info( "Reloading applcation config" );
        config = loadConfig();
    }

    public JenkinsMonitorConfig loadYamlAsObject ( String yaml , String checksum ) {
        JenkinsMonitorConfig jenkinsMonitorConfig = jenkinsMonitorConfigYaml().load( yaml );
        jenkinsMonitorConfig.setChecksum( checksum );
        logger.debug( "Checksum: {} \n{}" , checksum , yaml );
        return jenkinsMonitorConfig;
    }

    public String digestChecksum ( String data ) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance( CHECKSUM_ALGORITHM );
            messageDigest.update( data.getBytes() );
            byte[] digest = messageDigest.digest();
            StringBuffer buffer = new StringBuffer();
            for ( byte b : digest ) {
                buffer.append( String.format( "%02x" , b & 0xff ) );
            }
            return buffer.toString();
        } catch ( NoSuchAlgorithmException e ) {
            logger.warn( "Unable to find {} algorithm" , CHECKSUM_ALGORITHM );
            throw new RuntimeException( "Unable find algorithm" , e );
        }
    }

    public String readAsString ( File applicationFile ) {
        try ( InputStream inputStream = new FileInputStream( applicationFile ) ; //
                Scanner scanner = new Scanner( inputStream ) ) {
            scanner.useDelimiter( "\\A" );
            return scanner.next();
        } catch ( IOException e ) {
            logger.warn( "Unable to read file: {}" , applicationFile.getAbsolutePath() );
            throw new RuntimeException( "Unable to read application.yml" , e );
        }
    }

    private File findApplicationFile () throws FileNotFoundException {
        List < File > paths = getPaths();
        return paths.stream().filter( file -> file.exists() ).findFirst() //
                .orElseThrow( () -> {
                    logger.warn( "Application file not found at: {}" , paths.stream() //
                            .map( file -> String.valueOf( file.getAbsolutePath() ) ) //
                            .collect( Collectors.joining( ", " ) ) );

                    return new FileNotFoundException( "Unable to find " + APPLICATION_FILE );
                } );
    }

    public List < File > getPaths () {
        URL resource = getClass().getResource( "/" + APPLICATION_FILE );
        return Arrays.asList( Optional.ofNullable( resource ) //
                .map( r -> new File( resource.getFile() ) ) //
                .orElse( new File( APPLICATION_FILE ) ) , //
                new File( "config" , APPLICATION_FILE ) );
    }
}