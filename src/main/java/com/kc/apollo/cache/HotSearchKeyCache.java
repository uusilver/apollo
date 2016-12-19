package com.kc.apollo.cache;

import com.kc.apollo.model.NewsModel;
import com.kc.apollo.util.HotSearchKeyGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijunying on 16/12/19.
 */
public class HotSearchKeyCache {

    private static Log logger = LogFactory.getLog(HotSearchKeyCache.class);

    private static  final int SIZE = 10;

    private static HotSearchKeyCache INSTANCE = new HotSearchKeyCache();

    //初始化10条新闻
    private static List<String> hotSearchKeyCache = new ArrayList<>(10);

    static {
        if(hotSearchKeyCache.size() == 0) {
            hotSearchKeyCache = HotSearchKeyGenerator.getHotSearchKey();
            logger.info("新闻缓存初始化完成...");
        }
    }
    private HotSearchKeyCache(){

    }

    public static  HotSearchKeyCache getInstance(){
        if(INSTANCE !=null)
            return INSTANCE;
        return new HotSearchKeyCache();
    }

    public List<String> getListCache(){
        return hotSearchKeyCache;
    }

    public void updateCache(List<String> updateCache){
        hotSearchKeyCache = updateCache;
    }
}
