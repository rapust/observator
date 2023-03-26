package net.rapust.observator.client.listener;

import net.rapust.observator.client.ClientAccessor;
import net.rapust.observator.commons.crypt.AESKey;
import net.rapust.observator.commons.logger.MasterLogger;
import net.rapust.observator.commons.util.Hash;
import net.rapust.observator.protocol.connection.Client;
import net.rapust.observator.protocol.listener.Listener;
import net.rapust.observator.protocol.packet.impl.ErrorPacket;
import net.rapust.observator.protocol.packet.impl.StartSharingPacket;
import net.rapust.observator.protocol.packet.impl.StopSharingPacket;

import java.nio.charset.StandardCharsets;

public class ScreenListener implements Listener {

    public void onStart(StartSharingPacket packet, Client client) {
        AESKey key = ClientAccessor.getInstance().getClient().getAesKey();

        String password = new String(key.decrypt(packet.getPassword()), StandardCharsets.UTF_8);

        String ourPassword = Hash.hash(ClientAccessor.getInstance().getConfig().getPassword(), "SHA256");

        if (password.equals(ourPassword)) {
            ClientAccessor.getInstance().startSending(packet.getHWID());
        } else {
            try {
                client.write(new ErrorPacket(ErrorPacket.ErrorType.WRONG_PASSWORD));
            } catch (Exception e) {
                MasterLogger.error(e);
            }
        }

        ClientAccessor.getInstance().getMainGUI().setVisible(true);
        ClientAccessor.getInstance().getMainGUI().update();
    }

    public void onStop(StopSharingPacket packet, Client client) {
        ClientAccessor.getInstance().stopSending();
        ClientAccessor.getInstance().getMainGUI().setVisible(true);
        ClientAccessor.getInstance().getMainGUI().update();
    }

}
