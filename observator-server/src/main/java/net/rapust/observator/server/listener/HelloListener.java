package net.rapust.observator.server.listener;

import net.rapust.observator.protocol.connection.impl.Server;
import net.rapust.observator.protocol.listener.Listen;
import net.rapust.observator.protocol.listener.Listener;
import net.rapust.observator.protocol.packet.impl.ErrorPacket;
import net.rapust.observator.protocol.packet.impl.HelloPacket;
import net.rapust.observator.protocol.packet.impl.PublicKeyPacket;
import net.rapust.observator.server.ServerAccessor;
import net.rapust.observator.server.client.ClientManager;
import net.rapust.observator.server.client.ConnectedClient;

import java.io.IOException;

public class HelloListener implements Listener {

    @Listen
    public void onHello(HelloPacket packet, Server.ClientHandler clientHandler) throws IOException {
        ClientManager manager = ServerAccessor.getInstance().getClientManager();
        ConnectedClient client = manager.getClientByName(packet.getName());

        int max = ServerAccessor.getInstance().getConfig().getMaxConnections();
        if (manager.getClients().size() >= max && max != -1) {
            clientHandler.stop();
            return;
        }

        if (client == null) {
            client = new ConnectedClient(clientHandler, packet);
            ServerAccessor.getInstance().getClientManager().addClient(client);
            ServerAccessor.getInstance().getMainGUI().update();
            client.sendPacket(new PublicKeyPacket(ServerAccessor.getInstance().getKeyPair().getPublic()));
        } else {
            clientHandler.write(new ErrorPacket(ErrorPacket.ErrorType.NAME_USED));
            clientHandler.stop();
        }
    }

}
