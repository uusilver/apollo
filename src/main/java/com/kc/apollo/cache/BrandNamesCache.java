package com.kc.apollo.cache;

import com.kc.apollo.util.DicFileGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 保存315快查项目内所有的品牌词信息，用于提供自动完成功能以及进行数据检索过程
 * Created by lijunying on 16/12/10.
 */
public class BrandNamesCache {

    private static  Log logger = LogFactory.getLog(BrandNamesCache.class);

    private static  final int SIZE = 10;

    private static BrandNamesCache INSTANCE = new BrandNamesCache();

    private static final List<String> listCache = new ArrayList<>();

    static {
        if(listCache.size() == 0) {
            Set<String> set = DicFileGenerator.getDicContent();
            for (String str : set) {
                listCache.add(str);
            }
            logger.info("缓存初始化完成...");
        }
    }
    private BrandNamesCache(){

    }

    public static BrandNamesCache getInstance(){
        if(INSTANCE !=null)
            return INSTANCE;
        return new BrandNamesCache();
    }

    public List<String> getListCache(String keywords){
        List<String> result = new ArrayList<>();
        stop:for(String s : listCache){
            if(s.contains(keywords)){
                result.add(s);
                if (result.size() == SIZE)
                    break stop;
            }
        }
        return result;
    }

    //检索输入的文章标题中是否包含品牌关键词
    //如果包含返回true,不然就是false
    public boolean containsBrand(String title){
            for(String s : listCache)
                if(title.contains(s))
                    return true;
        return false;
    }
}
