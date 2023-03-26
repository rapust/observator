package net.rapust.observator.protocol.buffer;

import net.rapust.observator.commons.crypt.RSAKeyPair;
import net.rapust.observator.commons.logger.MasterLogger;
import net.rapust.observator.protocol.packet.Packet;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ByteContainer {

    private final List<Byte> bytes = new ArrayList<>();
    private final HashMap<String, Constructor<?>> constructors = new HashMap<>();

    public void add(byte b) {
        bytes.add(b);
    }

    public boolean check() {
        int size = bytes.size();

        if (size < 4) {
            return false;
        }

        return bytes.get(size - 1) == 121 && bytes.get(size - 2) == -66 && bytes.get(size - 3) == -94 && bytes.get(size - 4) == 9;
    }

    public Buffer toBuffer() {
        Byte[] bytes = new Byte[this.bytes.size() - 4];

        for (int i = 0; i < this.bytes.size() - 4; i++) {
            bytes[i] = this.bytes.get(i);
        }

        this.bytes.clear();
        return Buffer.fromBytes(bytes);
    }

    public Packet toPacket(RSAKeyPair keyPair) {
        try {
            if (keyPair == null) {
                keyPair = new RSAKeyPair();
            }

            Buffer buffer = toBuffer();

            String packetName = buffer.readString();

            Constructor<?> constructor = constructors.get(packetName);

            if (constructor == null) {
                Class<?> packetClass = Class.forName(packetName);
                constructor = packetClass.getConstructor();
                constructors.put(packetName, constructor);
            }

            Packet packet = (Packet) constructor.newInstance();

            packet.read(buffer, keyPair);

            return packet;
        } catch (Exception e) {
            MasterLogger.error(e);
            return null;
        }
    }

}
