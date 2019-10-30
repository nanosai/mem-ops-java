package com.nanosai.memops.objects;

public interface IObjectFactory<T> {
    public T instance(ObjectPool<T> objectPool);
}
