package com.kc.apollo.spider.config;

import com.kc.apollo.model.SpiderSqlBean;
import com.kc.apollo.model.SpiderXmlRoot;
import com.kc.apollo.util.DBHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by lijunying on 16/11/20.\
 * <p/>
 * 读取spider_config.xml的信息，在类的静态构造的时候就构造完成
 * 2016-12-25 更新，采用Sql的方式读取数据库的爬虫配置信息
 * 之后通过静态get的方法获取
 */
public class SpiderConfigBuilder {

    private static Log logger = LogFactory.getLog(SpiderConfigBuilder.class);


    private static SpiderXmlRoot config = null;

    static {
        ClassLoader classLoader = SpiderConfigBuilder.class.getClassLoader();
        File file = new File(classLoader.getResource("spider_config.xml").getFile());

        config = (SpiderXmlRoot) unmarshallerByJaxb(file, SpiderXmlRoot.class);
    }

    private static Object unmarshallerByJaxb(File file, Class clazz) {
        JAXBContext jc = null;
        try {
            jc = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            if (!file.exists()) {
                throw new FileNotFoundException("Can not load xml file!");
            }
            return unmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            logger.error(e.getMessage());
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public static SpiderXmlRoot getConfig() {
        return config;
    }

    public static Stack<SpiderSqlBean> getSpiderSqlConfig() {
        String sql = "select base, prefix, page_rank, depth from apollo_spider_website where active_flag='Y'";
        try {
            Object[][] result = DBHelper.getInstance().queryResultFromDatabase(sql, null, null);
            Stack<SpiderSqlBean> stack = new Stack<>();
            for (Object[] objects : result) {
                SpiderSqlBean spiderSqlBean = new SpiderSqlBean
                        (
                                (String) objects[0],
                                (String) objects[1],
                                (Integer) objects[2],
                                (Integer) objects[3]
                        );
                stack.push(spiderSqlBean);
            }
            return stack;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
