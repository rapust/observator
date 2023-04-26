package net.rapust.observator.client.listener;

import net.rapust.observator.client.ClientAccessor;
import net.rapust.observator.client.gui.ConfirmGUI;
import net.rapust.observator.protocol.connection.impl.Client;
import net.rapust.observator.protocol.listener.Listen;
import net.rapust.observator.protocol.listener.Listener;
import net.rapust.observator.protocol.packet.impl.HelloPacket;

public class HelloListener implements Listener {

    @Listen
    public void onHello(HelloPacket packet, Client client) {
        String ip = client.getIp() + ":" + client.getPort();
        String HWID = packet.getHWID();

        if (!ClientAccessor.getInstance().getVerifiedServers().getOrDefault(ip, "-") .equals(HWID)) {
            ConfirmGUI confirmGUI = new ConfirmGUI(ip, packet.getName(), HWID);
            confirmGUI.setVisible(true);
        }
    }

}
