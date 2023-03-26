package net.rapust.observator.commons.util;

import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class Folder {

    public static File getWorkingFolder() {
        File userFolder = new File(System.getProperty("user.home"));
        File workingFolder = new File(userFolder, "observator");

        if (!workingFolder.exists()) {
            workingFolder.mkdirs();
        }

        return workingFolder;
    }

}
