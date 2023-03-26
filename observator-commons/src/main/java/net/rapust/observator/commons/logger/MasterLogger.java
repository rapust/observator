package net.rapust.observator.commons.logger;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@UtilityClass
public class MasterLogger {

    private boolean log4j;

    private Logger logger;

    public void info(String message) {
        if (log4j) {
            logger.info(message);
        } else {
            System.out.println(format("info", message));
        }
    }

    public void warn(String message) {
        if (log4j) {
            logger.warn(message);
        } else {
            System.out.println(format("warn", message));
        }
    }

    public void error(Exception e) {
        if (log4j) {
            logger.error(e.getMessage(), e);
        } else {
            System.err.println(format("error", e.getMessage()));
            e.printStackTrace();
        }
    }

    public void error(String message) {
        error(message, true);
    }

    public void error(String message, boolean thrown) {
        if (log4j) {
            if (thrown) {
                logger.error(message, new Exception(message));
            } else {
                logger.error(message);
            }
        } else {
            System.err.println(format("error", message));
            if (thrown) {
                new Exception(message).printStackTrace();
            }
        }
    }

    public void error(String message, Exception e) {
        if (log4j) {
            logger.error(message, e);
        } else {
            System.err.println(format("error", message));
            e.printStackTrace();
        }
    }

    private String format(String level, String message) {
        DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");

        return "[" + timeFormat.format(new Date()) + "] [" + level.toUpperCase() + "] " + message;
    }

    static {
        try {
            logger = LogManager.getLogger(MasterLogger.class);
            log4j = true;

            info("Используем log4j.");
        } catch (NoClassDefFoundError ignored) {
            log4j = false;

            info("Не используем log4j.");
        }
    }

}
