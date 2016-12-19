package com.kc.apollo.cache;

import com.kc.apollo.model.NewsModel;
import com.kc.apollo.util.DicFileGenerator;
import com.kc.apollo.util.NewsGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by lijunying on 16/12/19.
 */
public class NewsCache {

    private static Log logger = LogFactory.getLog(NewsCache.class);

    private static  final int SIZE = 10;

    private static NewsCache INSTANCE = new NewsCache();

    //初始化10条新闻
    private static List<NewsModel> newsCache = new ArrayList<>(10);

    static {
        if(newsCache.size() == 0) {
            newsCache = NewsGenerator.getNewsContent();
            logger.info("新闻缓存初始化完成...");
        }
    }
    private NewsCache(){

    }

    public static  NewsCache getInstance(){
        if(INSTANCE !=null)
            return INSTANCE;
        return new NewsCache();
    }

    public List<NewsModel> getListCache(){
        return newsCache;
    }

    public void updateCache(List<NewsModel> updateCache){
        newsCache = updateCache;
    }
}
