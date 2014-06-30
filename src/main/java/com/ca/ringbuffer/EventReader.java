package com.ca.ringbuffer;

import com.lmax.disruptor.EventHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by coonrod on 6/26/14.
 */
public class EventReader implements EventHandler<Event> {

    private long divisor;
    private boolean first = true;
    private String oString = null;
    private List<Long> counters = new ArrayList<Long>();

    public void listErrors(){
        System.out.println("Listing Counters where overflow detected:");
        for(Long error : counters){
            System.out.println(error + ",");
        }
        System.out.println("Error Print Complete.");
    }

    private static final ThreadLocal<Long> counter = new ThreadLocal<Long>(){
        @Override
        protected Long initialValue(){
            return 0l;
        }
    };

    public EventReader(int ringBufferSize){
        this.divisor = ringBufferSize;
    }

    /**
     * Called when a publisher has published an event to the {@link com.lmax.disruptor.RingBuffer}
     *
     * @param event      published to the {@link com.lmax.disruptor.RingBuffer}
     * @param sequence   of the event being processed
     * @param endOfBatch flag to indicate if this is the last event in a batch from the {@link com.lmax.disruptor.RingBuffer}
     * @throws Exception if the EventHandler would like the exception handled further up the chain.
     */
    @Override
    public void onEvent(Event event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println("Reading slot:\t" + sequence + "\tcounter:\t" + counter.get() + "\tpayload:\t" + event.getCounter());
        if(first){
            Thread.sleep(5000);
            first = false;
        }

        if (simpleOverflowCheck(event.getCounter(), counter.get(), sequence)){
            counters.add(event.getCounter());
        }
        counter.set(event.getCounter());

    }

    private void overflowCheck(Long newCounter) {
        System.out.println("Previous Counter: " + counter.get());
        System.out.println("Current Counter: " + newCounter);
        System.out.println("Divisior: " + divisor);

        long remainder = newCounter % divisor;
        long quotient = newCounter / divisor;

        //Consumer has completeed one circuit of the ring buffer
        if((remainder == 0l) && (quotient == 1) && (newCounter >= divisor)){
            //Increment the divisor by a factor of 2
            divisor = divisor * 2;
        }

        System.out.println("Remainder: " + remainder);
        System.out.println("Quotient: " + quotient);

        if(newCounter > (counter.get() + 1l)){
            System.out.println("EventWriter has overwritten EventReader's position on the RingBuffer!");
            System.out.println("Laps ahead: " + quotient);

        } else if (newCounter == (counter.get() + 1l)){
            System.out.println("EventReader is keeping pace with EventWriter.");
        } else if (newCounter.compareTo(counter.get()) == 0) {
            System.out.println("First run.");
        } else {
            System.out.println("I have no idea how we got here!");
        }

    }

    private boolean simpleOverflowCheck(Long newCounter, Long prevCounter, long sequence){

        boolean overflow = (newCounter > (prevCounter + 1));

        if(overflow){
            oString = "Reading from RingBuffer slot: " + sequence + "\n";
            oString += "Previous counter value: " + prevCounter + "\n";
            oString += "New counter value: " + newCounter + "\n";
            System.out.println("OVERFLOW:\n" + oString);
        } else {
            oString = null;
        }

        return overflow;

    }
}
