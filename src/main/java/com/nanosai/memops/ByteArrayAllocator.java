package com.nanosai.memops;

import java.util.Arrays;

/**
 * The ByteArrayAllocator class is capable of allocating (and freeing) smaller sections of a larger byte array.
 * The underlying larger byte array is passed to the ByteArrayAllocator when it is instantiated, along with
 * an array of longs which is used to mark which parts of the internal byte array that is free.
 *
 * When a block (section) of the bigger array is allocated, it is allocated from the first free block that
 * has the same or larger size as the block requested. In other words, if you request a block of 1024 bytes,
 * those bytes will be allocated from the first free section that is 1024 bytes or larger.
 *
 * The length of the array of longs determines how many free blocks the ByteArrayAllocator can hold internally,
 * before it needs to defragment the free blocks. Defragmenting the free blocks means joining two or more
 * adjacent free blocks into a single, bigger free block.
 */
public class ByteArrayAllocator {

    private static int  FREE_BLOCK_ARRAY_SIZE_INCREMENT = 16;
    private static long FROM_AND_MASK = (long) 0xFFFFFFFF00000000L;
    private static long TO_AND_MASK   = (long) 0x00000000FFFFFFFFL;

    private byte[] data = null;
    private long[] freeBlocks = new long[FREE_BLOCK_ARRAY_SIZE_INCREMENT];
    //private long[] usedBlocks = null;

    private int freeBlockCount = 0;
    //private int nextUsedBlockIndex = 0;
    private int freeBlockCountDefragLimit = 10000;

    public ByteArrayAllocator(byte[] data) {
        init(data);
    }

    private void init(byte[] data) {
        this.data = data;
        free(0, data.length);
    }

    public byte[] getData() {
        return this.data;
    }

    public int capacity() {
        return this.data.length;
    }

    public int freeBlockCount() {
        return this.freeBlockCount;
    }

    public int freeCapacity() {
        int freeCapacity = 0;
        for(int i=0; i<this.freeBlockCount; i++){
            long from = this.freeBlocks[i];
            from >>=32;

            long to   = this.freeBlocks[i];
            to &= TO_AND_MASK;

            freeCapacity += (to - from);
        }

        return freeCapacity;
    }

    public int freeBlockCountDefragLimit() {
        return freeBlockCountDefragLimit;
    }

    public void freeBlockCountDefragLimit(int freeBlockCountDefragLimit) {
        this.freeBlockCountDefragLimit = freeBlockCountDefragLimit;
    }

    /**
     * This method allocates a section of the internal byte array from the first free block of that array found.
     * You should not call this method directly. Rather, obtain a MemoryBlock via getMemoryBlock() and call
     * MemoryBlock.allocate(size) on that instance.
     *
     * If a block could be allocated of the requested size, the offset into the underlying byte array of
     * that block is returned by this method. If no free block was large enough to allocate the requested
     * block, -1 is returned.
     *
     * @param blockSize The requested number of bytes to allocate
     * @return The offset into the underlying byte array of the allocated block (of the requested size),
     * or -1 if the block could not be allocated.
     */
    protected int allocate(int blockSize){

        boolean freeBlockFound = false;

        int freeBlockIndex = 0;

        while(!freeBlockFound && freeBlockIndex < this.freeBlockCount){
            long freeBlockFromIndex = this.freeBlocks[freeBlockIndex];
            freeBlockFromIndex >>=32;

            long freeBlockToIndex   = this.freeBlocks[freeBlockIndex];
            freeBlockToIndex &= TO_AND_MASK;

            if(blockSize <= (freeBlockToIndex-freeBlockFromIndex)){
                freeBlockFound = true;

                long newBlockDescriptor = freeBlockFromIndex + blockSize;
                newBlockDescriptor <<= 32;

                newBlockDescriptor += freeBlockToIndex;

                this.freeBlocks[freeBlockIndex] = newBlockDescriptor;
                return (int) freeBlockFromIndex;
            } else {
                freeBlockIndex++;
            }
        }
        return -1;
    }

    public void free(int from, int to){
        freeAndDefragment(from, to);

        if(freeBlockCount == freeBlockCountDefragLimit){
            defragment();
        }
    }

    protected void freeAndDefragment(long from, long to) {
        long freeBlockDescriptor = ((from << 32) + to);

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
                int length = this.freeBlockCount - i;
                System.arraycopy(this.freeBlocks, i, this.freeBlocks, i+1, length);
                this.freeBlocks[i] = freeBlockDescriptor;
                this.freeBlockCount++;
                return;
            }
        }

        //no place found to insert the free block, so append it at the end instead.
        if(this.freeBlockCount >= this.freeBlocks.length) {
            //expand array
            long[] newFreeBlocks = new long[this.freeBlocks.length + FREE_BLOCK_ARRAY_SIZE_INCREMENT];
            System.arraycopy(this.freeBlocks, 0, newFreeBlocks, 0, this.freeBlocks.length);
            this.freeBlocks = newFreeBlocks;
        }
        this.freeBlocks[freeBlockCount] = freeBlockDescriptor;
        freeBlockCount++;
    }

    protected void appendFreeBlock(int from, int to) {
        long freeBlockDescriptor = from;
        freeBlockDescriptor <<= 32;

        freeBlockDescriptor += to;

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
