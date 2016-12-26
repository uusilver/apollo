package com.kc.apollo.model;

/**
 * Created by lijunying on 16/12/25.
 */
public class SpiderSqlBean {

    private String base;
    private String prefix;
    private Integer pageRank;
    private Integer depth;

    public SpiderSqlBean(String base, String prefix, Integer pageRank, Integer depth) {
        this.base = base;
        this.prefix = prefix;
        this.pageRank = pageRank;
        this.depth = depth;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Integer getPageRank() {
        return pageRank;
    }

    public void setPageRank(Integer pageRank) {
        this.pageRank = pageRank;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    @Override
    public String toString() {
        return "SpiderSqlBean{" +
                "base='" + base + '\'' +
                ", prefix='" + prefix + '\'' +
                ", pageRank=" + pageRank +
                ", depth=" + depth +
                '}';
    }
}
