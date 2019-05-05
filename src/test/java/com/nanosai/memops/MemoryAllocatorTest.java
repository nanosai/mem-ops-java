package com.nanosai.memops;

import org.junit.jupiter.api.Test;

public class MemoryAllocatorTest {

    public void testInstantiation() {
        byte[] data = new byte[1024 * 1024];
        long[] freeBlocks = new long[1024];
        MemoryAllocator memoryAllocator = new MemoryAllocator(data, freeBlocks);

        MemoryBlock memoryBlock = memoryAllocator.getMemoryBlock();
        if(memoryBlock != null) {
            memoryBlock.allocate(1024);
        }


    }

    @Test
    public void test() {

    }
}
