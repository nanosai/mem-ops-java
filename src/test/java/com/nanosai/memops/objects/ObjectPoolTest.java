package com.nanosai.memops.objects;

import com.nanosai.memops.bytes.BytesAllocatorAutoDefrag;
import com.nanosai.memops.objects.Bytes;
import com.nanosai.memops.objects.BytesFactory;
import com.nanosai.memops.objects.IObjectFactory;
import com.nanosai.memops.objects.ObjectPool;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ObjectPoolTest {

    @Test
    public void testInstance() {
        IObjectFactory<String> stringFactory = new IObjectFactory<String>() {
            int instanceNo = 0;
            @Override
            public String instance(ObjectPool<String> objectPool) {
                String value = "" + instanceNo;
                instanceNo++;
                return value;
            }
        };

        IObjectFactory stringFactory2 = (objectPool) -> "" + System.currentTimeMillis();

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

    @Test
    public void testByteArrayBlock() {
        BytesAllocatorAutoDefrag allocator = new BytesAllocatorAutoDefrag(new byte[1024]);
        ObjectPool<Bytes> objectPool = new ObjectPool<>(8, new BytesFactory(allocator) );

        Bytes bytes1 = objectPool.instance();
        Bytes bytes2 = objectPool.instance();

        assertNotSame(bytes1, bytes2);

        assertEquals(0, bytes1.startIndex);
        assertEquals(0, bytes1.length);

        assertTrue(bytes1.allocate(16));
        assertEquals(0, bytes1.startIndex);
        assertEquals(16, bytes1.length);

        assertTrue(bytes2.allocate(16));
        assertEquals(16, bytes2.startIndex);
        assertEquals(16, bytes2.length);

    }
}
