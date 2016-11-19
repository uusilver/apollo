package com.kc.apollo.spider;


import com.kc.apollo.model.POSITION;
import com.kc.apollo.model.SpiderTask;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by lijunying on 16/10/6.
 */
public class HtmlAnalyzer implements Runnable {

    BlockingQueue<SpiderTask> queue;

    private volatile boolean running = true;

    public HtmlAnalyzer(BlockingQueue<SpiderTask> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (running) {

            try {
                SpiderTask task = queue.take();
                System.out.println("执行任务");
                Thread.sleep(new Random().nextInt(10)*100);
                if (task instanceof POSITION) {
                    System.out.println("获得毒丸");
                    System.out.println("退出程序");
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void stop() {
        running = false;
    }
}
