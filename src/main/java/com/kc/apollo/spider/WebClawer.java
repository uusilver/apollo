package com.kc.apollo.spider;

import com.kc.apollo.model.SpiderTask;

import java.util.concurrent.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lijunying on 17/1/4.
 */
public class WebClawer {

    public static void main(String args[]) throws IOException{
        SpiderTask basicTask = new SpiderTask();
        String basicUrl = "http://www.aqsiq.gov.cn/";
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




}
