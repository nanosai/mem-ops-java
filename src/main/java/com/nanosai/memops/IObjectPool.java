package com.nanosai.memops;

public interface IObjectPool<T> {

    public int capacity();

    public T instance();

}
