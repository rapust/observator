package net.rapust.observator.server;

import lombok.Getter;
import lombok.Setter;
import net.rapust.observator.commons.crypt.RSAKeyPair;
import net.rapust.observator.commons.logger.MasterLogger;
import net.rapust.observator.commons.util.Folder;
import net.rapust.observator.commons.util.Resources;
import net.rapust.observator.server.client.ClientManager;
import net.rapust.observator.server.config.ServerConfig;
import net.rapust.observator.server.gui.MainGUI;
import net.rapust.observator.server.listener.ErrorListener;
import net.rapust.observator.server.listener.HelloListener;
import net.rapust.observator.server.listener.KeyListener;

import javax.swing.*;
import java.io.File;

@Getter
public class ServerAccessor implements Runnable {

    @Getter
    private static ServerAccessor instance;

    @Setter
    private ServerImpl server;

    private final File workingFolder;
    private final ServerConfig config;
    private final ClientManager clientManager;

    private RSAKeyPair keyPair;

    @Setter
    private MainGUI mainGUI;

    public ServerAccessor() {
        instance = this;

        File preWorkingFolder = Folder.getWorkingFolder();
        workingFolder = new File(preWorkingFolder, "server");
        workingFolder.mkdirs();

        File configFile = new File(workingFolder, "server.properties");
        Resources.saveResource(configFile);

        config = new ServerConfig(configFile);
        clientManager = new ClientManager();

        try {
            keyPair = RSAKeyPair.create(workingFolder);
        } catch (Exception e) {
            MasterLogger.error("Ошибка при создании пары ключей", e);
            System.exit(100);
        }
    }

    public void connect(ServerImpl server) {
        mainGUI.update();

        this.server = server;

        server.registerListeners(
                new KeyListener(),
                new HelloListener(),
                new ErrorListener()
        );

        server.setKeyPair(keyPair);

        Thread thread = new Thread(this, "ObservatorServer");
        thread.start();
    }

    @Override
    public void run() {
        try {
            server.start();
        } catch (Exception e) {
            MasterLogger.error("Ошибка в главном потоке протокол-сервера", e);
            mainGUI.setVisibility(true);

            JOptionPane.showMessageDialog(mainGUI.getSettingsGUI(), "Ошибка в главном потоке протокол-сервера");
        }
    }

}