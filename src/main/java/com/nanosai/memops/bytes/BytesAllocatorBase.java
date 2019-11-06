package com.nanosai.memops.bytes;

/**
 * A common base class for the two concrete IBytesAllcator implementations BytesAllocatorAutoDefag and
 * BytesAllocatorManualDefrag. The allocation of bytes is done in the exact same way, hence that is
 * implemented in this base class. Only the freeing of blocks is different in the two subclasses.
 */
public abstract class BytesAllocatorBase implements IBytesAllocator {

    protected static int  FREE_BLOCK_ARRAY_SIZE_INCREMENT = 16;
    protected static long FROM_AND_MASK = (long) 0xFFFFFFFF00000000L;
    protected static long TO_AND_MASK   = (long) 0x00000000FFFFFFFFL;


    protected byte[] data = null;
    protected long[] freeBlocks = new long[FREE_BLOCK_ARRAY_SIZE_INCREMENT];

    protected int freeBlockCount = 0;



    protected void init(byte[] data) {
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

    public int freeBlockStartIndex(int freeBlockIndex) {
        long freeBlockDescriptor = this.freeBlocks[freeBlockIndex];
        long startIndex = freeBlockDescriptor & FROM_AND_MASK;
        startIndex >>= 32;

        return (int) startIndex;
    }

    public int freeBlockEndIndex(int freeBlockIndex) {
        long freeBlockDescriptor = this.freeBlocks[freeBlockIndex];
        long startIndex = freeBlockDescriptor & TO_AND_MASK;
        return (int) startIndex;
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

    /**
     * This method allocates a section of the internal byte array from the first free block of that array found.
     * You should not call this method directly. Rather, obtain a MemoryBlock via getMemoryBlock() and call
     * MemoryBlock.allocate(size) on that instance.
     *
     * If a block could be allocated of the requested size, the startIndex into the underlying byte array of
     * that block is returned by this method. If no free block was large enough to allocate the requested
     * block, -1 is returned.
     *
     * @param blockSize The requested number of bytes to allocate
     * @return The startIndex into the underlying byte array of the allocated block (of the requested size),
     * or -1 if the block could not be allocated.
     */
    public int allocate(int blockSize){

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



}
