package com.kc.apollo.spider.config;

import com.kc.apollo.model.SpiderXmlRoot;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by lijunying on 16/11/20.\
 *
 * 读取spider_config.xml的信息，在类的静态构造的时候就构造完成
 * 之后通过静态get的方法获取
 */
public class SpiderConfigBuilder {

    private static Log logger = LogFactory.getLog(SpiderConfigBuilder.class);


    private static SpiderXmlRoot config = null;
    static {
        ClassLoader classLoader = SpiderConfigBuilder.class.getClassLoader();
        File file = new File(classLoader.getResource("spider_config.xml").getFile());

        config = (SpiderXmlRoot)unmarshallerByJaxb(file, SpiderXmlRoot.class);
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

}
