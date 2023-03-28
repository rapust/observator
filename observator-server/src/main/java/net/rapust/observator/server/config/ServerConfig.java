package net.rapust.observator.server.config;

import lombok.Getter;
import lombok.Setter;
import net.rapust.observator.commons.logger.MasterLogger;

import java.io.File;
import java.nio.file.Files;
import java.util.Properties;

@Getter
public class ServerConfig {

    private File file;
    private Properties properties;

    @Setter
    private String name = "Server";
    @Setter
    private int port = 1337;
    @Setter
    private int maxConnections = 10;

    public ServerConfig(File config) {

        try {
            file = config;
            properties = new Properties();
            properties.load(Files.newInputStream(file.toPath()));


            name = properties.getProperty("name", "Server");
            port = Integer.parseInt(properties.getProperty("port", "1337"));
            maxConnections = Integer.parseInt(properties.getProperty("max-connections", "10"));
        } catch (Exception e) {
            MasterLogger.error("Ошибка при сохранении конфига", e);
        }

    }

    public void save() {

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            properties = new Properties();

            properties.setProperty("name", name);
            properties.setProperty("port", String.valueOf(port));
            properties.setProperty("max-connections", String.valueOf(maxConnections));

            properties.save(Files.newOutputStream(file.toPath()), "Observator server properties");
        } catch (Exception e) {
            MasterLogger.error("Ошибка при сохранении конфига", e);
        }

    }

}
