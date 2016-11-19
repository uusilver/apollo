package com.kc.apollo.spider.worker;

/**
 * Created by lijunying on 16/10/17.
 */
public abstract class HtmlParentWorker implements HtmlWorker{

    public boolean checkUrl(String url) throws NullPointerException {
        if(url==null) throw new NullPointerException("HTML路径不可为空");
        return false;
    }
}
