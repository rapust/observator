package net.rapust.observator.protocol.packet.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rapust.observator.commons.crypt.RSAKeyPair;
import net.rapust.observator.commons.crypt.RSAPublicKey;
import net.rapust.observator.protocol.buffer.Buffer;
import net.rapust.observator.protocol.packet.Packet;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicKeyPacket implements Packet {

    private byte[] key;

    @Override
    public void write(Buffer buffer, RSAPublicKey key) {
        buffer.writeByteArray(this.key);
    }

    @Override
    public void read(Buffer buffer, RSAKeyPair keyPair) {
        key = buffer.readByteArray();
    }

}
