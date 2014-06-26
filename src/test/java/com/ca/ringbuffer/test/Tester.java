package com.ca.ringbuffer.test;

import com.ca.ringbuffer.EventWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by coonrod on 6/26/14.
 */
public class Tester {

    private int ringBufferSize = 4;



    @Before
    public void setUp() throws Exception {

        EventWriter.init(ringBufferSize);

    }

    @Test
    public void straightLineSpeedTest() throws Exception {

        int runs = ringBufferSize * 2 * 2;
        for(int i = 0; i < runs; i++){
            EventWriter.publish();
        }

    }

    @After
    public void tearDown() throws Exception {

        EventWriter.stop();

    }
}
