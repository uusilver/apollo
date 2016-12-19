package com.kc.apollo;

import com.kc.apollo.spider.SpiderMainRunner;
import org.junit.Test;

/**
 * Created by lijunying on 16/12/18.
 */
public class SpiderTest {
    @Test
    public void testSpider(){
        //开启多现场执行
            new SpiderMainRunner().spiderRunner();
        }
}
