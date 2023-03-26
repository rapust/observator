package net.rapust.observator.server.listener;

import net.rapust.observator.commons.crypt.AESKey;
import net.rapust.observator.commons.crypt.RSAPublicKey;
import net.rapust.observator.protocol.connection.Server;
import net.rapust.observator.protocol.listener.Listener;
import net.rapust.observator.protocol.packet.impl.AESKeyPacket;
import net.rapust.observator.protocol.packet.impl.ErrorPacket;
import net.rapust.observator.protocol.packet.impl.PublicKeyPacket;
import net.rapust.observator.server.ServerAccessor;
import net.rapust.observator.server.client.ConnectedClient;

public class KeyListener implements Listener {

    public static RSAPublicKey key;
    public static AESKey aesKey;

    public void onKey(PublicKeyPacket packet, Server.ClientHandler clientHandler) throws Exception {
        key = new RSAPublicKey(packet.getKey());
        aesKey = new AESKey();

        clientHandler.setPublicKey(key);

        ConnectedClient client = ServerAccessor.getInstance().getClientManager().getClientByHandler(clientHandler);
        if (client != null) {
            client.setRsaPublicKey(key);
            client.setAesKey(aesKey);
            client.sendPacket(new AESKeyPacket(aesKey));
        } else {
            clientHandler.write(new ErrorPacket(ErrorPacket.ErrorType.CLIENT_NOT_FOUND));
            clientHandler.stop();
        }
    }

}
