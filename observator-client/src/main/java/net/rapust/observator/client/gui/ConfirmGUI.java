package net.rapust.observator.client.gui;

import lombok.Getter;
import net.rapust.observator.client.ClientAccessor;
import net.rapust.observator.client.ClientImpl;
import net.rapust.observator.commons.logger.MasterLogger;
import net.rapust.observator.commons.util.Tray;
import net.rapust.observator.protocol.packet.impl.ErrorPacket;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ConfirmGUI extends JFrame {

    @Getter
    private static final List<ConfirmGUI> guis = new ArrayList<>();

    private final JLabel title;
    private final JLabel hwid;
    private final JButton yesButton;
    private final JButton noButton;

    public ConfirmGUI(String ipPort, String name, String HWID) {
        super("Подтверждение");

        guis.add(this);

        ClientImpl client = ClientAccessor.getInstance().getClient();

        title = new JLabel("Получен HWID от " + name + " (" + ipPort + ")");
        hwid = new JLabel(HWID);

        yesButton = new JButton("Подтверждаю");
        yesButton.addActionListener((click) -> {
            ClientAccessor.getInstance().getVerifiedServers().put(ipPort, HWID);

            setVisible(false);

            ClientAccessor.getInstance().getMainGUI().setVisible(true);
            ClientAccessor.getInstance().getMainGUI().update();

            JOptionPane.showMessageDialog(ClientAccessor.getInstance().getMainGUI(), "HWID от " + ipPort + " подтвержден", "Успешно", JOptionPane.INFORMATION_MESSAGE, Tray.getIcon());

        });

        noButton = new JButton("Отклоняю");
        noButton.addActionListener((click) -> {
            setVisible(false);

            try {
                client.write(new ErrorPacket(ErrorPacket.ErrorType.UNACCEPTED_HWID));
                client.disconnect();
            } catch (Exception e) {
                MasterLogger.error(e);
            }

            JOptionPane.showMessageDialog(ClientAccessor.getInstance().getMainGUI(), "HWID от " + ipPort + " отклонён", "Успешно", JOptionPane.INFORMATION_MESSAGE, Tray.getIcon());
        });

        JPanel panel = new JPanel();
        panel.add(title);
        panel.add(hwid);
        panel.add(yesButton);
        panel.add(noButton);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(340, 130);
        setContentPane(panel);
    }

}
