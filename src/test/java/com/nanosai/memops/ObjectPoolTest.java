package com.nanosai.memops;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectPoolTest {

    @Test
    public void testInstance() {
        IObjectFactory<String> stringFactory = new IObjectFactory<String>() {
            int instanceNo = 0;
            @Override
            public String instance() {
                String value = "" + instanceNo;
                instanceNo++;
                return value;
            }
        };

        ObjectPool<String> objectPool = new ObjectPool<String>(8, stringFactory);
        assertEquals(8, objectPool.capacity());

        String instance0 = objectPool.instance();
        assertEquals("0", instance0);
        assertEquals(1, objectPool.instances());

        String instance1 = objectPool.instance();
        assertEquals("1", instance1);
        assertEquals(2, objectPool.instances());

    }
}
