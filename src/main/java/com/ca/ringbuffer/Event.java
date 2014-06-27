package com.ca.ringbuffer;


import com.lmax.disruptor.EventFactory;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by coonrod on 6/26/14.
 */
public class Event {

    private UUID uuid;
    private Long counter;
    private Timestamp timestamp;

    public Long getCounter() {
        return counter;
    }

    public void setCounter(Long counter) {
        this.counter = counter;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Event(){

    }

    public final static EventFactory<Event> EVENT_FACTORY = new EventFactory<Event>() {
        @Override
        public Event newInstance() {
            return new Event();
        }
    };

//    public final static EventTranslator<Event> EVENT_TRANSLATOR = new EventTranslator<Event>() {
//        @Override
//        public void translateTo(Event event, long sequence) {
//
//        }
//    };

}
