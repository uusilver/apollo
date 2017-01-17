package com.kc.apollo.model;

/**
 * Created by lijunying on 16/10/6.
 */
public class SpiderTask {
    private String url;
    private String host;

    public SpiderTask(){}


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
