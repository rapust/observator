package net.rapust.observator.protocol.packet.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rapust.observator.commons.crypt.AESKey;
import net.rapust.observator.commons.crypt.RSAKeyPair;
import net.rapust.observator.commons.crypt.RSAPublicKey;
import net.rapust.observator.protocol.buffer.Buffer;
import net.rapust.observator.protocol.packet.Packet;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AESKeyPacket implements Packet {

    private AESKey aesKey;

    @Override
    public void write(Buffer buffer, RSAPublicKey key) {
        buffer.writeByteArray(key.encrypt(aesKey.getKey()));
    }

    @Override
    public void read(Buffer buffer, RSAKeyPair keyPair) {
        aesKey = new AESKey(keyPair.decrypt(buffer.readByteArray()));
    }

}
