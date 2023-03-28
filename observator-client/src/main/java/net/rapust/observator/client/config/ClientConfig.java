package net.rapust.observator.client.config;

import lombok.Getter;
import lombok.Setter;
import net.rapust.observator.commons.logger.MasterLogger;

import java.io.File;
import java.nio.file.Files;
import java.util.Properties;

@Getter
public class ClientConfig {

    private File file;
    private Properties properties;

    @Setter
    private String name = "Client";
    @Setter
    private String password = "qwerty123";
    @Setter
    private String ip = "127.0.0.1";
    @Setter
    private int port = 1337;
    @Setter
    private int fps = 20;

    public ClientConfig(File config) {

        try {
            file = config;
            properties = new Properties();
            properties.load(Files.newInputStream(file.toPath()));

            name = properties.getProperty("name", "Client");
            password = properties.getProperty("password", "qwerty123");
            ip = properties.getProperty("ip", "127.0.0.1");
            port = Integer.parseInt(properties.getProperty("port", "1337"));
            fps = Integer.parseInt(properties.getProperty("fps", "20"));
        } catch (Exception e) {
            MasterLogger.error("Ошибка при загрузке конфига", e);
        }

    }

    public void save() {

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            properties = new Properties();

            properties.setProperty("name", name);
            properties.setProperty("password", password);
            properties.setProperty("ip", ip);
            properties.setProperty("port", String.valueOf(port));
            properties.setProperty("fps", String.valueOf(fps));

            properties.save(Files.newOutputStream(file.toPath()), "Observator client config");
        } catch (Exception e) {
            MasterLogger.error("Ошибка при сохранении конфига", e);
        }

    }

}
