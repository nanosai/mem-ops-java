package com.nanosai.memops;

/**
 * An interface representing a MemoryBlock factory. A MemoryBlock factory can be used to create instances
 * of subclasses of MemoryBlock. A MemoryBlock may represent e.g. a message received from a specific socket
 * (e.g. an IAP message). You might want to create a subclass of MemoryBlock which can also hold a reference
 * to a Socket, or a socket id, in addition to the core MemoryBlock fields.
 *
 * Created by jjenkov on 26-05-2016.
 */
public interface IMemoryBlockFactory {


    /**
     * Create a MemoryBlock instance, or an instance of a subclass of MemoryBlock.
     * @param memoryAllocator The MemoryAllocator to obtain the underlying byte array interval (block) from.
     * @return A MemoryBlock instance or MemoryBlock subclass instance.
     */
    public MemoryBlock createMemoryBlock(MemoryAllocator memoryAllocator);

}
