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
public class MouseMovePacket implements Packet {

    private int x;
    private int y;

    @Override
    public void write(Buffer buffer, RSAPublicKey key) {
        buffer.writeInt(x);
        buffer.writeInt(y);
    }

    @Override
    public void read(Buffer buffer, RSAKeyPair keyPair) {
        this.x = buffer.readInt();
        this.y = buffer.readInt();
    }

}
