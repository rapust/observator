package net.rapust.observator.client.listener;

import net.rapust.observator.commons.logger.MasterLogger;
import net.rapust.observator.protocol.connection.Client;
import net.rapust.observator.protocol.listener.Listener;
import net.rapust.observator.protocol.packet.impl.KeyboardClickPacket;
import net.rapust.observator.protocol.packet.impl.MouseClickPacket;
import net.rapust.observator.protocol.packet.impl.MouseMovePacket;

import java.awt.*;
import java.awt.event.InputEvent;

public class ClickListener implements Listener {

    private static Robot robot;

    public void onMouseClick(MouseClickPacket packet, Client client) {
        robot.mouseMove(packet.getX(), packet.getY());

        int mask = InputEvent.getMaskForButton(packet.getButton());

        if (packet.getAction() == MouseClickPacket.ClickAction.PRESSED) {
            robot.mousePress(mask);
        } else if (packet.getAction() == MouseClickPacket.ClickAction.RELEASED) {
            robot.mouseRelease(mask);
        }
    }

    public void onMouseMove(MouseMovePacket packet, Client client) {
        robot.mouseMove(packet.getX(), packet.getY());
    }

    public void onKeyboardClick(KeyboardClickPacket packet, Client client) {
        int button = packet.getButton();

        if (packet.getAction() == KeyboardClickPacket.ClickAction.PRESSED) {
            robot.keyPress(button);
        } else if (packet.getAction() == KeyboardClickPacket.ClickAction.RELEASED) {
            robot.keyRelease(button);
        }
    }

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            MasterLogger.error("Ошибка при инициализации робота", e);
        }
    }

}
