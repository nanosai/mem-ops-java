# Mem Ops for Java
Mem Ops for Java is a Java memory manager / allocator for use in systems requiring steady state memory consumption
(as little garbage collection as possible).

The main use case is high performance, high reliability, low latency distributed systems, but Mem Ops can be used for
any use case that requires stable memory consumption.



# The Basics

Mem Ops has two core classes:

 - MemoryAllocator
 - MemoryBlock

The MemoryAllocator allocates a single, big byte array which can then be reallocated by the MemoryAllocator
into smaller memory blocks. Each memory block is represented by a MemoryBlock object.

When your application needs a byte array, instead of creating a new byte array using

    new byte[size]

your application can obtain a byte array from the MemoryAllocator. Actually, what you get is a block of
its big, internal byte array which you can use. Thus, if you write beyond the end of your block, you
risk writing into a block allocated for another purpose.

When you are done with a MemoryBlock you must explicitly free it. Freeing the memory block will mark
that section of the MemoryAllocator's big, internal byte array as free for allocation again.

MemoryBlock objects are pooled internally, and thus reused. That is done to avoid creating a new MemoryBlock
each time a byte block is allocated. Otherwise you would still put a lot of pressure on the garbage collector.


# Advantages Over Traditional Memory Allocation
Allocating byte blocks from a bigger, shared byte array can, in some cases, provide several advantages
over allocating byte arrays via the Java "new" command. I will explain these advantages in the following sections:


## Avoiding Heap Fragmentation
By obtaining a part of a bigger byte array rather than creating a new byte array via the Java language "new" command,
you avoid fragmenting the heap of the Java VM. If the Java VM heap becomes fragmented, the garbage collector will
need to spend considerable time to defragment the heap, so it can be reallocated anew.

Garbage collection may cause a garbage collection pause. If your application allocates byte arrays at a fast pace,
that may put pressure on the garbage collector and result in uneven performance of your application.


## Defragmentation at Your Convenience
When memory blocks are explicitly freed, the MemoryAllocator can either defragment
the internal byte array immediately, or at a later time when it is convenient for you. This way you get to choose
when defragmentation (garbage collection) takes place. You can either garbage collect immediately when the
memory block is freed, thus paying the price of defragmentation immediately (and just for that memory block),
or you can defragment the big byte array when your application has no or little other work to do.

Since you cannot explicitly free objects in Java, nor trigger the garbage collector, you do not have these options
with normally allocated byte arrays.


## Checking if Enough Memory is Free Before Allocation
You can check if it possible to allocate the desired memory before actually doing so. If not, you could e.g.
reject an incoming request, or simply not read any more data from inbound sockets etc., in order to provide
backpressure back up your request chain. This is not possible with the Java "new" command either.


