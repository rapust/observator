package net.rapust.observator.client.listener;

import net.rapust.observator.client.ClientAccessor;
import net.rapust.observator.commons.util.Tray;
import net.rapust.observator.protocol.connection.impl.Client;
import net.rapust.observator.protocol.listener.Listen;
import net.rapust.observator.protocol.listener.Listener;
import net.rapust.observator.protocol.packet.impl.ErrorPacket;

import javax.swing.*;

public class ErrorListener implements Listener {

    @Listen
    public void onError(ErrorPacket packet, Client client) {
        if (packet.getType() == ErrorPacket.ErrorType.NAME_USED) {
            ClientAccessor.getInstance().getMainGUI().setVisibility(true);
            JOptionPane.showMessageDialog(ClientAccessor.getInstance().getMainGUI().getSettingsGUI(), "Это имя клиента уже используется на данном сервере!", "Ошибка", JOptionPane.ERROR_MESSAGE, Tray.getIcon());
        }
    }

}
