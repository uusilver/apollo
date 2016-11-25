package com.kc.apollo.spider.fixer;

import com.kc.apollo.model.SearchResult;
import com.kc.apollo.util.DataHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;

/**
 * Created by lijunying on 16/11/25.
 */
public class BaiduRealTimeWorker {

    public SearchResult baiduWorker(String keyWords) throws IOException {
        long start = System.currentTimeMillis();
        SearchResult searchResult = new SearchResult();
        //TODO 配置到properties内
        String url = "http://www.baidu.com/s?wd="+keyWords+"质量问题曝光";
        Document document = Jsoup.connect(url).get();
        Element rootElement = document.body().select("#content_left").get(0);
        Elements elements = rootElement.select(".result.c-container"); //多个css间不要有空格

        HashSet<SearchResult.SearchItem> set = new HashSet<SearchResult.SearchItem>();
        for(Element e : elements){
            SearchResult.SearchItem item = new SearchResult.SearchItem();
            String title = e.select(".t").get(0).text();
            String link = e.select(".t").get(0).select("a").attr("href");
            String content = e.select(".c-abstract").get(0).text();
            item.setTitle(title);
            item.setUrl(link);
            item.setBody_content(content);
            item.setCreate_date(DataHelper.getCurrentTimeStamp().toString());
            item.setSource("百度");
            set.add(item);
        }
        searchResult.setSearchItemSet(set);
        searchResult.setTotalResult(10);
        long end = System.currentTimeMillis();
        long timeCost = end-start;
        searchResult.setExecuteTime(timeCost);
        return searchResult;
    }



}
