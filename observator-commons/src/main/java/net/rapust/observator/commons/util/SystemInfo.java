package net.rapust.observator.commons.util;

import lombok.experimental.UtilityClass;

import javax.tools.Tool;
import java.awt.*;

@UtilityClass
public class SystemInfo {

    public String getComputerName() {
        return System.getenv("COMPUTERNAME");
    }

    public String getProcessor() {
        return System.getenv("PROCESSOR_IDENTIFIER");
    }

    public String getProcessorLevel() {
        return System.getenv("PROCESSOR_LEVEL");
    }

    public String getOSName() {
        return System.getProperty("os.name");
    }

    public String getOSArch() {
        return System.getProperty("os.arch");
    }

    public String getOSVersion() {
        return System.getProperty("os.version");
    }

    public String getUsername() {
        return System.getProperty("user.name");
    }

    public Dimension getScreenInfo() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    public String getHWID() {
        return Hash.hash(getComputerName() + getProcessor() + getProcessorLevel() +
                getOSName() + getOSArch() + getOSArch() + getOSVersion() + getUsername(), "MD5");
    }

}
