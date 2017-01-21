package com.kc.apollo.cache;

import com.kc.apollo.builder.TrieBuilder;
import com.kc.apollo.util.DicFileGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 使用Trie树来构建自动完成查询
 * 保存315快查项目内所有的品牌词信息，用于提供自动完成功能以及进行数据检索过程
 * Created by lijunying on 16/12/10.
 */
public class BrandNamesCache {

    private static  Log logger = LogFactory.getLog(BrandNamesCache.class);

    private static  final int SIZE = 5;

    private static BrandNamesCache INSTANCE = new BrandNamesCache();

    private static final List<String> listCache = new ArrayList<>();

    private static final TrieBuilder builder = new TrieBuilder();

    static {
        if(listCache.size() == 0) {
            Set<String> set = DicFileGenerator.getDicContent();
            for (String str : set) {
                listCache.add(str);
                builder.insert(str);
            }
            logger.info("缓存初始化完成...");
        }
    }
    private BrandNamesCache(){

    }

    //返回单例构造的对象
    public static BrandNamesCache getInstance(){
        if(INSTANCE !=null)
            return INSTANCE;
        return new BrandNamesCache();
    }

    //获取查询对象
    public Collection getListCache(String keywords){
        return builder.autoComplete(keywords);
    }


    //爬虫使用
    //检索输入的文章标题中是否包含品牌关键词
    //如果包含返回true,不然就是false
    public boolean containsBrand(String title){
            for(String s : listCache)
                if(title.contains(s))
                    return true;
        return false;
    }
}
