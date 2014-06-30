package com.ca.ringbuffer;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslatorVararg;
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
 * Created by coonrod on 6/30/14.
 */
public class EventWriterWithTranslator {

    private static RingBuffer<Event> ringBuffer;
    private static Disruptor<Event> disruptor;
    private static EventHandler<Event> handler;

    private static Long counter;

    public static void init(int ringBufferSize, WaitStrategy waitStrategy){
        counter = 0l;
        ExecutorService exec = Executors.newCachedThreadPool();
        disruptor = new Disruptor<Event>(Event.EVENT_FACTORY, ringBufferSize, exec, ProducerType.SINGLE, waitStrategy);

        handler = new EventReader(ringBufferSize);
        disruptor.handleEventsWith(handler);
        ringBuffer = disruptor.start();
    }

    private static final EventTranslatorVararg<Event> EVENT_TRANSLATOR =
            new EventTranslatorVararg<Event>() {
                @Override
                public void translateTo(Event event, long sequence, Object... args) {
                    System.out.println("Writing to slot:\t" + sequence + "\tcounter:\t" + counter);
                    String payload = null;
                    for (int i = 0; i < args.length; i++) {
                        payload += args[i].toString();
                    }
                    //System.out.println("Write Payload: " + payload);
                    event.setPayload(payload);
                    event.setCounter(counter);
                    event.setUuid(UUID.randomUUID());
                    event.setTimestamp(new Timestamp((new Date()).getTime()));
                    counter += 1;
                }
    };

    public static boolean tryPublish(Object... args){
        return ringBuffer.tryPublishEvent(EVENT_TRANSLATOR, args);
    }

    public static void stop(){
        disruptor.shutdown();
    }
}
