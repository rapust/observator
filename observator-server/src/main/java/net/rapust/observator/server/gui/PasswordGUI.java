package net.rapust.observator.server.gui;

import lombok.Data;
import net.rapust.observator.commons.logger.MasterLogger;
import net.rapust.observator.commons.util.Hash;
import net.rapust.observator.commons.util.SystemInfo;
import net.rapust.observator.commons.util.Tray;
import net.rapust.observator.protocol.packet.impl.StartSharingPacket;
import net.rapust.observator.server.ServerAccessor;
import net.rapust.observator.server.client.ConnectedClient;

import javax.swing.*;
import java.nio.charset.StandardCharsets;

@Data
public class PasswordGUI extends JFrame {

    private final ServerAccessor serverAccessor;
    private final ConnectedClient client;

    private final JLabel infoLabel;
    private final JTextField passwordField;

    private final JButton sendButton;

    private String lastPassword = null;

    public PasswordGUI(ServerAccessor serverAccessor, ConnectedClient client) {
        super("Введите пароль");

        this.serverAccessor = serverAccessor;
        this.client = client;

        infoLabel = new JLabel("Пароль для " + client.getName());
        passwordField = new JTextField(15);

        sendButton = new JButton("Отправить");
        sendButton.addActionListener((click) -> {
            if (passwordField.getText().length() == 0) {
                JOptionPane.showMessageDialog(this, "Введите пароль!", "Ошибка", JOptionPane.ERROR_MESSAGE, Tray.getIcon());
                return;
            }

            lastPassword = Hash.hash(passwordField.getText(), "SHA256");

            setVisible(false);
            try {
                client.setUsedSaved(false);
                client.setWaiting(true);
                client.sendPacket(new StartSharingPacket(client.getAesKey().encrypt(lastPassword.getBytes(StandardCharsets.UTF_8)), SystemInfo.getHWID()));
            } catch (Exception e) {
                MasterLogger.error(e);
            }
        });

        JPanel panel = new JPanel();
        panel.add(infoLabel);
        panel.add(passwordField);
        panel.add(sendButton);

        setResizable(false);
        setSize(200, 135);
        setContentPane(panel);
    }

}
