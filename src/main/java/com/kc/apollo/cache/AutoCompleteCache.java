package com.kc.apollo.cache;

import com.kc.apollo.util.DicFileGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by lijunying on 16/12/10.
 */
public class AutoCompleteCache {

    private static  Log logger = LogFactory.getLog(AutoCompleteCache.class);

    private static  final int SIZE = 10;

    private static AutoCompleteCache INSTANCE = new AutoCompleteCache();

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
    private AutoCompleteCache(){

    }

    public static  AutoCompleteCache getInstance(){
        if(INSTANCE !=null)
            return INSTANCE;
        return new AutoCompleteCache();
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
}
