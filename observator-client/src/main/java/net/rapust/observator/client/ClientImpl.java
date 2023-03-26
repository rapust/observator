package net.rapust.observator.client;

import lombok.Getter;
import lombok.Setter;
import net.rapust.observator.client.gui.ConfirmGUI;
import net.rapust.observator.commons.crypt.AESKey;
import net.rapust.observator.commons.util.Tray;
import net.rapust.observator.protocol.connection.Client;
import net.rapust.observator.protocol.packet.Packet;

import javax.swing.*;

public class ClientImpl extends Client {

    @Getter @Setter
    private AESKey aesKey;

    @Setter
    private long lastMessage = 0L;

    public ClientImpl() {
        super(ClientAccessor.getInstance().getConfig().getName(),
                ClientAccessor.getInstance().getConfig().getIp(),
                ClientAccessor.getInstance().getConfig().getPort());
    }

    @Override
    public void onPacket(Packet packet) {
        // ничего
    }

    @Override
    public void onConnect() {
        ClientAccessor.getInstance().getMainGUI().setVisible(true);
        ClientAccessor.getInstance().getMainGUI().update();
    }

    @Override
    public void onDisconnect() {
        SendingRunnable runnable = ClientAccessor.getInstance().getSendingRunnable();
        if (runnable != null) {
            runnable.stop();
        }

        ClientAccessor.getInstance().setClient(null);
        ClientAccessor.getInstance().getMainGUI().setVisibility(true);
        ClientAccessor.getInstance().getMainGUI().update();

        if (lastMessage + 1000L < System.currentTimeMillis()) {
            lastMessage = System.currentTimeMillis();
            JOptionPane.showMessageDialog(ClientAccessor.getInstance().getMainGUI().getSettingsGUI(), "Отключён от сервера", "Внимание", JOptionPane.WARNING_MESSAGE, Tray.getIcon());
        }

        ConfirmGUI.getGuis().forEach(gui -> {
            if (gui != null) {
                gui.setVisible(false);
            }
        });

        ConfirmGUI.getGuis().clear();
    }

}
