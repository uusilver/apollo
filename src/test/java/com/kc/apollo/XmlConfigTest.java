package com.kc.apollo;

import com.kc.apollo.model.SpiderXmlBean;
import com.kc.apollo.model.SpiderXmlRoot;
import com.kc.apollo.spider.config.SpiderConfigBuilder;

import java.io.File;

/**
 * Created by lijunying on 16/11/20.
 */
public class XmlConfigTest {

    public static void main(String args[]){
        SpiderXmlRoot root = SpiderConfigBuilder.getConfig();
        for(SpiderXmlBean bean : root.getBeanList()){
            System.out.println(bean.getBody_tag());
        }

    }
}
