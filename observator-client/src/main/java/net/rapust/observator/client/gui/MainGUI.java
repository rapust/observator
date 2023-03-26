package net.rapust.observator.client.gui;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.rapust.observator.client.ClientAccessor;
import net.rapust.observator.client.ClientImpl;
import net.rapust.observator.commons.logger.MasterLogger;
import net.rapust.observator.commons.util.Tray;

import javax.swing.*;
import java.awt.*;

@Data
public class MainGUI extends JFrame {

    private final ClientAccessor clientAccessor;

    private final SettingsGUI settingsGUI;


    private ClientStatus status = ClientStatus.IDLING;
    private final JLabel statusLabel;

    private final JLabel verifiedLabel;
    private final JTextArea verifiedArea;

    private final JButton disconnectButton;

    public MainGUI(ClientAccessor clientAccessor) {
        super("Observator клиент");

        this.clientAccessor = clientAccessor;
        clientAccessor.setMainGUI(this);

        settingsGUI = new SettingsGUI(clientAccessor);

        statusLabel = new JLabel(status.format());

        verifiedLabel = new JLabel("Подтвержденные серверы:");

        verifiedArea = new JTextArea("", 7, 20);
        verifiedArea.setLineWrap(true);
        verifiedArea.setWrapStyleWord(true);
        verifiedArea.setEditable(false);

        disconnectButton = new JButton("Отключиться");
        disconnectButton.addActionListener((click) -> {
            ClientImpl client = ClientAccessor.getInstance().getClient();
            if (client != null) {
                try {
                    client.disconnect();
                } catch (Exception e) {
                    MasterLogger.error(e);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Клиент и так не подключён!", "Ошибка", JOptionPane.ERROR_MESSAGE, Tray.getIcon());
            }
        });

        JPanel panel = new JPanel();
        panel.add(statusLabel);
        panel.add(verifiedLabel);
        panel.add(verifiedArea);
        panel.add(new JScrollPane(verifiedArea), BorderLayout.CENTER);
        panel.add(disconnectButton);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(275, 240);
        setContentPane(panel);

        if (isVisible()) {
            update();
        }
    }

    public void setVisibility(boolean visibility) {
        this.setVisible(visibility);
        settingsGUI.setVisible(visibility);
    }

    public void update() {
        if (ClientAccessor.getInstance().getClient() == null) {
            status = ClientStatus.IDLING;
        } else {
            if (ClientAccessor.getInstance().getSendingRunnable() == null) {
                status = ClientStatus.CONNECTED;
            } else {
                status = ClientStatus.SENDING;
            }
        }

        statusLabel.setText(status.format());
        verifiedArea.setEditable(true);

        StringBuilder builder = new StringBuilder();
        ClientAccessor.getInstance().getVerifiedServers().keySet().forEach(s -> {
            if (!builder.toString().contains(s + "\n")) {
                builder.append(s).append("\n");
            }
        });

        verifiedArea.setText(builder.toString());
        verifiedArea.setEditable(false);
    }

    @AllArgsConstructor
    public enum ClientStatus {
        IDLING("Отдых     "),
        CONNECTED("Подключён к %server%"),
        SENDING("Отправка на %server%");

        private final String text;

        public String format() {
            String prefix = this == IDLING ? "   Статус: " : "Статус: ";

            ClientImpl client = ClientAccessor.getInstance().getClient();
            if (client == null) {
                return prefix + text;
            }

            return prefix + text.replace("%server%", client.getIp() + ":" + client.getPort());
        }

    }

}
