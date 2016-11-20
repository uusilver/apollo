package com.kc.apollo.model;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by lijunying on 16/11/20.
 *
 * @See spider_config.xml
 * spider_config.xml的子节点配置信息
 */
@XmlAccessorType(XmlAccessType.NONE)
public class SpiderXmlBean {

    private String base;
    private String prefix;
    private String links_tag;
    private String links;
    private String title_tag;
    private String title;
    private String body_tag;
    private String body;

    @XmlElement
    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    @XmlElement
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @XmlElement
    public String getTitle_tag() {
        return title_tag;
    }

    public void setTitle_tag(String title_tag) {
        this.title_tag = title_tag;
    }

    @XmlElement
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement
    public String getBody_tag() {
        return body_tag;
    }

    public void setBody_tag(String body_tag) {
        this.body_tag = body_tag;
    }

    @XmlElement
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @XmlElement
    public String getLinks_tag() {
        return links_tag;
    }

    public void setLinks_tag(String links_tag) {
        this.links_tag = links_tag;
    }

    @XmlElement
    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }
}
