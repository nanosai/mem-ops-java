package com.nanosai.memops;

public class SystemArrayCopyTest {

    public static void main(String[] args) {
        long[] array = new long[16];

        for(int i=0; i<array.length; i++) {
            array[i] = i * 2;
        }

        int fromIndex = 0;
        int toIndex   = fromIndex + 1;
        int elementCount = array.length;
        int length = elementCount - toIndex;

        System.arraycopy(array, fromIndex, array, toIndex, length);

        System.out.println("blablabla");
    }
}
