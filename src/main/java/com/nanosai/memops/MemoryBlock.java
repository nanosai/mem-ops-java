package com.nanosai.memops;

import java.nio.ByteBuffer;

/**
 *
 *
 */
public class MemoryBlock {

    public MemoryAllocator memoryAllocator = null;

    public int startIndex = 0;
    public int endIndex   = 0;

    public int readIndex  = 0;
    public int writeIndex = 0;    //equal to the length of the block already written to.

    private boolean isComplete = false;

    public MemoryBlock(MemoryAllocator memoryAllocator) {
        this.memoryAllocator = memoryAllocator;
    }


    public MemoryBlock allocate(int length) {
        this.startIndex = this.memoryAllocator.allocate(length);
        this.endIndex   = this.startIndex + length;
        this.readIndex  = this.startIndex;
        this.writeIndex = this.startIndex;

        return this;
    }

    public void setComplete(boolean complete){
        this.isComplete = complete;
    }

    public boolean isComplete() {
        return this.isComplete;
    }

    public int lengthWritten() {
        return this.writeIndex - this.startIndex;
    }

    public int lengthAllocated() {
        return this.endIndex - this.startIndex;
    }

    public void free() {
        this.memoryAllocator.free(this);
    }

    //todo does this method really belong here?

    public void writeLeadByte(int leadByte){
        this.memoryAllocator.data[this.writeIndex++] = (byte) (255 & (leadByte));
    }


    //todo does this method really belong here? IAP specific code!
    public void writeLength(int length, int lengthLength){
        for(int i=(lengthLength -1) * 8; i>=0; i-=8){
            this.memoryAllocator.data[this.writeIndex++] = (byte) (255 & (length >> i));
        }
    }

    //todo does this method really belong here? Should it be renamed to just "write" ?
    public void writeValue(ByteBuffer byteBuffer, int length){
        byteBuffer.get(this.memoryAllocator.data, this.writeIndex, length);
        this.writeIndex += length;
    }

    public void copyFrom(MemoryBlock source){
        System.arraycopy(
                source.memoryAllocator.data, source.startIndex,
                this  .memoryAllocator.data, this.writeIndex,
                source.lengthWritten()
        );
        this.writeIndex += source.lengthWritten();

    }


}
