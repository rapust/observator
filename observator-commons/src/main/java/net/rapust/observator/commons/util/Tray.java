package net.rapust.observator.commons.util;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.rapust.observator.commons.logger.MasterLogger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@UtilityClass
public class Tray {

    private TrayIcon trayIcon;

    @Getter
    private Icon icon;

    public void display(String caption, String text, TrayIcon.MessageType type) {
        try {
            trayIcon.displayMessage(caption, text, type);
        } catch (Exception e) {
            MasterLogger.error("Ошибка при выводе уведомления", e);
        }
    }

    public void init(String suffix) {
        try {
            String postSuffix = suffix.equals("client") ? "клиент" : "сервер";

            BufferedImage image = ImageIO.read(Resources.getResourceAsStream("icon-" + suffix + ".png"));

            icon = new ImageIcon(Image.resizeImage(image, 96, 96));

            SystemTray tray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(image, "Observator " + postSuffix);
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("Observator " + postSuffix);

            PopupMenu menu = new PopupMenu();
            MenuItem item = new MenuItem("Закрыть");
            item.addActionListener((click) -> {
                System.exit(0);
            });
            menu.add(item);

            trayIcon.setPopupMenu(menu);

            tray.add(trayIcon);
        } catch (Exception e) {
            MasterLogger.error("Ошибка при добавлении в трей", e);
        }
    }

}
