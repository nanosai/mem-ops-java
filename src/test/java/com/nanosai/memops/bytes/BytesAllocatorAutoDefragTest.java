package com.nanosai.memops.bytes;

import com.nanosai.memops.bytes.BytesAllocatorAutoDefrag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BytesAllocatorAutoDefragTest {


    @Test
    public void test() {
        BytesAllocatorAutoDefrag allocator = new BytesAllocatorAutoDefrag(new byte[1024]);

        assertEquals(  16, allocator.freeBlocks.length);
        assertEquals(   1, allocator.freeBlockCount);
        assertEquals(   0, allocator.freeBlockStartIndex(0));
        assertEquals(1024, allocator.freeBlockEndIndex(0));


        int offset1 = allocator.allocate(16);
        assertEquals   (1, allocator.freeBlockCount);
        assertEquals(  16, allocator.freeBlockStartIndex(0));
        assertEquals(1024, allocator.freeBlockEndIndex(0));

        int offset2 = allocator.allocate(16);
        assertEquals   (1, allocator.freeBlockCount);
        assertEquals(  32, allocator.freeBlockStartIndex(0));
        assertEquals(1024, allocator.freeBlockEndIndex(0));

        int offset3 = allocator.allocate(16);
        assertEquals   (1, allocator.freeBlockCount);
        assertEquals(  48, allocator.freeBlockStartIndex(0));
        assertEquals(1024, allocator.freeBlockEndIndex(0));

        int offset4 = allocator.allocate(16);
        assertEquals   (1, allocator.freeBlockCount);
        assertEquals(  64, allocator.freeBlockStartIndex(0));
        assertEquals(1024, allocator.freeBlockEndIndex(0));

        int offset5 = allocator.allocate(16);
        assertEquals   (1, allocator.freeBlockCount);
        assertEquals(  80, allocator.freeBlockStartIndex(0));
        assertEquals(1024, allocator.freeBlockEndIndex(0));

        allocator.free(offset2,offset2 + 16);
        assertEquals   (2, allocator.freeBlockCount);
        assertEquals(  16, allocator.freeBlockStartIndex(0));
        assertEquals(  32, allocator.freeBlockEndIndex(0));
        assertEquals(  80, allocator.freeBlockStartIndex(1));
        assertEquals(1024, allocator.freeBlockEndIndex(1));


        allocator.free(offset4,offset4 + 16);
        assertEquals   (3, allocator.freeBlockCount);
        assertEquals(  16, allocator.freeBlockStartIndex(0));
        assertEquals(  32, allocator.freeBlockEndIndex(0));
        assertEquals(  48, allocator.freeBlockStartIndex(1));
        assertEquals(  64, allocator.freeBlockEndIndex(1));
        assertEquals(  80, allocator.freeBlockStartIndex(2));
        assertEquals(1024, allocator.freeBlockEndIndex(2));

        allocator.free(offset3,offset3 + 16);
        assertEquals   (2, allocator.freeBlockCount);
        assertEquals(  16, allocator.freeBlockStartIndex(0));
        assertEquals(  64, allocator.freeBlockEndIndex(0));
        assertEquals(  80, allocator.freeBlockStartIndex(1));
        assertEquals(1024, allocator.freeBlockEndIndex(1));

        allocator.free(offset5,offset5 + 16);
        assertEquals   (1, allocator.freeBlockCount);
        assertEquals(  16, allocator.freeBlockStartIndex(0));
        assertEquals(1024, allocator.freeBlockEndIndex(0));

        allocator.free(offset1,offset1 + 16);
        assertEquals   (1, allocator.freeBlockCount);
        assertEquals(   0, allocator.freeBlockStartIndex(0));
        assertEquals(1024, allocator.freeBlockEndIndex(0));



    }
}
