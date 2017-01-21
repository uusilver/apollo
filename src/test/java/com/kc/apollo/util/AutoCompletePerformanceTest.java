package com.kc.apollo.util;

import com.kc.apollo.cache.BrandNamesCache;
import org.junit.Test;

/**
 * Created by lijunying on 17/1/21.
 */

public class AutoCompletePerformanceTest {

    @Test
    public void test1(){
        long start = System.currentTimeMillis();
        for(int i=0; i<=1000000; i++){
            BrandNamesCache.getInstance().getListCacheByStringStarts("中");
        }
        long end = System.currentTimeMillis();
        System.out.println("String.startWith cost:"+(end-start)+" ms");
    }

    @Test
    public void test2(){
        long start = System.currentTimeMillis();
        for(int i=0; i<=1000000; i++){
            BrandNamesCache.getInstance().matchStringByPrefix("中");
        }
        long end = System.currentTimeMillis();
        System.out.println("trie tree cost:"+(end-start)+" ms");
    }
}
