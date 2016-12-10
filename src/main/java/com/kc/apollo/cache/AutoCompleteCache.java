package com.kc.apollo.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijunying on 16/12/10.
 */
public class AutoCompleteCache {

    private static  Log logger = LogFactory.getLog(AutoCompleteCache.class);

    private static  final int SIZE = 5;


    private static final List<String> listCache = new ArrayList<>();

    static {
        //初始化搜索表
        listCache.add("中国乔丹");
        listCache.add("中国李宁");
        listCache.add("中国特步");
        listCache.add("李宁");
        listCache.add("耐克");
        listCache.add("阿迪达斯");
        listCache.add("361运动");
        listCache.add("特步");
        listCache.add("美津浓");
        listCache.add("美特斯邦威");
        listCache.add("玫琳凯");
        listCache.add("美国耐克乔丹");
        listCache.add("美丽肌肤面膜");
        listCache.add("美国胶囊");
        listCache.add("美丽传说");
        listCache.add("亚瑟士");
        logger.info("缓存初始化完成...");
    }

    public static List<String> getListCache(String keywords){
        List<String> result = new ArrayList<>();
        stop:for(String s : listCache){
            if(s.startsWith(keywords)){
                result.add(s);
                if (result.size() == SIZE)
                    break stop;
            }
        }
        return result;
    }
}
