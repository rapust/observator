package net.rapust.observator.server.config;

import lombok.Getter;
import lombok.Setter;
import net.rapust.observator.commons.logger.MasterLogger;
import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;

@Getter
public class ServerConfig {

    private File file;
    private FileConfiguration configuration;

    @Setter
    private String name = "Сервер";
    @Setter
    private int port = 1337;
    @Setter
    private int maxConnections = 10;

    public ServerConfig(File config) {

        try {
            file = config;
            configuration = YamlConfiguration.loadConfiguration(config);
            name = configuration.getString("name", "Сервер");
            port = configuration.getInt("port", 1337);
            maxConnections = configuration.getInt("max-connections", 10);
        } catch (Exception e) {
            MasterLogger.error("Ошибка при сохранении конфига", e);
        }

    }

    public void save() {

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            if (configuration == null) {
                configuration = new YamlConfiguration();
            }

            configuration.set("name", name);
            configuration.set("port", port);
            configuration.set("max-connections", maxConnections);

            configuration.save(file);
        } catch (Exception e) {
            MasterLogger.error("Ошибка при сохранении конфига", e);
        }

    }

}
