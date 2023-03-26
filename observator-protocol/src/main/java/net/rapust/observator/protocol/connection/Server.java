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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public abstract class Server {

    private boolean running = false;

    private final String name;
    private final int port;

    private ServerSocket serverSocket;

    private RSAKeyPair keyPair = new RSAKeyPair();

    private HashMap<String, ClientHandler> clients = new HashMap<>();
    private final List<ListenerRegistry> listeners = new ArrayList<>();

    public Server(String name, int port) {
        this.name = name;
        this.port = port;
    }

    public abstract void onPacket(ClientHandler client, Packet packet);

    public abstract void onConnect(ClientHandler client);

    public abstract void onDisconnect(ClientHandler client);

    public void start() throws Exception {
        MasterLogger.info("[СЕРВЕР-" + name + "] Запускаем сервер на порту " + port + ".");

        running = true;
        serverSocket = new ServerSocket(port);

        Async.run(() -> {
            while (running) {
                try {
                    ClientHandler clientHandler = new ClientHandler(this, serverSocket.accept());

                    clients.put(clientHandler.getIP(), clientHandler);
                    clientHandler.start();
                } catch (Exception e) {
                    if (!e.getMessage().contains("Socket closed")) {
                        MasterLogger.error("Ошибка при подключении", e);
                    }
                }
            }
        });
    }

    public void stop() throws Exception {
        MasterLogger.info("[СЕРВЕР-" + name + "] Останавливаем сервер.");

        running = false;
        new HashMap<>(clients).forEach((name, client) -> {
            try {
                client.stop();
            } catch (IOException e) {
                MasterLogger.error("Ошибка при остановке клиента " + client.getIP(), e);
            }
        });
        serverSocket.close();
    }

    @Data
    public static class ClientHandler implements Runnable {

        private final Server server;
        private final Socket clientSocket;

        private DataOutputStream out;
        private DataInputStream in;

        private RSAPublicKey publicKey = new RSAPublicKey();

        private boolean disconnected = false;

        public ClientHandler(Server server, Socket socket) {
            this.server = server;
            this.clientSocket = socket;
        }

        public void start() throws IOException {
            MasterLogger.info("[СЕРВЕР-" + server.getName() + "] Новое подключение от " + getIP() + ".");

            out = new DataOutputStream(this.clientSocket.getOutputStream());
            in = new DataInputStream(this.clientSocket.getInputStream());

            read();

            write(new HelloPacket(this.server.getName(), SystemInfo.getHWID(), 0, 0));

            server.onConnect(this);
        }

        public void stop() throws IOException {
            MasterLogger.info("[СЕРВЕР-" + server.getName() + "] Отключаем клиента " + getIP() + ".");
            server.getClients().remove(getIP());

            in.close();
            out.close();
            clientSocket.close();

            if (!disconnected) {
                server.onDisconnect(this);
            }
        }

        public String getIP() {
            return this.clientSocket.getInetAddress().getHostAddress() + ":" + this.clientSocket.getPort();
        }

        public void write(Packet packet) throws IOException {
            write(packet, publicKey);
        }

        public void write(Packet packet, RSAPublicKey publicKey) throws IOException {
            Buffer buffer = Buffer.empty();
            buffer.writeString(packet.hash());

            packet.write(buffer, publicKey);
            buffer.writeDescBytes();

            out.write(buffer.toArray());
        }

        public void read() {
            new Thread(this).start();
        }

        @Override
        public void run() {
            try {
                ByteContainer container = new ByteContainer();

                while (server.running) {
                    byte b = in.readByte();
                    container.add(b);

                    if (container.check()) {
                        Packet packet = container.toPacket(server.getKeyPair());

                        if (packet == null) {
                            MasterLogger.error("Packet is null!");
                        } else {
                            try {
                                server.runListeners(packet, this);
                                server.onPacket(this, packet);
                            } catch (Exception e) {
                                MasterLogger.error(e);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                server.getClients().remove(getIP());

                MasterLogger.info("[СЕРВЕР-" + server.getName() + "] Клиент " + getIP() + " отключился.");
                server.onDisconnect(this);

                try {
                    disconnected = true;
                    stop();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private void runListeners(Packet packet, ClientHandler handler) {
        Async.run(() -> {
            for (ListenerRegistry registry : listeners) {
                if (registry.getPacket().isInstance(packet)) {
                    registry.invoke(packet, handler);
                }
            }
        });
    }

    public void registerListeners(Listener... listeners) {
        for (Listener l : listeners) {
            Method[] methods = l.getClass().getMethods();
            for (Method m : methods) {
                Class<?>[] params = m.getParameterTypes();
                if (params.length == 2 && params[1] == ClientHandler.class) {
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
