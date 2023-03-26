package net.rapust.observator.server.gui;

import lombok.AllArgsConstructor;
import net.rapust.observator.commons.util.Bytes;
import net.rapust.observator.commons.util.Image;
import net.rapust.observator.protocol.packet.impl.KeyboardClickPacket;
import net.rapust.observator.protocol.packet.impl.MouseClickPacket;
import net.rapust.observator.protocol.packet.impl.MouseMovePacket;
import net.rapust.observator.protocol.packet.impl.StopSharingPacket;
import net.rapust.observator.server.client.ConnectedClient;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ClientViewerGUI extends JFrame {

    private final ConnectedClient client;

    private final JLabel label;

    private BufferedImage lastImage;

    public ClientViewerGUI(ConnectedClient client) {
        super("Экран: " + client.getName() + " (" + client.getIP() + ")");

        this.client = client;

        this.label = new JLabel();

        JPanel panel = new JPanel();
        panel.add(label);

        MouseListener listener = new MouseListener(client, this);

        addMouseListener(listener);
        addMouseMotionListener(listener);
        addMouseWheelListener(listener);

        addKeyListener(new KeyboardListener(client));

        setSize(client.getScreenWidth() / 2, client.getScreenHeight() / 2);
        setContentPane(panel);
    }

    @Override
    public void dispose() {
        client.sendPacket(new StopSharingPacket());
        client.setWaiting(false);
        super.dispose();
    }

    public void applyScreenShot(BufferedImage image) {
        lastImage = image;

        int height = getHeight() - 34;

        label.setIcon(new ImageIcon(Image.resizeImage(image, getWidth(), height)));
    }

    public void applyChanges(byte[] changes) {
        if (lastImage == null) {
            return;
        }

        int[] intChanges = Bytes.convert(changes);

        for (int i = 0; i < intChanges.length; i += 3) {
            int x = intChanges[i];
            int y = intChanges[i + 1];
            int rgb = intChanges[i + 2];

            lastImage.setRGB(x, y, rgb);
        }

        int height = getHeight() - 34;

        label.setIcon(new ImageIcon(Image.resizeImage(lastImage, getWidth(), height)));
    }

    private static class MouseListener extends MouseAdapter {

        private final ConnectedClient client;
        private final JFrame frame;

        private long lastMove = 0L;
        private final long DELAY = 250L;

        public MouseListener(ConnectedClient client, JFrame frame) {
            this.client = client;
            this.frame = frame;
        }

        @Override
        public void mousePressed(MouseEvent event) {
            int x = convertX(event.getX());
            int y = convertY(event.getY());

            client.sendPacket(new MouseClickPacket(x, y, event.getButton(), MouseClickPacket.ClickAction.PRESSED));
        }

        @Override
        public void mouseReleased(MouseEvent event) {
            int x = convertX(event.getX());
            int y = convertY(event.getY());

            client.sendPacket(new MouseClickPacket(x, y, event.getButton(), MouseClickPacket.ClickAction.RELEASED));
        }

        @Override
        public void mouseMoved(MouseEvent event) {
            if (lastMove + DELAY > System.currentTimeMillis()) {
                return;
            }

            lastMove = System.currentTimeMillis();

            int x = convertX(event.getX());
            int y = convertY(event.getY());

            client.sendPacket(new MouseMovePacket(x, y));
        }

        private int convertX(int x) {
            return (int) ((double) x * ((double) client.getScreenWidth() / (double) frame.getWidth()));
        }

        private int convertY(int y) {
            y = y - 34 + (y / 16);

            return (int) ((double) y * ((double) client.getScreenHeight() / (double) frame.getHeight()));
        }

    }

    @AllArgsConstructor
    private static class KeyboardListener extends KeyAdapter {

        private final ConnectedClient client;

        @Override
        public void keyPressed(KeyEvent event) {
            client.sendPacket(new KeyboardClickPacket(event.getKeyCode(), KeyboardClickPacket.ClickAction.PRESSED));
        }

        @Override
        public void keyReleased(KeyEvent event) {
            client.sendPacket(new KeyboardClickPacket(event.getKeyCode(), KeyboardClickPacket.ClickAction.RELEASED));
        }

    }

}
