package com.nanosai.memops.objects;

public interface IObjectPool<T> {

    public int capacity();

    public T instance();

}
