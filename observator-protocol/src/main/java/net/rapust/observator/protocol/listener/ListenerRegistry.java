package net.rapust.observator.protocol.listener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.rapust.observator.commons.logger.MasterLogger;
import net.rapust.observator.protocol.connection.Client;
import net.rapust.observator.protocol.connection.Server;
import net.rapust.observator.protocol.packet.Packet;

import java.lang.reflect.Method;

@Getter
@AllArgsConstructor
public class ListenerRegistry {

    private final Class<?> packet;
    private final Listener listener;
    private final Method method;

    public void invoke(Packet packet, Server.ClientHandler handler) {
        try {
            method.invoke(listener, packet, handler);
        } catch (Exception e) {
            MasterLogger.error("Ошибка в листенере", e);
        }
    }

    public void invoke(Packet packet, Client client) {
        try {
            method.invoke(listener, packet, client);
        } catch (Exception e) {
            MasterLogger.error("Ошибка в листенере", e);
        }
    }

}
