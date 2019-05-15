package com.nanosai.memops.objects;

import com.nanosai.memops.bytes.BytesAllocatorAutoDefrag;

/**
 * A smaller block of a bigger byte array, starting from startIndex and extending length bytes from that.
 */
public class Bytes {
    public byte[] data = null;
    public int startIndex = 0;
    public int length = 0;
    public int endIndex   = 0;

    public int readIndex  = 0;
    public int writeIndex = 0;    //equal to the length of the block already written to.

    private boolean isComplete = false;


    public BytesAllocatorAutoDefrag byteArrayAllocator = null;

    public void init(BytesAllocatorAutoDefrag byteArrayAllocator){
        this.byteArrayAllocator = byteArrayAllocator;
        this.data               = byteArrayAllocator.getData();
    }

    public boolean allocate(int length) {
        int startIndex = this.byteArrayAllocator.allocate(length);
        if(startIndex == -1) {
            return false;
        }

        this.startIndex = startIndex;
        this.length = length;
        return true;
    }

    public void free() {
        this.byteArrayAllocator.free(this.startIndex, this.startIndex + this.length);
    }


}
