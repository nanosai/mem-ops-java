package com.nanosai.memops;

import org.junit.jupiter.api.Test;

public class ByteArrayAllocatorAutoDefragTest {


    @Test
    public void test() {
        ByteArrayAllocatorAutoDefrag allocator = new ByteArrayAllocatorAutoDefrag(new byte[1024]);

        int offset1 = allocator.allocate(16);
        int offset2 = allocator.allocate(16);
        int offset3 = allocator.allocate(16);
        int offset4 = allocator.allocate(16);
        int offset5 = allocator.allocate(16);

        allocator.free(offset2,offset2 + 16);
        allocator.free(offset4,offset4 + 16);
        allocator.free(offset3,offset3 + 16);
        allocator.free(offset5,offset5 + 16);
        allocator.free(offset1,offset1 + 16);



    }
}
