package net.rapust.observator.protocol.packet;

import net.rapust.observator.commons.crypt.RSAKeyPair;
import net.rapust.observator.commons.crypt.RSAPublicKey;
import net.rapust.observator.protocol.buffer.Buffer;

public interface Packet {

    default void write(Buffer buffer) {
        write(buffer, new RSAPublicKey());
    }

    void write(Buffer buffer, RSAPublicKey key);

    default void read(Buffer buffer) {
        read(buffer, new RSAKeyPair());
    }

    void read(Buffer buffer, RSAKeyPair keyPair);

    default int getId() {
        return PacketManager.getInstance().getIdByPacket(this);
    }

}
