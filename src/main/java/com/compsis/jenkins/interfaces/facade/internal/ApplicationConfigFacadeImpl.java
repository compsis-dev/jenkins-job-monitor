package com.compsis.jenkins.interfaces.facade.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
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

    private static final String BEAN_NAME = "jenkinsMonitorConfig";
    private static final String APPLICATION_FILE = "application.yml";
    private static final String CHECKSUM_ALGORITHM = "SHA-1";

    @Autowired
    DefaultListableBeanFactory beanFactory;

    @Bean
    public Yaml jenkinsMonitorConfigYaml () {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties( true );
        Constructor constructor = new Constructor( JenkinsMonitorConfig.class );
        return new Yaml( constructor , representer );
    }

    @Override
    public synchronized JenkinsMonitorConfig getConfig () {
        File applicationFile = findApplicationFile();
        if ( ! applicationFile.exists() ) {
            logger.warn( "Unable to find {}" , APPLICATION_FILE );
            return null;
        }

        JenkinsMonitorConfig config;
        if ( beanFactory.isBeanNameInUse( BEAN_NAME ) ) {
            config = beanFactory.getBean( BEAN_NAME , JenkinsMonitorConfig.class );
        } else {
            String yaml = readAsString( applicationFile );
            config = loadYamlAsObject( yaml , digestChecksum( yaml ) );
            beanFactory.registerSingleton( BEAN_NAME , config );
        }

        return config;
    }

    @Override
    public boolean isFileChanged () {
        JenkinsMonitorConfig jenkinsMonitorConfig = getConfig();
        File applicationFile = findApplicationFile();
        String checksum = digestChecksum( readAsString( applicationFile ) );
        return ! jenkinsMonitorConfig.getChecksum().equals( checksum );
    }

    @Override
    public void reload () {
        logger.info( "Reloading jenkinsMonitorConfig bean from {}" , APPLICATION_FILE );
        beanFactory.destroySingleton( BEAN_NAME );
        getConfig();
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

    public File findApplicationFile () {
        URL resource = getClass().getResource( "/" + APPLICATION_FILE );
        File applicationFile = resource == null ? new File( APPLICATION_FILE ) : new File( resource.getFile() );
        if ( ! applicationFile.exists() ) {
            applicationFile = new File( "config" , APPLICATION_FILE );
        }
        return applicationFile;
    }
}