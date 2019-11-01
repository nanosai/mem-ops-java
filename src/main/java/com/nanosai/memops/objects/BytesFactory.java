package com.nanosai.memops.objects;

import com.nanosai.memops.bytes.IBytesAllocator;

public class BytesFactory implements IObjectFactory<Bytes> {

    private IBytesAllocator bytesAllocatorAutoDefrag = null;

    public BytesFactory(IBytesAllocator allocator){
        this.bytesAllocatorAutoDefrag = allocator;
    }

    @Override
    public Bytes instance(ObjectPool<Bytes> objectPool) {
        Bytes block = new Bytes();
        block.init(this.bytesAllocatorAutoDefrag, objectPool);
        return block;
    }
}
