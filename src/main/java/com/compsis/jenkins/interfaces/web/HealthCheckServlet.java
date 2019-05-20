package com.compsis.jenkins.interfaces.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class HealthCheckServlet extends AbstractHandler {
    private static final Logger logger = LoggerFactory.getLogger( HealthCheckServlet.class );

    @Override
    public void handle ( String target , Request baseRequest , HttpServletRequest request , HttpServletResponse response ) throws IOException , ServletException {
        response.setContentType( "text/plain" );
        response.setStatus( HttpServletResponse.SC_OK );
        baseRequest.setHandled( true );
        response.getWriter().println( "up" );
        logger.debug( "Server up" );
    }
}