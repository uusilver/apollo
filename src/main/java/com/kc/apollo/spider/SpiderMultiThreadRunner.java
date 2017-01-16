package com.kc.apollo.spider;


import com.kc.apollo.model.SpiderSqlBean;
import com.kc.apollo.model.SpiderXmlBean;
import com.kc.apollo.spider.config.SpiderConfigBuilder;
import com.kc.apollo.spider.worker.CommonHtmlWorkderBaseOnConfigedXmlFile;
import com.kc.apollo.spider.worker.HtmlCallableWorker;
import com.kc.apollo.spider.worker.HtmlWorker;
import com.kc.apollo.types.DBTypes;
import com.kc.apollo.util.DBHelper;
import com.kc.apollo.util.DataHelper;
import com.kc.apollo.util.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.*;

/**
 * Created by lijunying on 16/10/6.
 */
public class SpiderMultiThreadRunner {

    private Log logger = LogFactory.getLog(SpiderMainRunner.class);

    public void spiderRunner() {
        try {
            //创建标志文件
            FileUtils.createFile("Spider.run");
            //获取所有的待爬去网站表
            Stack<SpiderSqlBean> stack = SpiderConfigBuilder.getSpiderSqlConfig();
            final int stackSize = stack.size();
            if (stack == null)
                throw new Exception("爬虫配置文件读取有错误");

            int cpu = Runtime.getRuntime().availableProcessors();
            int threadNum = cpu+1; //IO密集，CPU数＋1
            int loopTimes = stack.size()/threadNum + 1;
            int checkNum = 0;
            for(int i=0;i<loopTimes;i++){
                ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(threadNum);
                List<HtmlCallableWorker> tasklist=new ArrayList<>();
                List<Future<Boolean>> resultlist;
                for(int j =0; j<5;j++){
                    if(!stack.isEmpty()) {
                        SpiderSqlBean sq = stack.pop();
                        tasklist.add(new HtmlCallableWorker(sq));
                        checkNum++;
                    }
                    resultlist = executor.invokeAll(tasklist);
                    for(Future<Boolean> f:resultlist){
                        try {
                            //              while(!f.isDone()){};
                            if(!f.get()){
                                logger.error("爬虫子任务任务执行出错!");
                            }
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            // TODO Auto-generated catch block
                            logger.error(e.getMessage());
                        }
                    }
                }//end of for
                executor.shutdown();
            }
            if(checkNum != stackSize){
                logger.warn("网站爬取个数出错!");
            }
            logger.info("爬虫爬取结束");
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            FileUtils.delectFile("Spider.run");
        }
    }


}
