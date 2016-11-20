package com.kc.apollo.spider.worker;

import com.kc.apollo.model.SpiderXmlBean;

import java.io.IOException;

/**
 * Created by lijunying on 16/10/17.
 */
public interface HtmlWorker {

    /**
     * 从Html中抽取href标签
     * @See #com.kc.apollo.model.SpiderXmlBean
     * @param site
     */
    public void retreveHyberLinkFromHtml(SpiderXmlBean site) throws Exception;

    /**检查输入url的合法性
     * 当为null时抛出{@link NullPointerException}时,
     * 并采用正则表达式进行验证
     * @param url
     * @throws Exception
     */
    public boolean checkUrl(String url) throws NullPointerException;

    /**
     *
     * @param fileUrlAddress
     */
    public void downloadRemoteFileAndPersist(String fileUrlAddress) throws Exception;
}
