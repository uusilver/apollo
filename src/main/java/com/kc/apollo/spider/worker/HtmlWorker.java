package com.kc.apollo.spider.worker;

import java.io.IOException;

/**
 * Created by lijunying on 16/10/17.
 */
public interface HtmlWorker {

    /**
     * 从Html中抽取href标签
     * @param baseHtmlAddress
     */
    public void retreveHyberLinkFromHtml(String baseHtmlAddress) throws Exception;

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
