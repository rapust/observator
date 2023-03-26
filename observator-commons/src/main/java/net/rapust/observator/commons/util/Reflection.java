package net.rapust.observator.commons.util;

import lombok.experimental.UtilityClass;
import net.rapust.observator.commons.logger.MasterLogger;

import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class Reflection {

    public List<Class<?>> loadClasses(ClassLoader loader, String pckg) throws Exception {
        pckg = pckg.replace(".", "/");

        List<Class<?>> classes = new ArrayList<>();

        URI uri = loader.getResource(pckg).toURI();

        Path path;
        try {
            path = FileSystems.getFileSystem(uri).getPath(pckg);
        } catch (Exception exception) {
            path = FileSystems.newFileSystem(uri, Collections.emptyMap()).getPath(pckg);
        }

        String pkg = pckg.replace("/", ".");

        Files.walk(path).forEach(file -> {
            String name = file.getFileName().toString();

            if (name.endsWith(".class") && !name.contains("$")) {
                try {
                    classes.add(Class.forName(pkg + "." + name.replace(".class", "")));
                } catch (ClassNotFoundException e) {
                    MasterLogger.warn(e.getMessage());
                }
            }
        });

        return classes;
    }
    
}
