package net.rapust.observator.server;

import net.rapust.observator.commons.logger.MasterLogger;
import net.rapust.observator.commons.util.Async;
import net.rapust.observator.commons.util.Tray;
import net.rapust.observator.protocol.connection.Server;
import net.rapust.observator.protocol.packet.Packet;
import net.rapust.observator.server.client.ClientManager;
import net.rapust.observator.server.client.ConnectedClient;

import javax.swing.*;
import java.awt.*;

public class ServerImpl extends Server {

    public ServerImpl() {
        super(ServerAccessor.getInstance().getConfig().getName(),
                ServerAccessor.getInstance().getConfig().getPort());
    }

    @Override
    public void onPacket(ClientHandler clientHandler, Packet packet) {
        ConnectedClient client = ServerAccessor.getInstance().getClientManager().getClientByHandler(clientHandler);
        if (client != null) {
            try {
                client.onPacket(packet);
            } catch (Exception e) {
                MasterLogger.error("Ошибка при обработке пакета клиентом " + clientHandler.getIP());
            }
        }
    }

    @Override
    public void onConnect(ClientHandler clientHandler) {
        Tray.display("Observator", "Подключён клиент " + clientHandler.getIP() + ".", TrayIcon.MessageType.INFO);
    }

    @Override
    public void onDisconnect(ClientHandler clientHandler) {
        ClientManager manager = ServerAccessor.getInstance().getClientManager();
        ConnectedClient client = manager.getClientByHandler(clientHandler);

        if (client != null) {
            manager.removeClient(client);

            boolean show = false;

            if (client.getGUI() != null && client.getGUI().isVisible()) {
                client.getGUI().setVisible(false);
                show = true;
            }

            if (client.getPasswordGUI() != null && client.getPasswordGUI().isVisible()) {
                client.getPasswordGUI().setVisible(false);
                show = true;
            }

            if (show) {
                Async.run(() -> JOptionPane.showMessageDialog(ServerAccessor.getInstance().getMainGUI(), "Клиент " + client.getName() + " (" + client.getIP() + ") отключился!", "Внимание", JOptionPane.WARNING_MESSAGE, Tray.getIcon()));
            }
        }

        Tray.display("Observator", "Отключён клиент " + clientHandler.getIP() + ".", TrayIcon.MessageType.INFO);
        ServerAccessor.getInstance().getMainGUI().update();
    }

}
