package com.nanosai.memops;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

        objectPool.free(instance0);  //instance0 put on internal stack
        objectPool.free(instance1);  //instance1 put on internal stack

        String instance1_2 = objectPool.instance();  //get instance1 again
        String instance0_2 = objectPool.instance();  //get instance0 again

        assertEquals("1", instance1_2);
        assertEquals("0", instance0_2);

        assertSame(instance0, instance0_2);
        assertSame(instance1, instance1_2);

        assertNotNull(objectPool.instance());
        assertNotNull(objectPool.instance());
        assertNotNull(objectPool.instance());
        assertNotNull(objectPool.instance());
        assertNotNull(objectPool.instance());
        assertNotNull(objectPool.instance());

        String instance9 = objectPool.instance();
        assertNull(instance9);  //null, because object pool only has a capacity of 8 instances.

    }
}
