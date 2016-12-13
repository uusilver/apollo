package com.kc.apollo.spider;


import com.kc.apollo.model.SpiderXmlBean;
import com.kc.apollo.spider.config.SpiderConfigBuilder;
import com.kc.apollo.spider.worker.CommonHtmlWorkderBaseOnConfigedXmlFile;
import com.kc.apollo.spider.worker.HtmlWorker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Created by lijunying on 16/10/6.
 */
public class SpiderMainRunner {

    private static Log logger = LogFactory.getLog(SpiderMainRunner.class);

    //开启多现场执行
    public static void main(String args[]) throws Exception {
//        BlockingQueue<SpiderTask> blockingQueue = new SynchronousQueue<SpiderTask>();
//        HtmlAnalyzer analyzer1 = new HtmlAnalyzer(blockingQueue);
//        HtmlProducer producer = new HtmlProducer(blockingQueue);
//
//        ExecutorService service = Executors.newCachedThreadPool();// 建立线程池
//        service.execute(analyzer1);
//        service.execute(producer);
//
//
//        service.shutdown();

        //获取所有的待爬去网站表
        List<SpiderXmlBean> siteQueue =SpiderConfigBuilder.getConfig().getBeanList();
        if(siteQueue == null)
            throw new Exception("爬虫配置文件spider_config.xml配置有错误");

        HtmlWorker worker = new CommonHtmlWorkderBaseOnConfigedXmlFile();
        for(SpiderXmlBean site : siteQueue){
            worker.retreveHyberLinkFromHtml(site.getBase(), site.getPrefix(), Integer.valueOf(site.getDepth()), 0);
        }

        logger.info("爬虫爬取结束");
    }


}
