package com.nanosai.memops.bytes;

/**
 * The BytesAllocatorAutoDefrag class is capable of allocating (and freeing) smaller sections of a larger byte array.
 * The underlying larger byte array is passed to the BytesAllocatorAutoDefrag when it is instantiated.
 *
 * When a block (section) of the bigger array is allocated, it is allocated from the first free block that
 * has the same or larger size as the block requested. In other words, if you request a block of 1024 bytes,
 * those bytes will be allocated from the first free section that is 1024 bytes or larger.
 *
 * */
public class BytesAllocatorAutoDefrag extends BytesAllocatorBase implements IBytesAllocator {

    public BytesAllocatorAutoDefrag(byte[] data) {
        init(data);
    }


    public void free(int from, int to) {
        long freeBlockDescriptor = from;
        freeBlockDescriptor <<=32;
        freeBlockDescriptor += ((long) to);

        for(int i=0; i<this.freeBlockCount; i++){
            if(freeBlockDescriptor < this.freeBlocks[i]){
                //insert the free block here - at index i
                boolean mergeWithPreviousBlock = i > 0 && (from == (int) (this.freeBlocks[i-1]  & TO_AND_MASK));
                boolean mergeWithNextBlock     =          (to   == (int) (this.freeBlocks[i]   >> 32));

                if(mergeWithPreviousBlock && mergeWithNextBlock) {
                    this.freeBlocks[i-1] = (this.freeBlocks[i-1] & FROM_AND_MASK) | (this.freeBlocks[i]   & TO_AND_MASK);
                    int length = this.freeBlockCount - i -1;
                    System.arraycopy(this.freeBlocks, i+1, this.freeBlocks, i, this.freeBlockCount - i -1);
                    this.freeBlockCount--;
                    return;
                }
                if(mergeWithPreviousBlock){
                    this.freeBlocks[i-1] = (this.freeBlocks[i-1] & FROM_AND_MASK) | to;
                    return;
                }
                if(mergeWithNextBlock){
                    this.freeBlocks[i] = (from << 32) | (this.freeBlocks[i] & TO_AND_MASK);
                    return;
                }

                //new free block is not adjacent to either previous nor next block, so insert it here.
                //todo check if this can result in a freeBlocks IndexOutOfBoundsException - if there are more free blocks than freeBlocks has space for.
                int length = this.freeBlockCount - i;
                System.arraycopy(this.freeBlocks, i, this.freeBlocks, i+1, length);
                this.freeBlocks[i] = freeBlockDescriptor;
                this.freeBlockCount++;
                return;
            }
        }

        //no place found to insert the free block, so append it at the end instead.
        //todo maybe extract if-statement into an "expandFreeBlocksArrayIfNecessary() method?
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
        //NOP - defragment happens already when blocks are freed.
    }

}
