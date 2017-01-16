package com.kc.apollo.task;

import com.kc.apollo.index.RevertIndex;
import com.kc.apollo.spider.SpiderMainRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by lijunying on 15/12/17.
 */
@Component
public class ApolloDailyTask {
    Log logger = LogFactory.getLog(ApolloDailyTask.class);

//    /**
//     * cron表达式：* * * * * *（共6位，使用空格隔开，具体如下）
//     * cron表达式：*(秒0-59) *(分钟0-59) *(小时0-23) *(日期1-31) *(月份1-12或是JAN-DEC) *(星期1-7或是SUN-SAT)
//     */
//
    /**
     * 定时卡点计算。每天凌晨 02:00 执行一次
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void spiderTask() {
        logger.info("每天2点爬虫任务执行... " + new Date());
        new SpiderMainRunner().spiderRunner();
    }

    /**
     * 定时卡点计算。每天凌晨 06:00 执行一次
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void revertIndexTask() {
        logger.info("每天6点索引生成任务执行... " + new Date());
        new RevertIndex().revertIndexRunner();
    }

//    /**
//     * 心跳更新。启动时执行一次，之后每隔1分钟执行一次
//     */
//    @Scheduled(fixedRate = 1000*60*1)
//    public void findIpAndUpdateAddress() {
//        logger.info("间隔执行任务");
//        new RevertIndex().revertIndexRunner();
//
//    }
//
//    @Scheduled(cron = "0/5 * * * * ? ") // 间隔5秒执行
//    public void taskCycle() {
//        System.out.println("使用SpringMVC框架配置定时任务");
//    }

    /**
     * 卡点持久化。启动时执行一次，之后每隔2分钟执行一次
     */
//    @Scheduled(fixedRate = 1000*60*2)
//    public void persistRecord() {
//        System.out.println("卡点持久化... " + new Date());
//    }
}