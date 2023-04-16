package net.rapust.observator.client.gui;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.rapust.observator.client.ClientAccessor;
import net.rapust.observator.client.ClientImpl;
import net.rapust.observator.client.config.ClientConfig;
import net.rapust.observator.commons.util.Tray;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Data
public class SettingsGUI extends JFrame {

    private final ClientAccessor clientAccessor;

    private final JLabel nameLabel;
    private final JTextField nameField;

    private final JLabel passwordLabel;
    private final JPasswordField passwordField;

    private final JLabel ipPortLabel;
    private final JTextField ipPortField;

    private final JLabel fpsLabel;
    private final JTextField fpsField;

    private final JButton connectButton;

    protected SettingsGUI(ClientAccessor clientAccessor) {
        super("Настройки");

        this.clientAccessor = clientAccessor;
        ClientConfig config = clientAccessor.getConfig();

        nameLabel = new JLabel("Имя клиента");
        nameField = new JTextField(config.getName(), 15);

        passwordLabel = new JLabel("Пароль клиента");
        passwordField = new JPasswordField(config.getPassword(), 15);

        ipPortLabel = new JLabel("Данные сервера");
        ipPortField = new JTextField(config.getIp() + ":" + config.getPort(), 15);

        fpsLabel = new JLabel("ФПС видео");
        fpsField = new JTextField(String.valueOf(config.getFps()), 15);

        connectButton = new JButton("Подключиться");
        connectButton.addActionListener(new ClickListener(this));

        JPanel panel = new JPanel();
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(ipPortLabel);
        panel.add(ipPortField);
        panel.add(fpsLabel);
        panel.add(fpsField);
        panel.add(connectButton);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(200, 300);
        setContentPane(panel);
    }

    @AllArgsConstructor
    private static class ClickListener implements ActionListener {

        private SettingsGUI gui;

        @Override
        public void actionPerformed(ActionEvent e) {
            String name = gui.getNameField().getText().trim();
            String password = gui.getPasswordField().getText();

            if (name.length() == 0) {
                JOptionPane.showMessageDialog(gui, "Введите имя клиента!", "Ошибка", JOptionPane.ERROR_MESSAGE, Tray.getIcon());
                return;
            }

            int len = password.length();

            if (password.length() == 0) {
                JOptionPane.showMessageDialog(gui, "Введите пароль клиента!", "Ошибка", JOptionPane.ERROR_MESSAGE, Tray.getIcon());
                return;
            }

            if (len < 5 || len > 32) {
                JOptionPane.showMessageDialog(gui, "Пароль должен быть от 5 до 32 символов!", "Ошибка", JOptionPane.ERROR_MESSAGE, Tray.getIcon());
                return;
            }

            if (password.startsWith(" ") || password.endsWith(" ")) {
                JOptionPane.showMessageDialog(gui, "Пароль не должен начинаться или заканчиваться на пробел!", "Ошибка", JOptionPane.ERROR_MESSAGE, Tray.getIcon());
                return;
            }

            String ipPort = gui.getIpPortField().getText();

            boolean isOkay = ipPort.length() > 0;

            String ip = null;
            Integer port = null;

            try {
                String[] values = ipPort.split(":");
                if (values.length != 2) {
                    isOkay = false;
                } else {
                    ip = values[0];
                    port = Integer.parseInt(values[1]);
                }
            } catch (Exception ignored) {
                isOkay = false;
            }

            if (!isOkay) {
                JOptionPane.showMessageDialog(gui, "Некорректное значение данных сервера!", "Ошибка", JOptionPane.ERROR_MESSAGE, Tray.getIcon());
                return;
            }

            int fps;
            try {
                fps = Integer.parseInt(gui.getFpsField().getText());
            } catch (Exception ignored) {
                JOptionPane.showMessageDialog(gui, "Некорректное значение ФПС!", "Ошибка", JOptionPane.ERROR_MESSAGE, Tray.getIcon());
                return;
            }

            ClientConfig config = ClientAccessor.getInstance().getConfig();

            config.setName(name);
            config.setPassword(password);
            config.setIp(ip);
            config.setPort(port);
            config.setFps(fps);

            config.save();

            ClientImpl client = new ClientImpl();
            ClientAccessor.getInstance().connect(client);

            gui.setVisible(false);
        }

    }

}
