package com.kc.apollo.model;

/**
 * Created by lijunying on 16/11/19.
 */
public class SearchObject {

    private String keywords;
    private int pageNo;

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }
}
