package net.rapust.observator.client;

import lombok.Getter;
import lombok.Setter;
import net.rapust.observator.client.config.ClientConfig;
import net.rapust.observator.client.gui.MainGUI;
import net.rapust.observator.client.listener.*;
import net.rapust.observator.commons.crypt.RSAKeyPair;
import net.rapust.observator.commons.logger.MasterLogger;
import net.rapust.observator.commons.util.Folder;
import net.rapust.observator.commons.util.Resources;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;

@Getter
public class ClientAccessor implements Runnable {

    @Getter
    private static ClientAccessor instance;

    @Setter
    private ClientImpl client;

    private final File workingFolder;
    private final ClientConfig config;
    private RSAKeyPair keyPair;

    @Setter
    private MainGUI mainGUI;

    private SendingRunnable sendingRunnable;

    @Getter
    private final HashMap<String, String> verifiedServers = new HashMap<>();

    public ClientAccessor() {
        instance = this;

        File preWorkingFolder = Folder.getWorkingFolder();
        workingFolder = new File(preWorkingFolder, "client");
        workingFolder.mkdirs();

        File configFile = new File(workingFolder, "client.properties");
        Resources.saveResource(configFile);

        config = new ClientConfig(configFile);
        try {
            keyPair = RSAKeyPair.create(workingFolder);
        } catch (Exception e) {
            MasterLogger.error("Ошибка при создании пары ключей", e);
            System.exit(100);
        }
    }

    public void connect(ClientImpl client) {
        this.client = client;

        client.registerListeners(
                new ClickListener(),
                new KeyListener(),
                new HelloListener(),
                new ScreenListener(),
                new ErrorListener()
        );

        client.setKeyPair(keyPair);

        Thread thread = new Thread(this, "ObservatorClient");
        thread.start();
    }

    public void startSending(String HWID) {
        if (client != null) {
            if (!verifiedServers.getOrDefault(client.getIp() + ":" + client.getPort(), "-").equals(HWID)) {
                return;
            }

            if (sendingRunnable != null) {
                sendingRunnable.stop();
            }

            sendingRunnable = new SendingRunnable();

            mainGUI.setVisible(true);
            mainGUI.update();
        }
    }

    public void stopSending() {
        if (sendingRunnable != null) {
            sendingRunnable.stop();
            sendingRunnable = null;

            mainGUI.setVisible(true);
            mainGUI.update();
        }
    }

    @Override
    public void run() {
        try {
            client.connect();
        } catch (Exception e) {
            MasterLogger.error("Ошибка в главном потоке протокол-клиента", e);
            mainGUI.setVisibility(true);

            if (e.getMessage().contains("Connection refused")) {
                JOptionPane.showMessageDialog(mainGUI.getSettingsGUI(), "Не удалось подключиться к серверу");
            } else {
                JOptionPane.showMessageDialog(mainGUI.getSettingsGUI(), "Ошибка в главном потоке протокол-клиента");
            }
        }
    }

}
