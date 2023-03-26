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
public class HelloPacket implements Packet {

    private String name;
    private String HWID;
    
    private int screenWidth;
    private int screenHeight;

    @Override
    public void write(Buffer buffer, RSAPublicKey key) {
        buffer.writeString(name);
        buffer.writeString(HWID);
        buffer.writeInt(screenWidth);
        buffer.writeInt(screenHeight);
    }

    @Override
    public void read(Buffer buffer, RSAKeyPair keyPair) {
        name = buffer.readString();
        HWID = buffer.readString();
        screenWidth = buffer.readInt();
        screenHeight = buffer.readInt();
    }

}
