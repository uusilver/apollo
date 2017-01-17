package com.kc.apollo.spider;

import com.kc.apollo.model.POSITION;
import com.kc.apollo.model.SpiderTask;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * Created by lijunying on 16/10/6.
 */
public class HtmlProducer implements Runnable {

    BlockingQueue<SpiderTask> queue;

    private volatile boolean running = true;

    public HtmlProducer(BlockingQueue<SpiderTask> queue) {
        this.queue = queue;
    }


    @Override
    public void run() {
        try {
            int index = 1;
            while (index < 10) {
                System.out.println("生产任务");
                queue.put(new SpiderTask());
                Thread.sleep(new Random().nextInt(10)*100);
                index++;

            }
            System.out.println("放入毒丸");
            queue.put(new POSITION());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void stop() {
        running = false;
    }
}
