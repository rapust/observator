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
public class ErrorPacket implements Packet {

    private ErrorType type;

    @Override
    public void write(Buffer buffer, RSAPublicKey key) {
        buffer.writeString(type.toString());
    }

    @Override
    public void read(Buffer buffer, RSAKeyPair keyPair) {
        this.type = ErrorType.valueOf(buffer.readString());
    }

    public enum ErrorType {
        NAME_USED,
        CLIENT_NOT_FOUND,
        WRONG_PASSWORD,
        UNACCEPTED_HWID
    }

}
