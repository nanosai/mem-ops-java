package com.nanosai.memops;

import org.junit.jupiter.api.Test;

public class ByteArrayAllocatorTest {


    @Test
    public void test() {
        ByteArrayAllocator allocator = new ByteArrayAllocator(new byte[1024]);

        int offset1 = allocator.allocate(16);
        int offset2 = allocator.allocate(16);
        int offset3 = allocator.allocate(16);
        int offset4 = allocator.allocate(16);

        allocator.freeAndDefragment(offset1,offset1 + 16);
        allocator.freeAndDefragment(offset2,offset2 + 16);
        allocator.freeAndDefragment(offset4,offset4 + 16);
        allocator.freeAndDefragment(offset3,offset3 + 16);



    }
}
