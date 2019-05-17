package com.compsis.jenkins.interfaces.facade.internal;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
@Scope ( "singleton" )
public class JsonService implements InitializingBean {

    private Gson gson;

    public < T > T fromJson ( InputStream stream , Class < T > classOfT ) {
        return gson.fromJson( new InputStreamReader( stream ) , classOfT );
    }

    @Override
    public void afterPropertiesSet () {
        gson = new GsonBuilder().create();
    }
}