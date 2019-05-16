package com.compsis.jenkins.interfaces.facade.dto;

import java.util.List;

public class JenkinsMonitorConfig {

    private ServerConfig server;

    private JenkinsConfig jenkins;

    private String checksum;

    public ServerConfig getServer () {
        return server;
    }

    public void setServer ( ServerConfig server ) {
        this.server = server;
    }

    public JenkinsConfig getJenkins () {
        return jenkins;
    }

    public void setJenkins ( JenkinsConfig jenkins ) {
        this.jenkins = jenkins;
    }

    public String getChecksum () {
        return checksum;
    }

    public void setChecksum ( String checksum ) {
        this.checksum = checksum;
    }

    public static class ServerConfig {
        private int port;

        public int getPort () {
            return port;
        }

        public void setPort ( int port ) {
            this.port = port;
        }
    }

    public static class JenkinsConfig {
        private String url;
        private String username;
        private String password;
        private List < String > jobs;
        private List < JenkinsHook > hooks;

        public String getUrl () {
            return url;
        }

        public void setUrl ( String url ) {
            this.url = url;
        }

        public String getUsername () {
            return username;
        }

        public void setUsername ( String username ) {
            this.username = username;
        }

        public String getPassword () {
            return password;
        }

        public void setPassword ( String password ) {
            this.password = password;
        }

        public List < String > getJobs () {
            return jobs;
        }

        public void setJobs ( List < String > jobs ) {
            this.jobs = jobs;
        }

        public List < JenkinsHook > getHooks () {
            return hooks;
        }

        public void setHooks ( List < JenkinsHook > hooks ) {
            this.hooks = hooks;
        }
    }
}