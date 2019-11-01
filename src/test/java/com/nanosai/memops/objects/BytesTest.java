package com.nanosai.memops.objects;

import com.nanosai.memops.bytes.BytesAllocatorAutoDefrag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class BytesTest {

    @Test
    public void testInstantiationViaObjectPool() {

        BytesAllocatorAutoDefrag byteArrayAllocator = new BytesAllocatorAutoDefrag(new byte[1024 * 1024]);

        BytesFactory bytesFactory = new BytesFactory(byteArrayAllocator);

        ObjectPool<Bytes> objectPool = new ObjectPool<Bytes>(3, bytesFactory);

        Bytes bytes1 = objectPool.instance();
        Bytes bytes2 = objectPool.instance();
        Bytes bytes3 = objectPool.instance();
        Bytes bytes4 = objectPool.instance();

        assertNotSame(bytes1, bytes2);
        assertNotSame(bytes1, bytes3);
        assertNotSame(bytes2, bytes3);

        assertNull(bytes4);
    }

    @Test
    public void testBytesAllocation() {
        BytesAllocatorAutoDefrag byteArrayAllocator = new BytesAllocatorAutoDefrag(new byte[1024 * 1024]);

        BytesFactory bytesFactory = new BytesFactory(byteArrayAllocator);

        ObjectPool<Bytes> objectPool = new ObjectPool<Bytes>(3,bytesFactory);

        assertEquals(0, objectPool.instancesFree());
        assertEquals(0, objectPool.instances());

        Bytes bytes1 = objectPool.instance();
        assertEquals(0, objectPool.instancesFree());

        assertEquals(1024 * 1024, byteArrayAllocator.freeCapacity());
        bytes1.allocate(1024);
        assertEquals((1024 * 1024) - 1024, byteArrayAllocator.freeCapacity());

        bytes1.free();
        assertEquals(1024 * 1024, byteArrayAllocator.freeCapacity());
        assertEquals(1, objectPool.instancesFree());
        assertEquals(1, objectPool.instances());


        int instances = objectPool.instances();
        System.out.println(instances);
    }
}
