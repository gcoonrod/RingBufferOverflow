package com.ca.ringbuffer;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by coonrod on 6/26/14.
 */
public class EventWriter {

    static RingBuffer<Event> ringBuffer = null;
    static Disruptor<Event> disruptor = null;

    private static Long counter;

    public static void init(int ringBufferSize, WaitStrategy waitStrategy){
        counter = 0l;
        ExecutorService exec = Executors.newCachedThreadPool();
        disruptor = new Disruptor<Event>(Event.EVENT_FACTORY, ringBufferSize, exec, ProducerType.SINGLE, waitStrategy);

        EventHandler<Event> handler = new EventReader(ringBufferSize);
        disruptor.handleEventsWith(handler);
        ringBuffer = disruptor.start();
    }

    public static void publish(){
        long seq = ringBuffer.next();
        //System.out.println("Writing to RingBuffer slot:  " + seq);
        Event e = ringBuffer.get(seq);
        e.setCounter(counter);
        counter += 1l;
        e.setUuid(UUID.randomUUID());
        e.setTimestamp(new Timestamp((new Date()).getTime()));
        ringBuffer.publish(seq);
    }

    public static boolean tryPublish(){
        EventTranslator<Event> et = new EventTranslator<Event>() {
            @Override
            public void translateTo(Event event, long sequence) {
                event.setCounter(counter);
                event.setUuid(UUID.randomUUID());
                event.setTimestamp(new Timestamp((new Date()).getTime()));
                counter += 1;
            }
        };

        return ringBuffer.tryPublishEvent(et);

    }

    public static void stop(){
        disruptor.shutdown();
        disruptor = null;
    }
}
