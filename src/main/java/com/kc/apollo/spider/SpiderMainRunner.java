package com.kc.apollo.spider;


import com.kc.apollo.model.SpiderXmlBean;
import com.kc.apollo.spider.config.SpiderConfigBuilder;
import com.kc.apollo.spider.worker.CommonHtmlWorkderBaseOnConfigedXmlFile;
import com.kc.apollo.spider.worker.HtmlWorker;
import com.kc.apollo.util.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Created by lijunying on 16/10/6.
 */
public class SpiderMainRunner {

    private Log logger = LogFactory.getLog(SpiderMainRunner.class);

    public void spiderRunner() {
        try {
            //创建标志文件
            FileUtils.createFile("Spider.run");
            //获取所有的待爬去网站表
            List<SpiderXmlBean> siteQueue = SpiderConfigBuilder.getConfig().getBeanList();
            if (siteQueue == null)
                throw new Exception("爬虫配置文件spider_config.xml配置有错误");

            HtmlWorker worker = new CommonHtmlWorkderBaseOnConfigedXmlFile();
            for (SpiderXmlBean site : siteQueue) {
                worker.retreveHyberLinkFromHtml(site.getBase(), site.getPrefix(), Integer.valueOf(site.getDepth()), 0);
            }

            logger.info("爬虫爬取结束");
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            FileUtils.delectFile("Spider.run");
        }
    }


}
