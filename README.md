# Mem Ops for Java

- [Introduction](#introduction)
- [Mem Ops Tutorial](#tutorial)
- [The Basics](#basics)
- [Advantages Over Standard Java Byte Array Instantiation](#advantages)
- [Maven Dependency](#maven-dependency)
- [Version History](#version-history)


<a name="introduction"></a>

# Introduction

Mem Ops for Java is a Java memory manager / allocator for use in systems requiring steady state memory consumption
(as little garbage collection as possible).

The main use case is high performance, high reliability, low latency distributed systems, but Mem Ops can be used for
any use case that requires stable memory consumption.

Mem Ops is developed by [https://nanosai.com](https://nanosai.com) .


<a name="tutorial"></a>

# Mem Ops Tutorial
This README.md page introduces what Mem Ops is, but if you are looking for a tutorial explaining how to use
Mem Ops in more detail, we have one here:

[http://tutorials.jenkov.com/mem-ops/index.html](http://tutorials.jenkov.com/mem-ops/index.html)


<a name="basics"></a>

# The Basics

Mem Ops has two core packages:

 - com.nanosai.memops.bytes
 - com.nanosai.memops.objects

The com.nanosai.memops.bytes package contains classes for allocating bytes (sections of a bigger byte array). Using these
classes you can allocate a single, big byte array from which you can allocate smaller blocks. That way you only
ever allocate the bigger byte array, even though you subdivide it and use the smaller blocks individually.
When you are done with a byte block, you must explicitly free it again. When
a block is freed it can be reallocated for use again.

The com.nanosai.memops.objects package contains classes for allocating (pooling) objects. These can be any object
you need. The package comes with some built-in classes for Bytes - which represents a block of bytes in a shared
byte array. Thus, a Bytes instance contains a byte[] array reference, a start index, end index etc. which is used
to identify the byte block in the big byte array.


<a name="advantages"></a>

# Advantages Over Standard Java Byte Array Instantiation
Allocating byte blocks from a bigger, shared byte array can, in some cases, provide several advantages
over allocating byte arrays via the Java "new" command. I will explain these advantages in the following sections:


## Avoiding Heap Fragmentation
By obtaining a part of a bigger byte array rather than creating a new byte array via the Java language "new" command,
you avoid fragmenting the heap of the Java VM. If the Java VM heap becomes fragmented, the garbage collector will
need to spend considerable time to defragment the heap, so it can be reallocated anew.

Garbage collection may cause a garbage collection pause. If your application allocates byte arrays at a fast pace,
that may put pressure on the garbage collector and result in uneven performance of your application.


## Defragmentation at Your Convenience
When memory blocks are explicitly freed, the byte allocators can either defragment
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


##Freeing Unused Bytes of a Block



<a name="maven-dependency"></a>

# Maven Dependency

If you want to use Mem Ops with Maven, the Maven dependency for Mem Ops looks like this:

    <dependency>
        <groupId>com.nanosai</groupId>
        <artifactId>mem-ops</artifactId>
        <version>0.6.2</version>
    </dependency>

Remember to substitute the version with the version of Mem Ops you want to use. See the Mem Ops version history in
the next section.


<a name="version-history"></a>

# Version History

| Version | Java Version | Change |
|---------|--------------|--------|
| 0.6.4   | Java 8       | Fixed a small bug in Bytes.allocate(), so all indexes are now set / reset correctly after allocation.   |
| 0.6.2   | Java 8       | Updated Bytes + BytesFactory to use IBytesAllocator instead of ByteAllocatorDefrag. Using IBytesAllocator allows for use of BytesAllocatorManualDefrag too.   |
| 0.6.0   | Java 8       | IObjectFactory interface instance() method had ObjectPool added as parameter.  |
| 0.5.1   | Java 8       | First release |


