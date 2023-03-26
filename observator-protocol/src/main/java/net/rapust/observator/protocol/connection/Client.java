package net.rapust.observator.protocol.connection;

import lombok.Data;
import net.rapust.observator.commons.crypt.RSAKeyPair;
import net.rapust.observator.commons.crypt.RSAPublicKey;
import net.rapust.observator.commons.logger.MasterLogger;
import net.rapust.observator.commons.util.Async;
import net.rapust.observator.commons.util.SystemInfo;
import net.rapust.observator.protocol.buffer.Buffer;
import net.rapust.observator.protocol.buffer.ByteContainer;
import net.rapust.observator.protocol.listener.Listener;
import net.rapust.observator.protocol.listener.ListenerRegistry;
import net.rapust.observator.protocol.packet.Packet;
import net.rapust.observator.protocol.packet.impl.HelloPacket;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Data
public abstract class Client {

    private final String name;
    private final String ip;
    private final int port;

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    private RSAKeyPair keyPair = new RSAKeyPair();
    private RSAPublicKey publicKey = new RSAPublicKey();

    private final List<ListenerRegistry> listeners = new ArrayList<>();

    public Client(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public abstract void onPacket(Packet packet);

    public abstract void onConnect();

    public abstract void onDisconnect();

    public void connect() throws Exception {
        MasterLogger.info("[КЛИЕНТ-" + name + "] Подключаемся к серверу " + ip + ":" + port + ".");

        socket = new Socket(ip, port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

        read();

        Dimension screen = SystemInfo.getScreenInfo();
        write(new HelloPacket(this.getName(), SystemInfo.getHWID(), (int) screen.getWidth(), (int) screen.getHeight()));

        onConnect();
    }

    public void disconnect() throws Exception {
        MasterLogger.info("[КЛИЕНТ-" + name + "] Отключаемся от сервера.");

        in.close();
        out.close();
        socket.close();

        onDisconnect();
    }

    public void write(Packet packet) throws IOException {
        write(packet, publicKey);
    }

    public void write(Packet packet, RSAPublicKey publicKey) throws IOException {
        Buffer buffer = Buffer.empty();
        buffer.writeInt(packet.getId());

        packet.write(buffer, publicKey);
        buffer.writeDescBytes();

        out.write(buffer.toArray());
    }

    public void read() {
        Async.run(() -> {
            try {
                ByteContainer container = new ByteContainer();

                while (true) {
                    byte b = in.readByte();
                    container.add(b);

                    if (container.check()) {
                        Packet packet = container.toPacket(keyPair);

                        if (packet == null) {
                            MasterLogger.error("Packet is null!");
                        } else {
                            try {
                                this.runListeners(packet);
                                this.onPacket(packet);
                            } catch (Exception e) {
                                MasterLogger.error(e);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                MasterLogger.info("[КЛИЕНТ-" + name + "] Клиент отключён от сервера.");
                onDisconnect();
            }
        });
    }

    private void runListeners(Packet packet) {
        Async.run(() -> {
            for (ListenerRegistry registry : listeners) {
                if (registry.getPacket().isInstance(packet)) {
                    registry.invoke(packet, this);
                }
            }
        });
    }

    public void registerListeners(Listener... listeners) {
        for (Listener l : listeners) {
            Method[] methods = l.getClass().getMethods();
            for (Method m : methods) {
                Class<?>[] params = m.getParameterTypes();
                if (params.length == 2 && params[1] == Client.class) {
                    this.listeners.add(new ListenerRegistry(
                            params[0],
                            l,
                            m
                    ));
                }
            }
        }
    }

}
