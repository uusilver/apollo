package com.kc.apollo.spider;


import com.kc.apollo.model.SpiderTask;
import com.kc.apollo.spider.worker.ChinaQualityWebSiteWorker;
import com.kc.apollo.spider.worker.HtmlWorker;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Created by lijunying on 16/10/6.
 */
public class SpiderMainRunner {

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

        HtmlWorker worker = new ChinaQualityWebSiteWorker();
        worker.retreveHyberLinkFromHtml(null);

    }


}
