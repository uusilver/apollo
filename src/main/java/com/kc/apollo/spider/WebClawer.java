package com.kc.apollo.spider;

import com.kc.apollo.model.SpiderSqlBean;
import com.kc.apollo.model.SpiderTask;

import java.util.Stack;
import java.util.concurrent.*;

import com.kc.apollo.util.DBHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Web爬虫程序，爬取相关新闻信息
 * Created by lijunying on 17/1/4.
 */
public class WebClawer {

    private  static  Log logger = LogFactory.getLog(WebClawer.class);

    public static void main(String args[]) throws IOException{

        String sql = "select url from apollo_spider_website where active_flag='Y'";
        try {
            Object[][] result = DBHelper.getInstance().queryResultFromDatabase(sql, null, null);
            for (Object[] objects : result) {

                String basicUrl =(String) objects[0];
                logger.info("开始爬取:"+basicUrl);
                SpiderTask basicTask = new SpiderTask();
                Pattern p = Pattern.compile("[^//]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = p.matcher(basicUrl);
                matcher.find();
                String host = matcher.group();
                basicTask.setUrl(basicUrl);
                basicTask.setHost(host);

                BlockingQueue<SpiderTask> blockingQueue = new LinkedBlockingDeque<>(100);
                blockingQueue.add(basicTask);

                ConcurrentHashMap<String, String> concurrentHashMap = new ConcurrentHashMap<>();

                ExecutorService executorService = Executors.newFixedThreadPool(2);
                HtmlAnalyzer analyzer1 = new HtmlAnalyzer(blockingQueue, concurrentHashMap);
                HtmlAnalyzer analyzer2 = new HtmlAnalyzer(blockingQueue, concurrentHashMap);
                executorService.execute(analyzer1);
                executorService.execute(analyzer2);

                executorService.shutdown();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }




}
