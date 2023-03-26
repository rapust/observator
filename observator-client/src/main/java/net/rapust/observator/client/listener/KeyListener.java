package net.rapust.observator.client.listener;

import net.rapust.observator.client.ClientAccessor;
import net.rapust.observator.client.ClientImpl;
import net.rapust.observator.commons.crypt.AESKey;
import net.rapust.observator.commons.crypt.RSAPublicKey;
import net.rapust.observator.protocol.connection.Client;
import net.rapust.observator.protocol.listener.Listener;
import net.rapust.observator.protocol.packet.impl.AESKeyPacket;
import net.rapust.observator.protocol.packet.impl.PublicKeyPacket;

public class KeyListener implements Listener {

    RSAPublicKey publicKey;

    public void onKey(PublicKeyPacket packet, Client client) throws Exception {
        publicKey = new RSAPublicKey(packet.getKey());

        client.setPublicKey(publicKey);

        client.write(new PublicKeyPacket(ClientAccessor.getInstance().getKeyPair().getPublic()));
    }

    public void onAes(AESKeyPacket packet, Client client) {
        AESKey key = packet.getAesKey();
        ((ClientImpl) client).setAesKey(key);
    }

}
