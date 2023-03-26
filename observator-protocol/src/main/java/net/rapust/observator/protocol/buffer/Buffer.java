package net.rapust.observator.protocol.buffer;

import lombok.Getter;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Buffer {

    private int readPointer;

    @Getter
    private final List<Byte> bytes;

    private Buffer() {
        this.bytes = new ArrayList<>();
    }

    private Buffer(List<Byte> bytes) {
        this.readPointer = 0;
        this.bytes = bytes;
    }

    public static Buffer empty() {
        return new Buffer();
    }

    public static Buffer fromBytes(List<Byte> bytes) {
        return new Buffer(bytes);
    }

    public static Buffer fromBytes(Byte[] bytes) {
        return fromBytes(Arrays.asList(bytes));
    }

    public void writeDescBytes() {
        this.writeBytesUnsafe((byte) 9, (byte) -94, (byte) -66, (byte) 121);
    }

    private void writeBytesUnsafe(Byte... bytes) {
        this.bytes.addAll(Arrays.asList(bytes));
    }

    private void writeBytesUnsafe(byte[] bytes) {
        for (byte b : bytes) {
            this.bytes.add(b);
        }
    }

    public Buffer writeInt(Integer i) {
        byte[] bytes = BigInteger.valueOf(i).toByteArray();
        Byte[] b = new Byte[bytes.length];

        for (int j = 0; j < b.length; j++) {
            b[j] = bytes[j];
        }

        this.writeBytesUnsafe((byte) (b.length + 1));
        this.writeBytesUnsafe(VarType.INT.getDescriptor());
        this.writeBytesUnsafe(b);
        return this;
    }

    public Buffer writeLong(Long l) {
        byte[] bytes = BigInteger.valueOf(l).toByteArray();
        Byte[] b = new Byte[bytes.length];

        for (int j = 0; j < b.length; j++) {
            b[j] = bytes[j];
        }

        this.writeBytesUnsafe((byte) (b.length + 1));
        this.writeBytesUnsafe(VarType.LONG.getDescriptor());
        this.writeBytesUnsafe(b);
        return this;
    }

    public Buffer writeFloat(Float f) {
        return this.writeDouble(f.doubleValue());
    }

    public Buffer writeDouble(Double d) {
        byte[] bytes = ByteBuffer.allocate(8).putDouble(d).array();
        Byte[] b = new Byte[bytes.length];

        for (int j = 0; j < b.length; j++) {
            b[j] = bytes[j];
        }

        this.writeBytesUnsafe((byte) (b.length + 1));
        this.writeBytesUnsafe(VarType.DOUBLE.getDescriptor());
        this.writeBytesUnsafe(b);
        return this;
    }

    public Buffer writeString(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        Byte[] b = new Byte[bytes.length];

        for (int j = 0; j < b.length; j++) {
            b[j] = bytes[j];
        }

        this.writeInt(b.length + 1);
        this.writeBytesUnsafe(VarType.STRING.getDescriptor());
        this.writeBytesUnsafe(b);
        return this;
    }

    public Buffer writeByteArray(byte[] array) {
        this.writeInt(array.length + 1);
        this.writeBytesUnsafe(VarType.BYTE_ARRAY.getDescriptor());
        this.writeBytesUnsafe(array);

        return this;
    }

    public List<Byte> readBytes() {
        if (this.bytes.size() - 1 < this.readPointer) {
            return new ArrayList<>();
        }

        int length = bytes.get(this.readPointer);
        this.readPointer++;

        int last = this.readPointer + length - 1;

        if (this.bytes.size() - 1 < last) {
            return new ArrayList<>();
        }

        List<Byte> bytes = new ArrayList<>();

        while (this.readPointer <= last) {
            bytes.add(this.bytes.get(this.readPointer));
            this.readPointer++;
        }

        return bytes;
    }

    public List<Byte> readBytes(int l) {
        List<Byte> bytes = new ArrayList<>();

        for (int i = 0; i < l; i++) {
            bytes.add(this.bytes.get(this.readPointer));
            this.readPointer++;
        }

        return bytes;
    }

    public int readInt() {
        List<Byte> bytes = this.readBytes();

        if (bytes.size() < 1) {
            return 0;
        }

        VarType type = VarType.getByDescriptor(bytes.get(0));
        if (type != VarType.INT) {
            return 0;
        }

        byte[] safeBytes = new byte[bytes.size() - 1];

        for (int i = 1; i < bytes.size(); i++) {
            safeBytes[i - 1] = bytes.get(i);
        }

        return new BigInteger(safeBytes).intValue();
    }

    public long readLong() {
        List<Byte> bytes = this.readBytes();

        if (bytes.size() < 1) {
            return 0;
        }

        VarType type = VarType.getByDescriptor(bytes.get(0));
        if (type != VarType.LONG) {
            return 0;
        }

        byte[] safeBytes = new byte[bytes.size() - 1];

        for (int i = 1; i < bytes.size(); i++) {
            safeBytes[i - 1] = bytes.get(i);
        }

        return new BigInteger(safeBytes).longValue();
    }

    public double readDouble() {
        List<Byte> bytes = this.readBytes();

        if (bytes.size() < 1) {
            return 0;
        }

        VarType type = VarType.getByDescriptor(bytes.get(0));
        if (type != VarType.DOUBLE) {
            return 0;
        }

        byte[] safeBytes = new byte[bytes.size() - 1];

        for (int i = 1; i < bytes.size(); i++) {
            safeBytes[i - 1] = bytes.get(i);
        }

        return ByteBuffer.wrap(safeBytes).getDouble();
    }

    public String readString() {
        List<Byte> bytes = this.readBytes(readInt());

        if (bytes.size() < 1) {
            return null;
        }

        VarType type = VarType.getByDescriptor(bytes.get(0));
        if (type != VarType.STRING) {
            return null;
        }

        byte[] safeBytes = new byte[bytes.size() - 1];

        for (int i = 1; i < bytes.size(); i++) {
            safeBytes[i - 1] = bytes.get(i);
        }

        return new String(safeBytes, StandardCharsets.UTF_8);
    }

    public byte[] readByteArray() {
        List<Byte> bytes = this.readBytes(readInt());

        if (bytes.size() < 1) {
            return null;
        }

        VarType type = VarType.getByDescriptor(bytes.get(0));
        if (type != VarType.BYTE_ARRAY) {
            return null;
        }

        byte[] array = new byte[bytes.size() - 1];
        for (int i = 1; i < bytes.size(); i++) {
            array[i - 1] = bytes.get(i);
        }

        return array;
    }

    public byte[] toArray() {
        byte[] array = new byte[this.bytes.size()];

        for (int i = 0; i < array.length; i++) {
            array[i] = this.bytes.get(i);
        }

        return array;
    }

}
