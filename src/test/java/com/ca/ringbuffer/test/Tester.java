package com.ca.ringbuffer.test;

import com.ca.ringbuffer.EventWriter;
import com.ca.ringbuffer.EventWriterWithTranslator;
import com.lmax.disruptor.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by coonrod on 6/26/14.
 */
public class Tester {

    private int ringBufferSize = 512;

    @Before
    public void setUp() throws Exception {

        //EventWriter.init(ringBufferSize);

    }

    @Test
     public void blockingWaitStrategyTest() throws Exception {

        System.out.println("================== START ==================");
        System.out.println("Beginning BlockingWaitStrategy Test:");
        EventWriter.init(ringBufferSize, new BlockingWaitStrategy());

        int runs = ringBufferSize * 2 * 2;
        for(int i = 0; i < runs; i++){
            EventWriter.publish();
        }

        EventWriter.stop();
        System.out.println("Completed BlockingWaitStrategy Test.");
        System.out.println("================== END ==================");

    }

    @Test
    public void busySpinWaitStrategyTest() throws Exception {

        System.out.println("================== START ==================");
        System.out.println("Beginning BusySpinWaitStrategy Test:");
        EventWriter.init(ringBufferSize, new BusySpinWaitStrategy());

        int runs = ringBufferSize * 2 * 2;
        for(int i = 0; i < runs; i++){
            EventWriter.publish();
        }

        EventWriter.stop();
        System.out.println("Completed BusySpinWaitStrategy Test.");
        System.out.println("================== END ==================");

    }

    @Test
    public void sleepingWaitStrategyTest() throws Exception {

        System.out.println("================== START ==================");
        System.out.println("Beginning SleepingWaitStrategy Test:");
        EventWriter.init(ringBufferSize, new SleepingWaitStrategy());

        int runs = ringBufferSize * 2 * 2;
        for(int i = 0; i < runs; i++){
            EventWriter.publish();
        }

        EventWriter.stop();
        System.out.println("Completed SleepingWaitStrategy Test.");
        System.out.println("================== END ==================");

    }

    @Test
    public void yieldingWaitStrategyTest() throws Exception {

        System.out.println("================== START ==================");
        System.out.println("Beginning YieldingWaitStrategy Test:");
        EventWriter.init(ringBufferSize, new YieldingWaitStrategy());

        int runs = ringBufferSize * 2 * 2;
        for(int i = 0; i < runs; i++){
            EventWriter.publish();
        }

        EventWriter.stop();
        System.out.println("Completed YieldingWaitStrategy Test.");
        System.out.println("================== END ==================");

    }

    @Test
    public void phasedBackoffWaitStrategyTest() throws Exception {

        System.out.println("================== START ==================");
        System.out.println("Beginning PhasedBackoffWaitStrategy Test:");
        EventWriter.init(ringBufferSize, new PhasedBackoffWaitStrategy(100l, 100l, TimeUnit.MILLISECONDS, new BusySpinWaitStrategy()));

        int runs = ringBufferSize * 2 * 2;
        for(int i = 0; i < runs; i++){
            EventWriter.publish();
        }

        EventWriter.stop();
        System.out.println("Completed PhasedBackoffWaitStrategy Test.");
        System.out.println("================== END ==================");

    }

    @Test
    public void timeoutBlockingWaitStrategyTest() throws Exception {

        System.out.println("================== START ==================");
        System.out.println("Beginning TimeoutBlockingWaitStrategy Test:");
        EventWriter.init(ringBufferSize, new TimeoutBlockingWaitStrategy(100l, TimeUnit.MILLISECONDS));

        int runs = ringBufferSize * 2 * 2;
        for(int i = 0; i < runs; i++){
            EventWriter.publish();
        }

        EventWriter.stop();
        System.out.println("Completed TimeoutBlockingWaitStrategy Test.");
        System.out.println("================== END ==================");

    }

    @Test
    public void tryPublishEventTest() throws Exception {
        System.out.println("================== START ==================");
        System.out.println("Beginning TryPublishEvent Test:");
        EventWriter.init(ringBufferSize, new YieldingWaitStrategy());

        int runs = ringBufferSize * 2 * 2;
        int errors = 0;

        System.out.println("Runs: " + runs);
        for(int i = 0; i < runs; i++){
            boolean flag = EventWriter.tryPublish();

            if(flag){
                //System.out.println("Writer has caught up to Reader! Potential data loss!");
                errors += 1;
            }

        }

        EventWriter.stop();
        System.out.println("Errors: " + errors);
        System.out.println("PRINT OUT COUNTERLIST");
        System.out.println("Completed TryPublishEvent Test.");
        System.out.println("================== END ==================");
    }

    @Test
    public void writerWithTranslatorTest() throws Exception {
        System.out.println("================== START ==================");
        System.out.println("Beginning TryPublishEvent Test:");
        EventWriterWithTranslator.init(ringBufferSize, new YieldingWaitStrategy());

        int runs = ringBufferSize * 2 * 2;
        int errors = 0;

        System.out.println("Runs: " + runs);
        for(int i = 0; i < runs; i++){
            System.out.println("Run: " + i);
            boolean flag = EventWriterWithTranslator.tryPublish("one", 2, "Three");

            if(flag){
                //System.out.println("Writer has caught up to Reader! Potential data loss!");
                errors += 1;
            }

        }
        Thread.sleep(10000);
        EventWriterWithTranslator.stop();
        System.out.println("Errors: " + errors);
        System.out.println("Completed TryPublishEvent Test.");
        System.out.println("================== END ==================");
    }

    @After
    public void tearDown() throws Exception {

        //EventWriter.stop();

    }
}
