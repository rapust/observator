package net.rapust.observator.protocol.packet.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rapust.observator.commons.crypt.RSAKeyPair;
import net.rapust.observator.commons.crypt.RSAPublicKey;
import net.rapust.observator.protocol.buffer.Buffer;
import net.rapust.observator.protocol.packet.Packet;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyboardClickPacket implements Packet {

    private int button;
    private ClickAction action;

    @Override
    public void write(Buffer buffer, RSAPublicKey key) {
        buffer.writeByteArray(key.encrypt(BigInteger.valueOf(button).toByteArray()));
        buffer.writeByteArray(key.encrypt(action.toString().getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void read(Buffer buffer, RSAKeyPair keyPair) {
        button = new BigInteger(keyPair.decrypt(buffer.readByteArray())).intValue();
        action = ClickAction.valueOf(new String(keyPair.decrypt(buffer.readByteArray()), StandardCharsets.UTF_8));
    }

    public static enum ClickAction {
        PRESSED,
        RELEASED;

        private ClickAction() {
        }
    }
}
