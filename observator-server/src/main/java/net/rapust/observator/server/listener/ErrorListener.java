package net.rapust.observator.server.listener;

import net.rapust.observator.commons.util.Tray;
import net.rapust.observator.protocol.connection.impl.Server;
import net.rapust.observator.protocol.listener.Listen;
import net.rapust.observator.protocol.listener.Listener;
import net.rapust.observator.protocol.packet.impl.ErrorPacket;
import net.rapust.observator.server.ServerAccessor;
import net.rapust.observator.server.client.ConnectedClient;
import net.rapust.observator.server.gui.PasswordGUI;

import javax.swing.*;
import java.util.Objects;

public class ErrorListener implements Listener {

    @Listen
    public void onError(ErrorPacket packet, Server.ClientHandler clientHandler) {

        if (Objects.requireNonNull(packet.getType()) == ErrorPacket.ErrorType.WRONG_PASSWORD) {
            ConnectedClient client = ServerAccessor.getInstance().getClientManager().getClientByHandler(clientHandler);

            if (client == null) {
                return;
            }

            PasswordGUI oldGui = client.getPasswordGUI();
            if (oldGui == null) {
                oldGui = new PasswordGUI(ServerAccessor.getInstance(), client);
                client.setPasswordGUI(oldGui);
                oldGui.setVisible(true);
            } else {
                oldGui.setVisible(false);
                oldGui.getPasswordField().setText("");
                oldGui.setVisible(true);
            }

            if (client.isUsedSaved()) {
                client.setSavedPassword(null);
            } else {
                JOptionPane.showMessageDialog(oldGui, "Неверный пароль!", "Ошибка", JOptionPane.ERROR_MESSAGE, Tray.getIcon());
            }
        }

    }

}
