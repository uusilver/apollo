package com.kc.apollo.model;
/**
 * @COPYRIGHT (C) 2016 Schenker AG
 * <p>
 * All rights reserved
 */


/**
 * 用于建立Lucene索引模型
 *
 * @author Vani Li
 */
public class LuceneIndexModel {

    public static final String UUID = "uuid";
    public static final String TEXT = "text";


    public LuceneIndexModel(String text, String uuid) {
        this.text = text;
        this.uuid = uuid;
    }

    private String text;
    private String uuid;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isNotNull(){
        return uuid!=null && uuid.length()>0 && text!=null && text.length()>0;
    }
}
