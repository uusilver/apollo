package com.kc.apollo.model;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by lijunying on 16/11/20.
 * @See spider_config.xml
 * spider_config.xml的根节点配置信息
 */
@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.NONE)
public class SpiderXmlRoot {

        @XmlElement(name = "site")
        private List<SpiderXmlBean> beanList;

        public List<SpiderXmlBean> getBeanList() {
            return beanList;
        }

        public void setBeanList(List<SpiderXmlBean> beanList) {
            this.beanList = beanList;
        }


}
