package net.rapust.observator.protocol.packet.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rapust.observator.commons.crypt.RSAKeyPair;
import net.rapust.observator.commons.crypt.RSAPublicKey;
import net.rapust.observator.protocol.buffer.Buffer;
import net.rapust.observator.protocol.packet.Packet;

@Data
@AllArgsConstructor
public class StopSharingPacket implements Packet {

    @Override
    public void write(Buffer buffer, RSAPublicKey key) { }

    @Override
    public void read(Buffer buffer, RSAKeyPair keyPair) { }

}
