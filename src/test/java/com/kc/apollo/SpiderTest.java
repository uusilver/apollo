package com.kc.apollo;

import com.kc.apollo.spider.SpiderMainRunner;
import com.kc.apollo.spider.SpiderMultiThreadRunner;
import org.junit.Test;

/**
 * Created by lijunying on 16/12/18.
 */
public class SpiderTest {
    @Test
    public void testSpider(){
            //单线程爬虫
//            new SpiderMainRunner().spiderRunner();
            //多线程爬虫
            new SpiderMultiThreadRunner().spiderRunner();
        }
}
