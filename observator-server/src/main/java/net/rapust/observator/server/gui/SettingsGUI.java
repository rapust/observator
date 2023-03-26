package net.rapust.observator.server.gui;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.rapust.observator.commons.util.Tray;
import net.rapust.observator.server.ServerAccessor;
import net.rapust.observator.server.ServerImpl;
import net.rapust.observator.server.config.ServerConfig;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Data
public class SettingsGUI extends JFrame {

    private final ServerAccessor serverAccessor;

    private final JLabel nameLabel;
    private final JTextField nameField;

    private final JLabel portLabel;
    private final JTextField portField;

    private final JLabel maxConnectionsLabel;
    private final JTextField maxConnectionsField;

    private final JButton startButton;

    public SettingsGUI(ServerAccessor serverAccessor) {
        super("Настройки");

        this.serverAccessor = serverAccessor;
        ServerConfig config = serverAccessor.getConfig();

        nameLabel = new JLabel("Имя сервера");
        nameField = new JTextField(config.getName(), 15);

        portLabel = new JLabel("Порт сервера");
        portField = new JTextField(String.valueOf(config.getPort()), 15);

        maxConnectionsLabel = new JLabel("Макс. подключений");
        maxConnectionsField = new JTextField(String.valueOf(config.getMaxConnections()), 15);

        startButton = new JButton("Запустить сервер");
        startButton.addActionListener(new ClickListener(this));

        JPanel panel = new JPanel();
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(portLabel);
        panel.add(portField);
        panel.add(maxConnectionsLabel);
        panel.add(maxConnectionsField);
        panel.add(startButton);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(200, 250);
        setContentPane(panel);
    }

    @AllArgsConstructor
    private static class ClickListener implements ActionListener {

        private SettingsGUI gui;

        @Override
        public void actionPerformed(ActionEvent e) {
            String name = gui.getNameField().getText().trim();

            if (name.length() == 0) {
                JOptionPane.showMessageDialog(gui, "Введите имя сервера!", "Ошибка", JOptionPane.ERROR_MESSAGE, Tray.getIcon());
                return;
            }

            String portS = gui.getPortField().getText();

            int port;
            try {
                port = Integer.parseInt(portS);
            } catch (Exception ignored) {
                JOptionPane.showMessageDialog(gui, "Некорректное значение порта!", "Ошибка", JOptionPane.ERROR_MESSAGE, Tray.getIcon());
                return;
            }

            String maxConnectionsS = gui.getMaxConnectionsField().getText();

            int maxConnections;
            try {
                maxConnections = Integer.parseInt(maxConnectionsS);
            } catch (Exception ignored) {
                JOptionPane.showMessageDialog(gui, "Некорректное значение макс. подключений!", "Ошибка", JOptionPane.ERROR_MESSAGE, Tray.getIcon());
                return;
            }

            ServerConfig config = ServerAccessor.getInstance().getConfig();

            config.setName(name);
            config.setPort(port);
            config.setMaxConnections(maxConnections);

            config.save();

            ServerImpl server = new ServerImpl();
            ServerAccessor.getInstance().connect(server);

            gui.setVisible(false);

            ServerAccessor.getInstance().getMainGUI().update();
        }

    }

}
