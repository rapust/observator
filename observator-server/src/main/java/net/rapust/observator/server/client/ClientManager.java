package net.rapust.observator.server.client;

import lombok.Getter;
import net.rapust.observator.protocol.connection.Server;

import java.util.ArrayList;
import java.util.List;

public class ClientManager {

    @Getter
    private final List<ConnectedClient> clients;

    public ClientManager() {
        clients = new ArrayList<>();
    }

    public void addClient(ConnectedClient client) {
        clients.add(client);
    }

    public ConnectedClient getClientByHandler(Server.ClientHandler handler) {
        for (ConnectedClient client : clients) {
            if (client.getHandler() == handler) {
                return client;
            }
        }
        return null;
    }

    public ConnectedClient getClientByName(String name) {
        for (ConnectedClient client : clients) {
            if (client.getName().equalsIgnoreCase(name)) {
                return client;
            }
        }
        return null;
    }

    public ConnectedClient getClientByIP(String ip) {
        for (ConnectedClient client : clients) {
            if (client.getIP().equals(ip)) {
                return client;
            }
        }
        return null;
    }

    public ConnectedClient getClientByHWID(String hwid) {
        for (ConnectedClient client : clients) {
            if (client.getHWID().equals(hwid)) {
                return client;
            }
        }
        return null;
    }

    public void removeClient(ConnectedClient client) {
        clients.remove(client);
    }

}
