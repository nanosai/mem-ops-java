package com.nanosai.memops.bytes;

import java.util.Arrays;

/**
 * The BytesAllocatorManualDefrag class is capable of allocating (and freeing) smaller sections of a larger byte array.
 * The underlying larger byte array is passed to the BytesAllocatorManualDefrag when it is instantiated.
 *
 * When a block (section) of the bigger array is allocated, it is allocated from the first free block that
 * has the same or larger size as the block requested. In other words, if you request a block of 1024 bytes,
 * those bytes will be allocated from the first free section that is 1024 bytes or larger.
 *
 */
public class BytesAllocatorManualDefrag extends BytesAllocatorBase implements IBytesAllocator {

    public BytesAllocatorManualDefrag(byte[] data) {
        init(data);
    }


    public void free(int from, int to){
        appendFreeBlock(from, to);
    }

    protected void appendFreeBlock(long from, long to) {
        long freeBlockDescriptor = from;
        freeBlockDescriptor <<= 32;

        freeBlockDescriptor += ((long) to);

        if(this.freeBlockCount >= this.freeBlocks.length) {
            //expand array
            long[] newFreeBlocks = new long[this.freeBlocks.length + FREE_BLOCK_ARRAY_SIZE_INCREMENT];
            System.arraycopy(this.freeBlocks, 0, newFreeBlocks, 0, this.freeBlocks.length);
            this.freeBlocks = newFreeBlocks;
        }


        this.freeBlocks[freeBlockCount] = freeBlockDescriptor;
        freeBlockCount++;
    }


    public void defragment() {
        //sort
        Arrays.sort(this.freeBlocks, 0, this.freeBlockCount);

        //merge
        int newIndex = 0;

        for(int i=0; i < freeBlockCount;){
            long from = this.freeBlocks[i];
            from >>=32;

            long to   = this.freeBlocks[i];
            to &= TO_AND_MASK;

            int nextIndex  = i + 1;

            long nextFrom = this.freeBlocks[nextIndex];
            nextFrom >>=32;

            long nextTo   = this.freeBlocks[nextIndex];
            nextTo &= TO_AND_MASK;

            while(to == nextFrom ){
                to = nextTo;      //todo this can be moved to after while loop?
                nextIndex++;
                if(nextIndex == this.freeBlockCount){
                    break;
                }

                nextFrom   = this.freeBlocks[nextIndex];
                nextFrom >>=32;

                nextTo     = this.freeBlocks[nextIndex];
                nextTo    &= TO_AND_MASK;
            }

            i = nextIndex;

            long newBlockDescriptor = from;
            newBlockDescriptor <<= 32;

            newBlockDescriptor += to;

            this.freeBlocks[newIndex] = newBlockDescriptor;
            newIndex++;
        }
        this.freeBlockCount = newIndex;
    }
}
