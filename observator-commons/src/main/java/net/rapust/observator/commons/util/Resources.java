package net.rapust.observator.commons.util;

import lombok.experimental.UtilityClass;
import net.rapust.observator.commons.logger.MasterLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@UtilityClass
public class Resources {

    public void saveResource(File file) {
        saveResource(file.getName(), file, true);
    }

    public void saveResource(String name, File file) {
        saveResource(name, file, true);
    }

    public void saveResource(File file, boolean replace) {
        saveResource(file.getName(), file, replace);
    }

    public void saveResource(String name, File file, boolean replace) {
        try {
            if (file.createNewFile()) {
                if (replace) {
                    Files.copy(getResourceAsStream(name), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            MasterLogger.error("Ошибка при сохранении файла " + name, e);
        }
    }

    public InputStream getResourceAsStream(String name) {
        return Resources.class.getClassLoader().getResourceAsStream(name);
    }

    public String getCodeSourcePath() {
        return Resources.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    public String getPath() {
        return getCodeSourcePath().substring(0, getCodeSourcePath().lastIndexOf("/"));
    }

}
