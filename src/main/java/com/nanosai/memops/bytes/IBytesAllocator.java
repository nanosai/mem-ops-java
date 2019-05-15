package com.nanosai.memops.bytes;

public interface IBytesAllocator {

    public byte[] getData();

    public int capacity();

    public int freeBlockCount();

    public int freeCapacity();

    public int allocate(int length);

    public void free(int from, int too);

    public void defragment();
}
