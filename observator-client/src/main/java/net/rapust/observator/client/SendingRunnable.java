package net.rapust.observator.client;

import lombok.Getter;
import net.rapust.observator.commons.crypt.AESKey;
import net.rapust.observator.commons.logger.MasterLogger;
import net.rapust.observator.commons.util.Image;
import net.rapust.observator.commons.util.Tray;
import net.rapust.observator.protocol.packet.impl.ScreenChangePacket;
import net.rapust.observator.protocol.packet.impl.ScreenPacket;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SendingRunnable implements Runnable {

    @Getter
    private final Thread thread;

    private boolean running = true;

    private final String ipPort;

    public SendingRunnable() {
        ClientImpl client = ClientAccessor.getInstance().getClient();
        ipPort = client.getIp() + ":" + client.getPort();

        Tray.display("Observator", "Начата отправка экрана на " + ipPort + ".", TrayIcon.MessageType.INFO);

        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        running = false;

        Tray.display("Observator", "Остановлена отправка экрана на " + ipPort + ".", TrayIcon.MessageType.INFO);
    }

    @Override
    public void run() {
        ClientImpl client = ClientAccessor.getInstance().getClient();

        if (client.getAesKey() == null) {
            try {
                Thread.sleep(1500L);
            } catch (InterruptedException e) {
                MasterLogger.error(e);
            }
        }

        AESKey key = client.getAesKey();

        int delay = 1000 / ClientAccessor.getInstance().getConfig().getFps();

        try {
            BufferedImage image = Image.takeScreenShotImage();
            Image.setLastImage(image);

            client.write(new ScreenPacket(key.encrypt(Image.toByteArray(image, "JPG"))));
        } catch (Exception e) {
            MasterLogger.error(e);
            return;
        }

        while (running) {
            try {
                BufferedImage image = Image.takeScreenShotImage();
                byte[] imageBytes = Image.toByteArray(image, "JPG");

                byte[] changes = Image.getChanges(image, imageBytes);

                if (changes == null) {
                    client.write(new ScreenPacket(key.encrypt(imageBytes)));
                } else {
                    client.write(new ScreenChangePacket(key.encrypt(changes)));
                }

                Thread.sleep(delay);
            } catch (Exception e) {
                MasterLogger.error("Ошибка при отправке экрана", e);
                stop();
            }
        }
    }

}
