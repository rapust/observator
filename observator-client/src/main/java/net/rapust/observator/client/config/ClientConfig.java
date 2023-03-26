package net.rapust.observator.client.config;

import lombok.Getter;
import lombok.Setter;
import net.rapust.observator.commons.logger.MasterLogger;
import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;

@Getter
public class ClientConfig {

    private File file;
    private FileConfiguration configuration;

    @Setter
    private String name = "Клиент";
    @Setter
    private String password = "Пароль";
    @Setter
    private String ip = "127.0.0.1";
    @Setter
    private int port = 1337;
    @Setter
    private int fps = 20;

    public ClientConfig(File config) {

        try {
            file = config;
            configuration = YamlConfiguration.loadConfiguration(config);
            name = configuration.getString("name", "Клиент");
            password = configuration.getString("password", "Пароль");
            ip = configuration.getString("ip", "127.0.0.1");
            port = configuration.getInt("port", 1337);
            fps = configuration.getInt("fps", 20);
        } catch (Exception e) {
            MasterLogger.error("Ошибка при загрузке конфига", e);
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
            configuration.set("password", password);
            configuration.set("ip", ip);
            configuration.set("port", port);
            configuration.set("fps", fps);

            configuration.save(file);
        } catch (Exception e) {
            MasterLogger.error("Ошибка при сохранении конфига", e);
        }

    }

}
