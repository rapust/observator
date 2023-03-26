package net.rapust.observator.commons.util;

import lombok.experimental.UtilityClass;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

@UtilityClass
public class Bytes {

    public byte[] convert(int[] intArray) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(intArray.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();

        intBuffer.put(intArray);

        return byteBuffer.array();
    }

    public int[] convert(byte[] byteArray) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();

        int[] intArray = new int[intBuffer.remaining()];
        intBuffer.get(intArray);

        return intArray;
    }

}
