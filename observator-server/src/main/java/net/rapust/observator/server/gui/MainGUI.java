package net.rapust.observator.server.gui;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.rapust.observator.commons.logger.MasterLogger;
import net.rapust.observator.commons.util.SystemInfo;
import net.rapust.observator.commons.util.Tray;
import net.rapust.observator.protocol.packet.impl.StartSharingPacket;
import net.rapust.observator.server.ServerAccessor;
import net.rapust.observator.server.ServerImpl;
import net.rapust.observator.server.client.ConnectedClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class MainGUI extends JFrame {

    private final ServerAccessor serverAccessor;
    private final SettingsGUI settingsGUI;

    private final JLabel statusLabel;

    private final JLabel HWIDLabel;

    private final JButton stopButton;

    private final JLabel clientsLabel;
    private final JTable clientsTable;

    private final HashMap<Integer, ConnectedClient> clients = new HashMap<>();

    public MainGUI(ServerAccessor serverAccessor) {
        super("Observator сервер");

        this.serverAccessor = serverAccessor;
        serverAccessor.setMainGUI(this);

        settingsGUI = new SettingsGUI(serverAccessor);

        statusLabel = new JLabel("                              " +
                "Статус: Отдых" +
                "                              ");

        HWIDLabel = new JLabel("HWID: " + SystemInfo.getHWID());

        stopButton = new JButton("               " +
                "Остановить сервер" +
                "              ");

        stopButton.addActionListener((click) -> {
            try {
                if (ServerAccessor.getInstance().getServer() == null) {
                    JOptionPane.showMessageDialog(this, "Сервер и так не запущен!", "Ошибка", JOptionPane.ERROR_MESSAGE, Tray.getIcon());
                    return;
                }

                setVisibility(true);
                serverAccessor.getServer().stop();
                serverAccessor.setServer(null);
                update();
            } catch (Exception e) {
                MasterLogger.error(e);
            }
        });

        clientsLabel = new JLabel("                " +
                "Список подключённых клиентов:" +
                "                ");

        clientsTable = new JTable();
        clientsTable.addMouseListener(new ClickListener(this));

        JPanel panel = new JPanel();
        panel.add(statusLabel);
        panel.add(HWIDLabel);
        panel.add(stopButton);
        panel.add(clientsLabel);
        panel.add(clientsTable);
        panel.add(new JScrollPane(clientsTable), BorderLayout.CENTER);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(500, 575);
        setContentPane(panel);

        update();
    }

    public void setVisibility(boolean visibility) {
        this.setVisible(visibility);
        settingsGUI.setVisible(visibility);
    }

    public void update() {
        String status = "                              " +
                "Статус: ";

        ServerImpl server = ServerAccessor.getInstance().getServer();
        if (server == null) {
            status += "Отдых" +
                    "                              ";
        } else {
            status += "Запущен на порту " + server.getPort() +
                    "                              ";
        }

        statusLabel.setText(status);

        DefaultTableModel tableModel = new DefaultTableModel() {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

        };

        tableModel.addColumn("Имя");
        tableModel.addColumn("Адрес");
        tableModel.addColumn("HWID");

        clients.clear();
        AtomicInteger i = new AtomicInteger(-1);

        ServerAccessor.getInstance().getClientManager().getClients().forEach(client -> {
            tableModel.addRow(new Object[]{client.getName(), client.getIP(), client.getHWID()});
            clients.put(i.incrementAndGet(), client);
        });

        clientsTable.setModel(tableModel);

        TableColumnModel model = clientsTable.getColumnModel();
        model.getColumn(0).setMaxWidth(80);
        model.getColumn(1).setMaxWidth(200);
    }

    @AllArgsConstructor
    private static class ClickListener extends MouseAdapter {

        private final MainGUI gui;

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            JTable table = (JTable) mouseEvent.getSource();

            Point point = mouseEvent.getPoint();
            int row = table.rowAtPoint(point);

            if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                ConnectedClient client = gui.getClients().get(row);

                if (client != null) {
                    String savedPassword = client.getSavedPassword();
                    if (savedPassword == null) {
                        PasswordGUI oldGui = new PasswordGUI(ServerAccessor.getInstance(), client);
                        client.setPasswordGUI(oldGui);
                        oldGui.setVisible(true);
                    } else {
                        try {
                            client.setUsedSaved(true);
                            client.setWaiting(true);
                            client.sendPacket(new StartSharingPacket(client.getAesKey().encrypt(savedPassword.getBytes(StandardCharsets.UTF_8)), SystemInfo.getHWID()));
                        } catch (Exception e) {
                            MasterLogger.error(e);
                        }
                    }
                }
            }
        }

    }

}
