package com.kc.apollo.model;

/**
 * Created by lijunying on 16/12/19.
 */
public class NewsModel {

    private String keywords;
    private String url;
    private String bodyContent;
    private String createDate;

    public NewsModel(String keywords, String url, String bodyContent, String createDate) {
        this.keywords = keywords;
        this.url = url;
        this.bodyContent = bodyContent;
        this.createDate = createDate;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBodyContent() {
        return bodyContent;
    }

    public void setBodyContent(String bodyContent) {
        this.bodyContent = bodyContent;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
