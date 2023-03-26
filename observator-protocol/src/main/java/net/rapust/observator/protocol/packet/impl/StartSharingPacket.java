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
public class StartSharingPacket implements Packet {

    private byte[] password;
    private String HWID;

    @Override
    public void write(Buffer buffer, RSAPublicKey key) {
        buffer.writeByteArray(password);
        buffer.writeString(HWID);
    }

    @Override
    public void read(Buffer buffer, RSAKeyPair keyPair) {
        this.password = buffer.readByteArray();
        this.HWID = buffer.readString();
    }

}
