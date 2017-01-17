package com.kc.apollo.spider;


import com.kc.apollo.cache.BrandNamesCache;
import com.kc.apollo.model.POSITION;
import com.kc.apollo.model.SpiderTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lijunying on 16/10/6.
 */
public class HtmlAnalyzer implements Runnable {

    BlockingQueue<SpiderTask> queue;

    ConcurrentHashMap<String, String> map;

    public HtmlAnalyzer(BlockingQueue<SpiderTask> queue, ConcurrentHashMap<String, String> map) {
        this.queue = queue;
        this.map = map;
    }

    @Override
    public void run() {
            try {
                SpiderTask task = queue.poll(100, TimeUnit.MILLISECONDS);
                if(task!=null) {
                    String url = task.getUrl();
                    //放入完成列表内
                    map.put(task.getUrl(), "s");
                    //analysis URL
                    Document doc = Jsoup.connect(url).get();
                    Elements links = doc.select("a[href]");
                    for (Element link : links) {
                        String absLink = link.attr("abs:href");
                        if (absLink.length() > 0 && absLink.contains(task.getHost()) && !map.containsKey(absLink)) {
                            dealWithValidUrl(absLink);
                        }
                        //添加新任务
                        SpiderTask cTask = new SpiderTask();
                        cTask.setHost(task.getHost());
                        cTask.setUrl(absLink);
                        queue.offer(cTask, 100, TimeUnit.MILLISECONDS);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


    }

    //处理每一个具体的链接的
    private void dealWithValidUrl(String url){
        //合法的html或者htm结尾的URL
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
            //提取title
            Elements elements = document.getElementsByTag("title");
            if(elements!=null && elements.size()>0){
                String title = elements.get(0).text();
                //提取全部的href链接
                Element body = document.body();
                //链接不为空，且标题内含有品牌词信息
                if(title !=null && BrandNamesCache.getInstance().containsBrand(title))
                    //TODO 保存进数据库
                    print(" * a: %s | <%s>  (%s)", url, title, trim(body.text(), 30));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }


}
