package com.nanosai.memops.bytes;

import com.nanosai.memops.bytes.BytesAllocatorAutoDefrag;
import org.junit.jupiter.api.Test;

public class BytesAllocatorAutoDefragTest {


    @Test
    public void test() {
        BytesAllocatorAutoDefrag allocator = new BytesAllocatorAutoDefrag(new byte[1024]);

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
